pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'jdk8'
        docker 'docker'
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
		stage ('Dockerize') {
			steps {
				checkout scm
				script {
					docker.withRegistry('https://mirceah.jfrog.io/artifactory/jwt-auth/', 'artifactory-id') {
	                    def image = docker.build("jwt-auth-api:${env.BUILD_ID}")
	                    image.push()
	                }
				}
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

        stage ('Deploy') {
            steps {
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