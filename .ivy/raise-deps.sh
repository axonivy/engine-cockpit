#!/bin/bash
set -e

newVersion=${1}
property=ivy.bom.version

mvn --batch-mode versions:set-property versions:commit  -f maven-config/pom.xml -Dproperty=${property} -DnewVersion=${newVersion}  -DallowSnapshots=true
