#!/bin/bash

mvn --batch-mode -f maven-config/pom.xml versions:set-property versions:commit -Dproperty=revision -DnewVersion=${1}
