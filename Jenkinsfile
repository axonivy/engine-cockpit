pipeline {
  agent {
    dockerfile true
  }
  
  triggers {
    pollSCM '@hourly'
    cron '@midnight'
  }

  parameters {
    string(name: 'engineSource', defaultValue: 'http://zugprobldmas/job/Trunk_All/lastSuccessfulBuild/', description: 'Engine page url')
  }

  stages {
    stage('build') {
      steps {
        script {
          def workspace = pwd()
          maven cmd: "clean deploy -e -Dengine.page.url=${params.engineSource}"
        }
      }
      post {
        always {
          archiveArtifacts '**/target/*.iar'
        }
      }
    }
  }
}
