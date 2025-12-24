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
                    configFileProvider([configFile(fileId: 'my-nexus-settings', variable: 'MAVEN_SETTINGS')]) {
                        sh 'mvn clean deploy -s $MAVEN_SETTINGS -Pgpg-signing'
                    }
                }
            }
        }
    }
}