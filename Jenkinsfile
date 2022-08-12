pipeline {
  agent any
  
  options {
    buildDiscarder(logRotator(numToKeepStr: '60', artifactNumToKeepStr: '5'))
  }
  
  triggers {
    cron '@midnight'
  }

  parameters {
    string(name: 'engineSource', defaultValue: 'https://jenkins.ivyteam.io/job/core_product/job/master/lastSuccessfulBuild/', description: 'Engine page url')
    booleanParam(name: 'forceDeployScreenshots', defaultValue: false, description: 'Force deploy new screenshots')
    booleanParam(name: 'skipScreenshots', defaultValue: false, description: 'Skip screenshot test (flag will be ignored on master)')
    string(name: 'testFilter', defaultValue: 'WebTest*.java', description: 'Change to only run tests of the matching classes (flag will be gnored on master)')
  }

  environment {
    imgSimilarity = getImageSimilarity()
    dockerfileParams = '--shm-size 1g --hostname=ivy'
    skipScreenshots = getSkipScreenshots()
    testFilter = getTestFilter()
  }
  stages {
    stage('check editorconfig') {
      steps {
        script {          
          docker.image('mstruebing/editorconfig-checker').inside {
            warnError('There are some editor errors') {
              sh 'ec -no-color'
            } 
          }
        }
      }
    }
    stage('build') {
      steps {
        script {
          withCredentials([string(credentialsId: 'github.ivy-team.token', variable: 'GITHUB_TOKEN')]) {
            def random = (new Random()).nextInt(10000000)
            def networkName = "build-" + random
            def seleniumName = "selenium-" + random
            def ivyName = "ivy-" + random
            def dbName = "db-" + random
            try {
              sh "docker network create ${networkName}"
              docker.image('mysql:5').withRun("-e \"MYSQL_ROOT_PASSWORD=1234\" -e \"MYSQL_DATABASE=test\" --name ${dbName} --network ${networkName}") {
                docker.image("selenium/standalone-firefox:3").withRun("-e START_XVFB=false --shm-size=2g --name ${seleniumName} --network ${networkName} ${dockerfileParams}") {
                  docker.build('maven').inside("--name ${ivyName} --network ${networkName}") {
                    maven cmd: "clean verify " +
                        "-Dwdm.gitHubTokenName=ivy-team " +
                        "-Dwdm.gitHubTokenSecret=${env.GITHUB_TOKEN} " +
                        "-Dmaven.test.failure.ignore=true " +
                        "-Dengine.page.url=${params.engineSource} " +
                        "-Dtest.engine.url=http://${ivyName}:8080 " +
                        "-Dselenide.remote=http://${seleniumName}:4444/wd/hub " +
                        "-Ddb.host=${dbName} " + 
                        "-Dmaven.test.skip=false " +
                        "-Dtest.filter=${env.testFilter} " +
                        "-Dskip.screenshots=${env.skipScreenshots} "

                    checkVersions recordIssue: false
                    checkVersions cmd: '-f maven-config/pom.xml'
                    recordIssues tools: [mavenConsole()], unstableTotalAll: 1, filters: [
                      excludeMessage('The system property test.engine.url is configured twice!*'),
                      excludeMessage('JAR will be empty*')
                    ]
                    junit testDataPublishers: [[$class: 'AttachmentPublisher'], [$class: 'StabilityTestDataPublisher']], testResults: '**/target/surefire-reports/**/*.xml'
                    archiveArtifacts '**/target/*.iar'
                    archiveArtifacts '.ivy-engine/logs/*'
                    archiveArtifacts artifacts: '**/target/selenide/reports/**/*', allowEmptyArchive: true                    
                  }
                }
              }
            } finally {
              sh "docker network rm ${networkName}"
            }
          }
        }
      }
    }
    stage('verify') {
      when {
        expression {return !params.forceDeployScreenshots && !env.skipScreenshots.toBoolean()}
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
            recordIssues filters: [includeType('screenshot-html-plugin:compare-images')], tools: [mavenConsole(name: 'Image', id: 'image-warnings')], unstableNewAll: 1,
            qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]]
            currentBuild.description = "<a href=${BUILD_URL}artifact/engine-cockpit-selenium-test/target/newscreenshots.html>&raquo; Screenshots</a>"
          }          
        }
      }
    }

    stage('deploy') {
      when {
        expression {
          isReleaseOrMasterBranch()
        }
      }
      steps {
        script {
          docker.image('maven:3.6.3-jdk-11').inside("${dockerfileParams}") {
            maven cmd: "deploy -Dskip.screenshots=${env.skipScreenshots}"
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
    return '97'
  }
  return '95'
}

def getSkipScreenshots() {
  if (env.BRANCH_NAME == 'master') {
    return false;
  }
  return params.skipScreenshots
}

def getTestFilter() {
  if (env.BRANCH_NAME == 'master') {
    return 'WebTest*.java';
  }
  return params.testFilter
}

def isReleaseOrMasterBranch() {
  return env.BRANCH_NAME == 'master' || env.BRANCH_NAME.startsWith('release/') 
}
