#!/bin/bash

mvn -B org.codehaus.mojo:versions-maven-plugin:2.13.0:set -DnewVersion=${1} -DprocessAllModules
mvn -B org.codehaus.mojo:versions-maven-plugin:2.13.0:commit -DprocessAllModules
