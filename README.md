# Axon Ivy Engine Cockpit

[![translation-status](https://hosted.weblate.org/widget/axonivy/doc/svg-badge.svg)](https://hosted.weblate.org/engage/axonivy/)

This is the repository of the Axon Ivy Engine Cockpit.

## Run local tests

To run selenium tests in VS Code, some VM arguments need to be set. The arguments can be changed in `.vscode/settings.json`.
You may need to change engine url. By default, the following is set:
`-Dtest.engine.url=http://localhost:8080/~Developer-engine-cockpit`

Requirements for some test run local in VS Code:

- Apps: Create apps 'test' and 'test-ad'
- Configuration: Copy engine-cockpit-selenium-test/engine-configuration yaml files to your Engine configuration folder

Some db tests are not runs without change the connections
