#!/bin/bash
set -ev

if [ "${JAS_BUILD_MODE}" = "e2e" -a "${TRAVIS_PULL_REQUEST}" = "false" ];
then
  (mvn appengine:devserver &) || /bin/true
  sleep 30 # let appengine fire up
fi
