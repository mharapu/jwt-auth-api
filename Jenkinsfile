pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'jdk8'
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }
        stage ('Build') {
            steps {
                sh 'mvn -Dmaven.test.failure.ignore=true package'
            }
        }

        stage ('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage ('Release deploy') {
            when { branch 'release'}
            steps {
                environment {
				TAG = sh returnStdout:true, script: """
						git fetch --tags
                        tag=`git for-each-ref --count=1 --sort=-taggerdate --format '%(refname:strip=2)' refs/tags`
                        if [[ "$tag" == "" ]]
                        then
                            tag="V0.0.1"
                        else
                            IFS="."
                            read -a tagArr <<< $tag
                            ((newVersion=${tagArr[2]}+1))
                            IFS=";"
                            tag=${tagArr[0]}'.'${tagArr[1]}'.'$newVersion
                        fi
                        echo $tag
				"""
                }
                script {
                        sh """
	                        git tag ${TAG}
	                        sed -e "10,//{s/<version>.*<\/version>/<version>${TAG}<\/version>/;}" pom.xml > pom.xml.new
	                        mv pom.xml.new pom.xml
	                        git commit -a -m "Set version to ${TAG}"
	                        git push origin ${TAG}
	                        git push --set-upstream origin release
	                        git checkout master
	                        git merge origin/release
	                        git push origin master
                        """

                    docker.withRegistry('https://mirceah.jfrog.io/artifactory/jwt-auth/', 'artifactory-id') {
                                            def image = docker.build("jwt-auth-api:${TAG}")
                                            image.push()
                                        }
                }
                pushToCloudFoundry(
                                  target: 'https://api.cap.explore.suse.dev',
                                  organization: 'mircea_harapu_gmail_com',
                                  cloudSpace: 'dev',
                                  credentialsId: 'cf_mircea',
                                   manifestChoice: [
                                      value: 'jenkinsConfig',
                                      appName: 'jwt-auth-api',
                                      memory: '1024',
                                      instances: '1',
                                      appPath: 'target/jwt-auth-api-${TAG}.jar'
                                    ]
                                )
            }
        }
    }
}