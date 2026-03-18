pipeline {
  agent any

  options {
    skipDefaultCheckout(true)
  }

  environment {
    IMAGE_NAME = 'your-dockerhub-username/langly-backend'
    DB_NAME = 'langly'
    DB_USERNAME = 'postgres'
    DB_PASSWORD = 'postgres'
    CI_DB_PORT = '55432'
    CI_REDIS_PORT = '56379'
    JWT_SECRET = 'ci-jwt-secret-key-with-at-least-32-characters'
    RESEND_API_KEY = 'ci-resend-placeholder'
    APP_BASE_URL = 'http://localhost:8081'
    STRIPE_API_KEY = 'ci-stripe-placeholder'
    STRIPE_WEBHOOK_SECRET = 'ci-stripe-webhook-placeholder'
    FRONTEND_BASE_URL = 'http://localhost:4200'
    UPLOAD_DIR = './uploads'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build & Test') {
      steps {
        bat '''
          set -eu

          POSTGRES_CONTAINER="langly-ci-postgres-${BUILD_ID}"
          REDIS_CONTAINER="langly-ci-redis-${BUILD_ID}"
          CI_NETWORK="langly-ci-${BUILD_ID}"

          docker rm -f "$POSTGRES_CONTAINER" "$REDIS_CONTAINER" >/dev/null 2>&1 || true
          docker network rm "$CI_NETWORK" >/dev/null 2>&1 || true

          chmod +x ./mvnw
          docker network create "$CI_NETWORK"

          docker run -d --name "$POSTGRES_CONTAINER" \
            --network "$CI_NETWORK" \
            -p "${CI_DB_PORT}:5432" \
            -e POSTGRES_DB="$DB_NAME" \
            -e POSTGRES_USER="$DB_USERNAME" \
            -e POSTGRES_PASSWORD="$DB_PASSWORD" \
            postgres:16-alpine

          docker run -d --name "$REDIS_CONTAINER" \
            --network "$CI_NETWORK" \
            -p "${CI_REDIS_PORT}:6379" \
            redis:7-alpine

          attempts=0
          until docker exec "$POSTGRES_CONTAINER" pg_isready -U "$DB_USERNAME" -d "$DB_NAME" >/dev/null 2>&1
          do
            attempts=$((attempts + 1))
            if [ "$attempts" -ge 30 ]; then
              echo "PostgreSQL did not become ready in time."
              exit 1
            fi
            sleep 2
          done

          attempts=0
          until docker exec "$REDIS_CONTAINER" redis-cli ping >/dev/null 2>&1
          do
            attempts=$((attempts + 1))
            if [ "$attempts" -ge 30 ]; then
              echo "Redis did not become ready in time."
              exit 1
            fi
            sleep 1
          done

          export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:${CI_DB_PORT}/${DB_NAME}"
          export SPRING_DATA_REDIS_HOST="localhost"
          export SPRING_DATA_REDIS_PORT="${CI_REDIS_PORT}"

          ./mvnw clean verify
        '''
      }
    }

    stage('Docker Image Build') {
      steps {
        bat '''
          set -eu
          docker build -t "${IMAGE_NAME}:${BUILD_ID}" .
        '''
      }
    }

    stage('Push to Docker Hub') {
      steps {
        withCredentials([
          usernamePassword(
            credentialsId: 'docker-hub-creds',
            usernameVariable: 'DOCKER_HUB_USERNAME',
            passwordVariable: 'DOCKER_HUB_PASSWORD'
          )
        ]) {
          bat '''
            set -eu

            cleanup() {
              docker logout >/dev/null 2>&1 || true
            }

            trap cleanup EXIT

            echo "$DOCKER_HUB_PASSWORD" | docker login -u "$DOCKER_HUB_USERNAME" --password-stdin
            docker push "${IMAGE_NAME}:${BUILD_ID}"
          '''
        }
      }
    }
  }

  post {
    always {
      bat(
        returnStatus: true,
        script: '''
          POSTGRES_CONTAINER="langly-ci-postgres-${BUILD_ID}"
          REDIS_CONTAINER="langly-ci-redis-${BUILD_ID}"

          docker rm -f "$POSTGRES_CONTAINER" "$REDIS_CONTAINER" >/dev/null 2>&1 || true
        '''
      )
      bat(
        returnStatus: true,
        script: '''
          CI_NETWORK="langly-ci-${BUILD_ID}"

          docker network rm "$CI_NETWORK" >/dev/null 2>&1 || true
        '''
      )
      bat(
        returnStatus: true,
        script: '''
          docker image rm -f "${IMAGE_NAME}:${BUILD_ID}" >/dev/null 2>&1 || true
        '''
      )
    }
  }
}
