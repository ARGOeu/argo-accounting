pipeline {
    agent none
    options {
        checkoutToSubdirectory('accounting.system')
        newContainerPerStage()
    }
    environment {
        PROJECT_DIR='accounting.system'
        GH_USER = 'newgrnetci'
        GH_EMAIL = '<argo@grnet.gr>'
    }
    stages {
        stage('Accounting System API Packaging & Testing') {
            agent {
                docker {
                    image 'argo.registry:5000/epel-7-java11-mvn384'
                    args '-v /var/run/docker.sock:/var/run/docker.sock -u root:root'
                }
            }
            steps {
                echo 'Accounting System API Packaging & Testing'
                sh """
                cd ${WORKSPACE}/${PROJECT_DIR}
                mvn clean package -Dquarkus.package.type=uber-jar
                """
                junit '**/target/surefire-reports/*.xml'
                archiveArtifacts artifacts: '**/target/*.jar'
            }
            post {
                always {
                    cleanWs()
                }
            }
        }
    }
    post {
        always {
            cleanWs()
        }
        success {
            script{
                if ( env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'devel' ) {
                    slackSend( message: ":rocket: New version for <$BUILD_URL|$PROJECT_DIR>:$BRANCH_NAME Job: $JOB_NAME !")
                }
            }
        }
        failure {
            script{
                if ( env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'devel' ) {
                    slackSend( message: ":rain_cloud: Build Failed for <$BUILD_URL|$PROJECT_DIR>:$BRANCH_NAME Job: $JOB_NAME")
                }
            }
        }
    }
}