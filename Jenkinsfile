pipeline {
  agent {
     dockerfile{ 
        args '--shm-size 1g --hostname=ivy'
   }
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
    booleanParam(name: 'deployArtifacts', defaultValue: false, description: 'Deploy new version of cockpit and screenshots')
  }

  stages {
    stage('build') {
      steps {
        script {
          maven cmd: "clean verify -Dengine.page.url=${params.engineSource}  -Dsel.jup.output.folder=target/surefire-reports -Dsel.jup.screenshot.at.the.end.of.tests=true -Dsel.jup.screenshot.format=png"
        }
      }
      post {
        always {
          archiveArtifacts '**/target/*.iar'
          junit testDataPublishers: [[$class: 'AttachmentPublisher'], [$class: 'StabilityTestDataPublisher']], testResults: '**/target/surefire-reports/**/*.xml'
        }
      }
    }
    stage('verify') {
      when {
        expression {return !params.deployArtifacts}
      }
      steps {
        script {
          maven cmd: "-f image-validation/pom.xml clean verify -Dmaven.test.failure.ignore=true"
        }
      }
      post {
        always {
          archiveArtifacts '**/target/docu/**/*'
          archiveArtifacts '**/target/*.html'
          recordIssues filters: [includeType('screenshot-html-plugin:compare-images')], tools: [mavenConsole(name: 'Image')], unstableNewAll: 1,
          qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]]
        }
        success {
          dir ('engine-cockpit-selenium-test/target/surefire-reports') {
            deleteDir()
          }
        }
      }
    }
    stage('deploy') {
      when {
        allOf {
          //branch 'master'
          expression {return currentBuild.result == 'SUCCESS' || params.deployArtifacts}
        }
      }
      steps {
        script {
          maven cmd: "deploy -Dmaven.test.skip=true"
        }
      }
    }
    stage('cleanup') {
      steps {
        dir ('.ivy-engine') {
          deleteDir()
        }
      }
    }
  }

}
