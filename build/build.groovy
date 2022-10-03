def buildCockpit(def phase = 'verify', def testFilter = '') {
  build(phase, testFilter, 'cockpit', '.ivy-engine/logs/*, **/target/*.iar');
}

def buildScreenshots(def phase = 'verify') {
  build(phase, '', 'screenshots', '.ivy-engine/logs/*');
}

def build(def phase = 'verify', def testFilter = '', def profile = 'cockpit', def artifacts = '') {
  withCredentials([string(credentialsId: 'github.ivy-team.token', variable: 'GITHUB_TOKEN')]) {
    def random = (new Random()).nextInt(10000000)
    def networkName = "build-" + random
    def seleniumName = "selenium-" + random
    def ivyName = "ivy-" + random
    def dbName = "db-" + random
    try {
      sh "docker network create ${networkName}"
      docker.image('mysql:5').withRun("-e \"MYSQL_ROOT_PASSWORD=1234\" -e \"MYSQL_DATABASE=test\" --name ${dbName} --network ${networkName}") {
        docker.image("selenium/standalone-firefox:4").withRun("-e START_XVFB=false --shm-size=2g --name ${seleniumName} --network ${networkName} ${dockerFileParams()}") {
          docker.build('maven', '-f build/Dockerfile .').inside("--name ${ivyName} --network ${networkName}") {
            maven cmd: "clean ${phase} -ntp " +
                "-Divy.engine.version='[10.0.0,]' " +
                "-Dwdm.gitHubTokenName=ivy-team " +
                "-Dwdm.gitHubTokenSecret=${env.GITHUB_TOKEN} " +
                "-Dmaven.test.failure.ignore=true " +
                "-Dengine.page.url=${params.engineSource} " +
                "-Dtest.engine.url=http://${ivyName}:8080 " +
                "-Dselenide.remote=http://${seleniumName}:4444/wd/hub " +
                "-Ddb.host=${dbName} " + 
                "-Dmaven.test.skip=false " +
                "-Dtest.filter=${env.testFilter} " +
                "-P${profile} "

            recordIssues tools: [mavenConsole()], unstableTotalAll: 1, filters: [
              excludeMessage('The system property test.engine.url is configured twice!*'),
              excludeMessage('JAR will be empty*')
            ]
            junit testDataPublishers: [[$class: 'AttachmentPublisher'], [$class: 'StabilityTestDataPublisher']], testResults: '**/target/surefire-reports/**/*.xml'
            archiveArtifacts "${artifacts}"
            archiveArtifacts artifacts: '**/target/selenide/reports/**/*', allowEmptyArchive: true
          }
        }
      }
    } finally {
      sh "docker network rm ${networkName}"
    }
  }
}

def dockerFileParams() {
  return '--shm-size 1g --hostname=ivy';
}

return this
