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
          maven cmd: "clean ${phase} -Dengine.page.url=${params.engineSource}  -Dsel.jup.output.folder=target/surefire-reports -Dsel.jup.screenshot.at.the.end.of.tests=true -Dsel.jup.screenshot.format=png"
        }
      }
      post {
        always {
          archiveArtifacts '**/target/*.iar'
          junit testDataPublishers: [[$class: 'AttachmentPublisher'], [$class: 'StabilityTestDataPublisher']], testResults: '**/target/surefire-reports/**/*.xml'
        }
      }
    }
  }
}
