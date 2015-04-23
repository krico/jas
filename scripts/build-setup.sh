#!/bin/bash
set -e

if [ "${JAS_BUILD_MODE}" = "e2e" -a "${TRAVIS_PULL_REQUEST}" = "false" ];
then
  #mvn -f schedule/schedule-appengine/pom.xml -DskipTests=true compile

  logfile="$(pwd)/build-logs/devserver.$(date +%s).$$.log"
  mkdir -pv "$(dirname $logfile)"
  (mvn -f schedule/schedule-appengine/pom.xml -DskipTests=true appengine:devserver >"${logfile}" 2>&1 ) &
  count=0
  while [ ! -e "${logfile}" ];
  do
    if [[ $count -gt 10 ]];
    then
      echo "[$count] Waited to long for logfile, giving up...";
      exit 1;
    fi
    let ++count
    echo "[$count] Waiting for logfile..."
    sleep 1;
  done

  # Now wait for it to start
  count=0
  sleepTime=10
  maxCount=90 # We give up after 15min

  while [[ $count -lt $maxCount ]];
  do
    let ++count
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
  cat "$logfile";
  echo "Took too long, giving up";
  exit 1;
fi
