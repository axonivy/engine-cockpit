#!/bin/bash

mvn --batch-mode versions:set-property versions:commit -f maven-config/pom.xml -Dproperty=web.tester.version -DnewVersion=${2} -DallowSnapshots=true

