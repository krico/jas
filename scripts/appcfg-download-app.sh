#!/bin/bash

readonly APPENGINE_VERSION=1.9.24
readonly MAVEN_HOME=~/.m2
readonly APPENGINE_HOME="${MAVEN_HOME}/repository/com/google/appengine/appengine-java-sdk/${APPENGINE_VERSION}/appengine-java-sdk/appengine-java-sdk-${APPENGINE_VERSION}"
readonly TOOL="${APPENGINE_HOME}/bin/appcfg.sh"
readonly BASE_DIR=$(cd $(dirname $0)/..; pwd)

if [ ! -d "${APPENGINE_HOME}" ];
then
  echo "Missing APPENGINE_HOME='$APPENGINE_HOME'" >&2
  exit 1
fi

if ! cd "${BASE_DIR}";
then
  echo "Failed to chdir to BASE_DIR = $BASE_DIR" >&2
  exit 1
fi

echo -ne "Making scripts executable under ${APPENGINE_HOME}/bin ...";
find "${APPENGINE_HOME}/bin" -type f -name "*.sh" -exec chmod a+x {} + 1>/dev/null
echo "ok"


echo "Running appcfg.sh"
$TOOL -A jasify-schedule download_app "$@"