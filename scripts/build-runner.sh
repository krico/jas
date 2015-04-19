#!/bin/bash
set -ev

if [ "${JAS_BUILD_MODE}" = "ci" ];
then
  exec mvn test
fi

if [ "${JAS_BUILD_MODE}" = "e2e" ];
then
  if [ "${TRAVIS_PULL_REQUEST}" = "false" ];
  then
    cd schedule/schedule-appengine;
    exec ./node_modules/.bin/protractor src/test/e2e/protractor.cfg.js --suite ci
  else
    echo "Cannot run e2e tests on pull requests...";
    exit 0
  fi
fi

echo "UNKNOWN BUILD MODE: [${JAS_BUILD_MODE}]"
exit 1
