def buildCockpit(def phase = 'verify', def testFilter = '') {
  def mvnArgs = "-Dtest.filter=${testFilter} -Dmaven.test.failure.ignore=true "
  build(phase, mvnArgs, '-Pcockpit -Pintegration');
  
  archiveArtifacts '**/target/*.iar'
}

def buildScreenshots(def phase = 'verify', def imgRefBranch = 'master', def imgSimilarity= '97') {
  build(phase, "", '-Pscreenshots');

  archiveArtifacts '**/target/docu/**/*'
}

def build(def phase = 'verify', def mvnArgs = '', def profiles = '-Pcockpit') {
  withCredentials([string(credentialsId: 'github.ivy-team.token', variable: 'GITHUB_TOKEN')]) {
    def random = (new Random()).nextInt(10000000)
    def networkName = "build-" + random
    def seleniumName = "selenium-" + random
    def ivyName = "ivy-" + random
    def dbName = "db-" + random
    try {
      sh "docker network create ${networkName}"
      docker.image('mysql:5').withRun("-e \"MYSQL_ROOT_PASSWORD=1234\" -e \"MYSQL_DATABASE=test\" --name ${dbName} --network ${networkName}") {
        docker.image("selenium/standalone-firefox:4.10").withRun("-e START_XVFB=false --shm-size=2g --name ${seleniumName} --network ${networkName} ${dockerFileParams()}") {
          docker.build('maven', '-f build/Dockerfile .').inside("--name ${ivyName} --network ${networkName}") {
            def mvnBuildArgs = "-ntp " +
                "-Divy.engine.version='[10.0.0,10.1.0)' " +
                "-Dwdm.gitHubTokenName=ivy-team " +
                "-Dwdm.gitHubTokenSecret=${env.GITHUB_TOKEN} " +
                "-Dengine.page.url=${params.engineSource} " +
                "-Dtest.engine.url=http://${ivyName}:8080 " +
                "-Dselenide.remote=http://${seleniumName}:4444/wd/hub " +
                "-Ddb.host=${dbName} " + 
                "-Dmaven.test.skip=false " +
                profiles
                mvnArgs;

            buildMvn(phase, mvnBuildArgs);
          }
        }
      }
    } finally {
      sh "docker network rm ${networkName}"
    }
  }
}

def integrationTestOnWindows() {
  def mvnBuildArgs = "-ntp " +
      "-Divy.engine.version=[10.0.0,10.1.0) " +
      "-Dengine.page.url=${params.engineSource} " +
      "-Dtest.filter=WebTestOs " +
      "-Dmaven.test.failure.ignore=true "+
      "-Dmaven.test.skip=false " +
      "-Dskip.test=true " +
      "-Dsun.io.useCanonCaches=false " +
      "-Pintegration";
  buildMvn("verify", mvnBuildArgs);
}

def buildMvn(def phase, def mvnArgs) {
  maven cmd: "clean ${phase} " + mvnArgs;
  junit testDataPublishers: [[$class: 'AttachmentPublisher'], [$class: 'StabilityTestDataPublisher']], testResults: '**/target/surefire-reports/**/*.xml'
  archiveArtifacts '.ivy-engine/logs/*'
  archiveArtifacts artifacts: '**/target/selenide/reports/**/*', allowEmptyArchive: true
}

def dockerFileParams() {
  return '--shm-size 1g --hostname=ivy';
}

def recordMavenIssues() {
  recordIssues tools: [mavenConsole()], qualityGates: [[threshold: 1, type: 'TOTAL']], filters: [
    excludeMessage('The system property test.engine.url is configured twice!*'),
    excludeMessage('JAR will be empty*')
  ]
}

return this
