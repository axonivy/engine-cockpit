#!/bin/bash

mvn -f maven-config/pom.xml versions:set-property versions:commit -Dproperty=revision -DnewVersion=${1}
