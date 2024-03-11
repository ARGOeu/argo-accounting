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
                    args '-v $HOME/.m2:/root/.m2 -v /var/run/docker.sock:/var/run/docker.sock -u root:root'
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
                step( [ $class: 'JacocoPublisher' ] )
            }
            post {
                always {
                    cleanWs()
                }
            }
        }
        stage ('Deploy Docs') {
            // run deployment of docs only when merging to devel
            // when {
            //     branch 'devel'
            // }
            agent {
                docker {
                    image 'node:18-buster'
                }
            }
            steps {
                echo 'Publish argo-accounting docs...'
                sh '''
                    cd $WORKSPACE/$PROJECT_DIR
                    cd website
                    npm install
                '''
                sshagent (credentials: ['jenkins-master']) {
                    sh '''
                        cd $WORKSPACE/$PROJECT_DIR/website
                        mkdir ~/.ssh && ssh-keyscan -H github.com > ~/.ssh/known_hosts
                        git config --global user.email ${GH_EMAIL}
                        git config --global user.name ${GH_USER}
                        echo ${GH_USER}
                        GIT_USER=${GH_USER} USE_SSH=true npm run deploy
                    '''
                }
            }
        } 
    }
    post {
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
