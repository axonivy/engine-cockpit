pipeline {
  agent {
    dockerfile {args '--shm-size 1g'}
  }
  
  options {
    buildDiscarder(logRotator(numToKeepStr: '60', artifactNumToKeepStr: '5'))
  }
  
  triggers {
    pollSCM '@hourly'
    cron '@midnight'
  }

  parameters {
    string(name: 'engineSource', defaultValue: 'http://zugprojenkins/job/ivy-core_product/job/master/lastSuccessfulBuild/', description: 'Engine page url')
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
