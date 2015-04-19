#!/bin/bash
set -ev
if [ "${JAS_BUILD_MODE}" = "ci" ];
then
  exec mvn install -DskipTests=true
fi
