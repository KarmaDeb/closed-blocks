pipeline {
    agent any

    tools {
        jdk 'Java21'
        maven 'Maven3.9'
    }

    stages {
        stage('Deploy') {
            steps {
                withCredentials([
                    string(credentialsId: 'nexus-creds', variable: 'NEXUS_PASSWORD'),
                    string(credentialsId: 'proxy-creds', variable: 'PROXY_PASSWORD'),
                    string(credentialsId: 'gpg-creds', variable: 'GPG_PASSPHRASE')
                ]) {
                    configFileProvider([configFile(fileId: '214cc31c-3f09-41a6-9104-2d556ba49f95', variable: 'MAVEN_SETTINGS')]) {
                        sh '''
                        export NEXUS_PASSPHRASE="${NEXUS_PASSPHRASE}"
                        export GPG_PASSPHRASE="${GPG_PASSPHRASE}"

                        mvn clean deploy -s $MAVEN_SETTINGS -Pgpg-signing
                        '''
                    }
                }
            }
        }
    }
}