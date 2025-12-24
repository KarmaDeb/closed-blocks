pipeline {
    agent any

    tools {
        jdk 'Java21'
        maven 'Maven3.9'
    }

    environment {
        GPG_PASSPHRASE = credentials('GPG_PASSPHRASE')
        NEXUS_PASSPHRASE = credentials('NEXUS_PASSPHRASE')
        PROXY_PASSPHRASE = credentials('PROXY_PASSPHRASE')
        GPG_KEY_CONTENT = credentials('GPG_PRIVATE_KEY')
    }

    stages {
        stage('Deploy') {
            steps {
                configFileProvider([configFile(fileId: '214cc31c-3f09-41a6-9104-2d556ba49f95', variable: 'MAVEN_SETTINGS')]) {
                    sh '''
                    echo "$GPG_KEY_CONTENT" | gpg --batch --import
                    mvn clean deploy -s $MAVEN_SETTINGS -Pgpg-signing
                    '''
                }
            }
        }
    }
}