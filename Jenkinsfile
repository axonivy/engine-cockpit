pipeline {
  agent any
  
  options {
    buildDiscarder(logRotator(numToKeepStr: '60', artifactNumToKeepStr: '5'))
  }
  
  triggers {
    pollSCM '@hourly'
    cron '@midnight'
  }

  parameters {
    string(name: 'engineSource', defaultValue: 'https://jenkins.ivyteam.io/job/ivy-core_product/job/master/lastSuccessfulBuild/', description: 'Engine page url')
    booleanParam(name: 'deployArtifacts', defaultValue: false, description: 'Deploy new version of cockpit and screenshots')
  }

  environment {
    imgSimilarity = getImageSimilarity()
    dockerfileParams = '--shm-size 1g --hostname=ivy'
  }

  stages {
    stage('build') {
      steps {
        script {
          docker.image('mysql:5').withRun('-e "MYSQL_ROOT_PASSWORD=1234" -e "MYSQL_DATABASE=test"') { container ->
            docker.build('maven-build').inside("--link ${container.id}:db ${dockerfileParams}") {
              //'MySql', 'jdbc:mysql://db:3306', 'root', '1234'

              maven cmd: 'clean verify ' +
                      '-Dengine.page.url=' + params.engineSource + ' ' + 
                      '-Dsel.jup.output.folder=target/surefire-reports ' + 
                      '-Dsel.jup.screenshot.at.the.end.of.tests=true ' + 
                      '-Dsel.jup.screenshot.format=png'
      
              junit testDataPublishers: [[$class: 'AttachmentPublisher'], [$class: 'StabilityTestDataPublisher']], testResults: '**/target/surefire-reports/**/*.xml'
              archiveArtifacts '**/target/*.iar'
            }
          }
        }
      }
    }
    stage('verify') {
      when {
        expression {return !params.deployArtifacts}
      }
      steps {
        script {
          docker.build('maven-build').inside("${dockerfileParams}") {
            maven cmd: 'clean verify ' +
                    '-f image-validation/pom.xml ' + 
                    '-Dmaven.test.failure.ignore=true ' + 
                    '-Dimg.similarity=' + env.imgSimilarity

            archiveArtifacts '**/target/docu/**/*'
            archiveArtifacts '**/target/*.html'
            recordIssues filters: [includeType('screenshot-html-plugin:compare-images')], tools: [mavenConsole(name: 'Image')], unstableNewAll: 1,
            qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]]
          }
        }
      }
      post {
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
          branch 'master'
          expression {return currentBuild.currentResult == 'SUCCESS' || params.deployArtifacts}
        }
      }
      steps {
        script {
          docker.build('maven-build').inside("${dockerfileParams}") {
            maven cmd: "deploy -Dmaven.test.skip=true"
          }
        }
      }
    }
    stage('cleanup') {
      when {
        expression {return currentBuild.currentResult == 'SUCCESS'}
      }
      steps {
        dir ('.ivy-engine') {
          deleteDir()
        }
      }
    }
  }
}

def getImageSimilarity() {
  if (env.BRANCH_NAME == 'master') {
    return '98'
  } else {
    return '95'
  }
}
