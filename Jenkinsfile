pipeline {
    agent any

    tools {
        jdk 'Java21'
        maven 'Maven3.9'
    }

    stages {
        stage('Initialize') {
            steps {
                script {
                    // This forces Jenkins to resolve the tool path and set it to JAVA_HOME
                    def jdkPath = tool 'Java21'
                    env.JAVA_HOME = "${jdkPath}"
                    env.PATH = "${jdkPath}/bin:${env.PATH}"
                }
            }
        }

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