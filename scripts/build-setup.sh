#!/bin/bash
set -ev

if [ "${JAS_BUILD_MODE}" = "e2e" -a "${TRAVIS_PULL_REQUEST}" = "false" ];
then
  mvn -f schedule/schedule-appengine/pom.xml -DskipTests=true compile

  logfile="$(pwd)/build-logs/devserver.$(date +%s).$$.log"
  (mvn -f schedule/schedule-appengine/pom.xml -DskipTests=true appengine:devserver >"${logfile}" 2>&1 &)
  if [ ! -e "${logfile}" ];
  then
    echo "FAILED TO START DEVSERVER, missing [$logfile]" >&2
    exit 1
  fi

  # Now wait for it to start
  count=0
  sleepTime=30
  maxCount=30 # We give up after 15min

  while [[ $count < $maxCount ]];
  do
    let count++
    echo "[$count] Waiting $sleepTime seconds for appengine:devserver to start"
    sleep $sleepTime;
    if grep "com.google.appengine.tools.development.DevAppServerImpl doStart" "${logfile}" >/dev/null 2>&1;
    then
      # matched
      echo "APPENGINE DEVSERVER STARTED";
      exit 0;
    fi
    if grep "[INFO] BUILD FAILURE" "${logfile}" >/dev/null 2>&1;
    then
      # matched
      cat "$logfile";
      echo "FAILED TO START APPENGINE DEVSERVER";
      exit 1;
    fi
  done
fi
