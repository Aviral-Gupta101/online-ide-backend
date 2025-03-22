pipeline {
    agent any

    environment {
        DOCKER_HOST='tcp://docker:2376'
        DOCKER_CERT_PATH='/certs/client'
        DOCKER_TLS_VERIFY=1
    }

    stages {

		stage("Checkout"){
			steps{
				checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/Aviral-Gupta101/online-ide-backend.git']])
			}
		}
      
        stage('Build') {

            steps {
              sh "./gradlew build -x test"
            }
        }

        stage('Test') {

            steps{
                sh './gradlew test'
            }

            post{
                always{
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: false,
                        icon: '',
                        keepAll: false,
                        reportDir: './build/reports/tests/test',
                        reportFiles: 'index.html',
                        reportName: 'Test Report',
                        reportTitles: 'Test Report',
                        useWrapperFileDirectly: true
                    ])                    
                }
            }
        }
    }
}

