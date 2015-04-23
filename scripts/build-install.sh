#!/bin/bash
set -e

if [ "${JAS_BUILD_MODE}" = "ci" ];
then
  echo "Setting up links for schedule/schedule-appengine/node"
  mkdir -p schedule/schedule-appengine/node
  rm -rf schedule/schedule-appengine/node
  [ -d "$JAS_CACHE/schedule/schedule-appengine/node" ] || mkdir -p "$JAS_CACHE/schedule/schedule-appengine/node"
  ln -s "$JAS_CACHE/schedule/schedule-appengine/node" schedule/schedule-appengine/node

  echo "Setting up links for schedule/schedule-appengine/node_modules"
  mkdir -p schedule/schedule-appengine/node_modules
  rm -rf schedule/schedule-appengine/node_modules
  [ -d "$JAS_CACHE/schedule/schedule-appengine/node_modules" ] || mkdir -p "$JAS_CACHE/schedule/schedule-appengine/node_modules"
  ln -s "$JAS_CACHE/schedule/schedule-appengine/node_modules" schedule/schedule-appengine/node_modules

  echo "Setting up links for $HOME/.m2"
  rm -rf $HOME/.m2
  mkdir -p "$JAS_CACHE/.m2"
  ln -s "$JAS_CACHE/.m2" $HOME/.m2

  echo "Setting up links for $HOME/.npm"
  rm -rf $HOME/.npm
  mkdir -p "$JAS_CACHE/.npm"
  ln -s "$JAS_CACHE/.npm" $HOME/.npm

  echo "Setting up links for $HOME/.bower"
  rm -rf $HOME/.bower
  mkdir -p "$JAS_CACHE/.bower"
  ln -s "$JAS_CACHE/.bower" $HOME/.bower

  exec mvn install -DskipTests=true
fi
