#!/bin/bash

mvn versions:set -DnewVersion=${1} -DprocessAllModules
mvn versions:commit -DprocessAllModules
