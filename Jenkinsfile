pipeline {
  agent any

  environment {
    IMAGE_NAME = 'anouarelbarry/langly-backend'
  }

  stages {
    stage('Build & test') {
      steps {
        bat '''
          mvnw clean package -DskipTests
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
