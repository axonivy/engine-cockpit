# Axon Ivy Engine Cockpit

This is the repository of the Axon Ivy Engine Cockpit.

## Build

[Jenkins Pipeline](https://jenkins.ivyteam.io/job/engine-cockpit/job/master/)

If you made some UI changes, you may need to trigger the pipeline manually with the `forceDeployScreenshots` parameter checked.

## Run local tests

Requirements for some test run local in Designer:

- Apps: Create apps 'test' and 'test-ad'
- Configuration: Copy engine-cockpit-selenium-test/engine-configuration yaml files to your Designer configuration folder

Some db tests are not runs without change the connections
