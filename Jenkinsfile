pipeline {
  agent any

  environment {
    IMAGE_NAME = 'anouarelbarry/langly-backend'
    DB_USERNAME = 'postgres'
    DB_PASSWORD = 'postgres'
    DB_NAME = 'langly'
    JWT_SECRET = 'ci-jwt-secret-key-with-at-least-32-characters'
    RESEND_API_KEY = 'ci-resend-placeholder'
    APP_BASE_URL = 'http://localhost:8081'
    STRIPE_API_KEY = 'ci-stripe-placeholder'
    STRIPE_WEBHOOK_SECRET = 'ci-stripe-webhook-placeholder'
    UPLOAD_DIR = './uploads'
  }

  stages {
    stage('Build & test') {
      steps {
        bat '''
          > .env (
            echo DB_USERNAME=%DB_USERNAME%
            echo DB_PASSWORD=%DB_PASSWORD%
            echo DB_NAME=%DB_NAME%
            echo JWT_SECRET=%JWT_SECRET%
            echo RESEND_API_KEY=%RESEND_API_KEY%
            echo APP_BASE_URL=%APP_BASE_URL%
            echo STRIPE_API_KEY=%STRIPE_API_KEY%
            echo STRIPE_WEBHOOK_SECRET=%STRIPE_WEBHOOK_SECRET%
            echo UPLOAD_DIR=%UPLOAD_DIR%
          )

          mvnw clean package 
        '''
      }
    }

    stage('Build Docker Image') {
      steps {
        bat '''
          docker build -t %IMAGE_NAME%:%BUILD_NUMBER% .
        '''
      }
    }

    stage('Push to Docker Hub') {
      when {
        branch 'main'
      }
      steps {
        withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          bat '''
            docker login -u %DOCKER_USER% -p %DOCKER_PASS%
            docker push %IMAGE_NAME%:%BUILD_NUMBER%
            docker logout
          '''
        }
      }
    }
  }
}
