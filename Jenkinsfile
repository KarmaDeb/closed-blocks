pipeline {
    agent any

    tools {
        jdk 'Java21'
        maven 'Maven3.9'
    }

    environment {
        JAVA_HOME = "${tool 'Java21'}"
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
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
                        sh 'mvn clean deploy -s $MAVEN_SETTINGS -Pgpg-signing'
                    }
                }
            }
        }
    }
}