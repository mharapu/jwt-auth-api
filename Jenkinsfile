pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'jdk8'
    }
    stages {
        stage ('Initialize') {
            steps {
                script {
	                dir('git-source-code') {
		                git( url: "https://github.com/mharapu/jwt-auth-api.git",
		                    branch: "release"
		                  )
	                    def tag = sh(returnStdout: true, script: "git for-each-ref --count=1 --sort=-taggerdate --format '%(tag)' refs/tags")
	                    echo tag
	                }
                }
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
                checkout scm
                script {
                    docker.withRegistry('https://mirceah.jfrog.io/artifactory/jwt-auth/', 'artifactory-id') {
                        def image = docker.build("jwt-auth-api:${env.TAG_NAME}")
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
                                      appPath: 'target/jwt-auth-api-0.0.1-SNAPSHOT.jar'
                                    ]
                                )
            }
        }
    }
}