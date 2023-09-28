def buildIntegration(def mvnArgs = '') {
  withCredentials([string(credentialsId: 'github.ivy-team.token', variable: 'GITHUB_TOKEN')]) {
    def random = (new Random()).nextInt(10000000)
    def networkName = "build-" + random
    def seleniumName = "selenium-" + random
    def ivyName = "ivy-" + random
    def dbName = "db-" + random
    try {
      sh "docker network create ${networkName}"
      docker.image('mysql:5').withRun("-e \"MYSQL_ROOT_PASSWORD=1234\" -e \"MYSQL_DATABASE=test\" --name ${dbName} --network ${networkName}") {
        docker.image("selenium/standalone-firefox:4").withRun("-e START_XVFB=false --shm-size=2g --name ${seleniumName} --network ${networkName} --shm-size 1g --hostname=ivy") {
          docker.build('maven', '-f build/Dockerfile .').inside("--name ${ivyName} --network ${networkName}") {
            def mvnBuildArgs = "-Dwdm.gitHubTokenName=ivy-team " +
                "-Dwdm.gitHubTokenSecret=${env.GITHUB_TOKEN} " +
                "-Dengine.page.url=${params.engineSource} " +
                "-Dtest.engine.url=http://${ivyName}:8080 " +
                "-Dselenide.remote=http://${seleniumName}:4444/wd/hub " +
                "-Ddb.host=${dbName} " + 
                mvnArgs
            mvnBuild(mvnBuildArgs);

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

def build() {
  docker.build('maven', '-f build/Dockerfile .').inside("") {
    mvnBuild("-Pcockpit -Dengine.page.url=${params.engineSource}");
  }
}

def mvnBuild(def mvnArgs = '') {
  def phase = isReleaseOrMasterBranch() ? 'deploy' : 'verify'
  maven cmd: "clean ${phase} -ntp -Divy.engine.version='[10.0.0,]' -Dmaven.test.skip=false " + mvnArgs
  
  recordIssues tools: [mavenConsole()], unstableTotalAll: 1, filters: [
    excludeMessage('The system property test.engine.url is configured twice!*'),
    excludeMessage('JAR will be empty*')
  ]
  junit testDataPublishers: [[$class: 'AttachmentPublisher'], [$class: 'StabilityTestDataPublisher']], testResults: '**/target/surefire-reports/**/*.xml'
}

def isReleaseOrMasterBranch() {
  return env.BRANCH_NAME == 'master' || env.BRANCH_NAME.startsWith('release/') 
}

return this
