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

        stage("Deploy"){

            environment { 
                SSH_KEY_CREDENTIALS = 'c11a8892-7246-4fc4-a737-e65ebe770f08'
            }

            steps {
                sshagent(credentials: [SSH_KEY_CREDENTIALS]) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no root@185.199.52.100 "
                        
                        if [[ ! -d /deployment/online-ide-backend ]]; then
                            echo 'Directory not found. Cloning the repository...'
                            git clone https://github.com/Aviral-Gupta101/online-ide-backend.git /deployment/online-ide-backend
                        else
                            echo 'Directory already exists. Skipping clone.'
                        fi

                        cd /deployment/online-ide-backend
                        chmod +x deploy.sh
                        sh deploy.sh
                        "
                    '''
                }
            }
            
        }
    }
}

