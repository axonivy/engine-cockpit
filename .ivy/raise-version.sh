#!/bin/bash

mvn -B versions:set -DnewVersion=${1} -DprocessAllModules
mvn -B versions:commit -DprocessAllModules
