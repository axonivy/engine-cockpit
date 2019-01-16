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
          def phase = env.BRANCH_NAME == 'master' ? 'deploy' : 'verify'
          maven cmd: "clean "${phase} -Dengine.page.url=${params.engineSource}"
        }
      }
      post {
        always {
          archiveArtifacts '**/target/*.iar'
          junit '**/target/surefire-reports/**/*.xml'
        }
      }
    }
  }
}
