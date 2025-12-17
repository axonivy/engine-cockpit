#!/bin/bash
set -e

mvn --batch-mode versions:update-parent versions:commit -f maven-config/pom.xml -DparentVersion=${1} -DskipResolution=true -DallowSnapshots=true
