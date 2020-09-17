pipeline {
    agent any
    options {
        skipDefaultCheckout()
    }
    tools {
        maven 'maven'
        jdk 'jdk8'
    }
    stages {
        stage ('Initialize') {
            steps {
                git branch: 'master',
                    credentialsId: 'github',
                    url: 'https://github.com/mharapu/jwt-auth-api.git'
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
            environment {
                TAG = sh (returnStdout:true, script: '''
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
                        ''').trim()
                DOCKER_LOGIN = credentials('artifactory-id')
            }
            steps {
	            timeout(time: 60, unit: 'SECONDS') {
	                script {
	                    try {
	                            sh """
	                                git tag ${TAG}
	                                sed -e "10,//{s/<version>.*<\\/version>/<version>${TAG}<\\/version>/;}" pom.xml > pom.xml.new
	                                mv pom.xml.new pom.xml
	                                git commit -a -m "Set version to ${TAG}"
	                                git push --set-upstream https://github.com/mharapu/jwt-auth-api.git ${TAG}
	                                git push --set-upstream https://github.com/mharapu/jwt-auth-api.git release
	                                git checkout master
	                                git merge https://github.com/mharapu/jwt-auth-api.git/release
	                                git push --set-upstream https://github.com/mharapu/jwt-auth-api.git master
	                            """
	                    } catch(err) {
	                        echo "Caught: ${err}"
	                    }
	                    sh """
	                        docker login -u ${DOCKER_LOGIN_USR} --password-stdin https://mirceah.jfrog.io/artifactory/jwt-auth
	                        echo ${DOCKER_LOGIN_PSW}
	                        docker build . -t jwt-auth-api:${TAG}
	                        docker push jwt-auth-api:${TAG}
	                    """
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