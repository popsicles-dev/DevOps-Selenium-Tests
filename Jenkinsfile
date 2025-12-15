pipeline {
  agent any
  parameters {
    string(name: 'BASE_URL', defaultValue: 'http://16.171.224.162:8081', description: 'Base URL for the web app to test')
  }
  stages {
    stage('Build test image') {
      steps {
        sh 'docker build -t wemech-tests -f Dockerfile.tests .' 
      }
    }
    stage('Run tests') {
      steps {
        sh 'mkdir -p test-results'
        sh 'docker run --rm -e BASE_URL=${params.BASE_URL} -v $PWD/test-results:/app/test-results wemech-tests || true'
        junit 'test-results/results.xml'
      }
    }
    stage('Build Java test image') {
      steps {
        sh 'docker build -t wemech-tests-java -f Dockerfile.tests-java .' 
      }
    }
    stage('Run Java tests') {
      steps {
        sh 'mkdir -p test-results/java'
        sh 'docker run --rm -e BASE_URL=${params.BASE_URL} -v $PWD/test-results/java:/app/test-results wemech-tests-java || true'
        junit 'test-results/java/**.xml'
      }
    }
  }
  post {
    always {
      archiveArtifacts artifacts: 'test-results/**', allowEmptyArchive: true
    }
  }
}
