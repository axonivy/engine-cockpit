#!/bin/bash

mvn --batch-mode versions:set-property versions:commit -f maven-config/pom.xml -Dproperty=project.build.plugin.version -DnewVersion=${1} -DallowSnapshots=true
