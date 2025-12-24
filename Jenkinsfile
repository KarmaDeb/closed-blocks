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
                        script {
                            def javaHome = tool 'Java21'

                            sh """
                                export JAVA_HOME=${javaHome}
                                export PATH=\$JAVA_HOME/bin:\$PATH

                                # Verify the version in the log to be 100% sure
                                java -version

                                mvn clean deploy -s $MAVEN_SETTINGS -Pgpg-signing
                            """
                        }
                    }
                }
            }
        }
    }
}