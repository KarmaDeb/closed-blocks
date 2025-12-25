pipeline {
    agent any

    tools {
        jdk 'Java21'
        maven 'Maven3.9'
    }

    environment {
        MAVEN_GPG_PASSPHRASE = credentials('GPG_PASSPHRASE')
        NEXUS_PASSPHRASE = credentials('NEXUS_PASSPHRASE')
        GPG_KEY_PATH = credentials('GPG_KEY_FILE')
    }

    stages {
        stage('Deploy') {
            steps {
                configFileProvider([configFile(fileId: '214cc31c-3f09-41a6-9104-2d556ba49f95', variable: 'MAVEN_SETTINGS')]) {
                    sh '''
                    gpg --batch --import "${GPG_KEY_PATH}"
                    mvn clean deploy -s $MAVEN_SETTINGS -Pgpg-signing
                    '''
                }
            }
        }
    }
    post {
        success {
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
        }
    }
}