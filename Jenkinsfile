pipeline {
  agent any
  
  options {
    buildDiscarder(logRotator(numToKeepStr: '60', artifactNumToKeepStr: '5'))
  }
  
  triggers {
    cron '@midnight'
    bitbucketPush()
  }

  parameters {
    string(name: 'engineSource', defaultValue: 'https://jenkins.ivyteam.io/job/ivy-core_product/job/release%252F8.0/lastSuccessfulBuild/', description: 'Engine page url')
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
          withCredentials([string(credentialsId: 'github.ivy-team.token', variable: 'GITHUB_TOKEN')]) {
            docker.image('mysql:5').withRun('-e "MYSQL_ROOT_PASSWORD=1234" -e "MYSQL_DATABASE=test"') { container ->
              docker.image('axonivy/build-container:web-1.0').inside("--link ${container.id}:db ${dockerfileParams}") {
                //'MySql', 'jdbc:mysql://db:3306', 'root', '1234'

                maven cmd: "clean verify " +
                        "-Dwdm.gitHubTokenName=ivy-team " +
                        "-Dwdm.gitHubTokenSecret=${env.GITHUB_TOKEN} " +
                        "-Dengine.page.url=" + params.engineSource

                junit testDataPublishers: [[$class: 'AttachmentPublisher'], [$class: 'StabilityTestDataPublisher']], testResults: '**/target/surefire-reports/**/*.xml'
                archiveArtifacts '**/target/*.iar'
                archiveArtifacts '.ivy-engine/logs/*'
                archiveArtifacts artifacts: '**/target/selenide/reports/**/*', allowEmptyArchive: true
                checkVersions cmd: '-f maven-config/pom.xml', onlyProjectBuildPluginWithVersion: "8", additionalVersionArgs: "-DallowSnapshots=true"
              }
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
          docker.image('maven:3.6.3-jdk-11').inside("${dockerfileParams}") {
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
    }
    stage('deploy') {
      when {
        allOf {
          branch 'release/8.0'
          expression {return currentBuild.currentResult == 'SUCCESS' || params.deployArtifacts}
        }
      }
      steps {
        script {
          docker.image('maven:3.6.3-jdk-11').inside("${dockerfileParams}") {
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
