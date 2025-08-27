pipeline {
    agent any

    tools {
        maven 'Maven 3.8.1' // Make sure this matches Jenkins tool name
        jdk 'JDK 11'
    }

    stages {
        stage('Checkout Code') {
            steps {
                echo 'Checking out code from GitHub...'
                checkout scm
            }
        }

        stage('Build Project') {
            steps {
                echo 'Building project with Maven...'
                bat 'mvn clean compile'
            }
        }

        stage('Run Tests with TestNG') {
            steps {
                echo 'Running Playwright + TestNG tests...'
                bat 'mvn test'
            }
        }
    }

    post {
        always {
            echo 'Cleaning up...'
        }
        success {
            echo 'Build and tests completed successfully!'
        }
        failure {
            echo 'Build or tests failed. Check the console output for more details.'
        }
    }
}
