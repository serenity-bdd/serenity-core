pipeline {
    agent any

    tools {
        maven 'Gradle 3.4'
        jdk 'jdk8'
    }

    stages {
        stage('Test') {
            steps {
                sh './gradlew clean test'
                sh './gradlew integrationTests'
                sh './gradlew browserTests'
            }
            post {
                always {
                    junit 'target/surefire-reports/**/*.xml, **/build/reports/integration-tests/TEST-*.xml'
                }
            }
        }
    }
}