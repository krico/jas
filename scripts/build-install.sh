#!/bin/bash
set -e

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

echo "Setting up links for schedule/schedule-appengine/target/.bower"
mkdir -p schedule/schedule-appengine/target/.bower
rm -rf schedule/schedule-appengine/target/.bower
[ -d "$JAS_CACHE/schedule/schedule-appengine/target/.bower" ] || mkdir -p "$JAS_CACHE/schedule/schedule-appengine/target/.bower"
ln -s "$JAS_CACHE/schedule/schedule-appengine/target/.bower" schedule/schedule-appengine/target/.bower

echo "Setting up links for schedule/schedule-appengine/target/bower_components"
mkdir -p schedule/schedule-appengine/target/bower_components
rm -rf schedule/schedule-appengine/target/bower_components
[ -d "$JAS_CACHE/schedule/schedule-appengine/target/bower_components" ] || mkdir -p "$JAS_CACHE/schedule/schedule-appengine/target/bower_components"
ln -s "$JAS_CACHE/schedule/schedule-appengine/target/bower_components" schedule/schedule-appengine/target/bower_components

echo "Setting up links for $HOME/.m2"
rm -rf $HOME/.m2
mkdir -p "$JAS_CACHE/HOME.m2"
ln -s "$JAS_CACHE/HOME.m2" $HOME/.m2

echo "Setting up links for $HOME/.npm"
rm -rf $HOME/.npm
mkdir -p "$JAS_CACHE/HOME.npm"
ln -s "$JAS_CACHE/HOME.npm" $HOME/.npm

echo "Setting up links for $HOME/.bower"
rm -rf $HOME/.bower
mkdir -p "$JAS_CACHE/HOME.bower"
ln -s "$JAS_CACHE/HOME.bower" $HOME/.bower



if [ "${JAS_BUILD_MODE}" = "ci" ];
then
  exec mvn install -DskipTests=true
fi
