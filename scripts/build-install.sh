#!/bin/bash
set -e

if [ "${JAS_BUILD_MODE}" = "ci" ];
then
  mkdir -p schedule/schedule-appengine/node
  mkdir -p schedule/schedule-appengine/node_modules
  rm -rf schedule/schedule-appengine/node schedule/schedule-appengine/node_modules
  mkdir -p $JAS_CACHE/schedule/schedule-appengine/node
  ln -s $JAS_CACHE/schedule/schedule-appengine/node schedule/schedule-appengine/node
  mkdir -p $JAS_CACHE/schedule/schedule-appengine/node_modules
  ln -s $JAS_CACHE/schedule/schedule-appengine/node_modules schedule/schedule-appengine/node_modules
  exec mvn install -DskipTests=true
fi
