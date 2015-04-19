#!/bin/bash
set -ev

if [ "${JAS_BUILD_MODE}" = "e2e" -a "${TRAVIS_PULL_REQUEST}" = "false" ];
then
  mvn -f schedule/schedule-appengine/pom.xml -DskipTests=true compile
  (mvn -f schedule/schedule-appengine/pom.xml -DskipTests=true appengine:devserver &) || /bin/true
  sleep 300 # let appengine fire up
fi
