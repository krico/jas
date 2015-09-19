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

usage()
{
  echo "Usage: $(basename $0) VERSION DOWNLOAD_DIR";
  echo "";
  echo " downloads app version VERSION to DOWLOAD_DIR";
  echo "";
  exit 1;
}

readonly VERSION="$1"
readonly DOWNLOAD_DIR="$2"

if [ -z "$VERSION" ];
then
  echo "MISSING: VERSION" >&2
  usage;
fi

if [ -z "$DOWNLOAD_DIR" ];
then
  echo "MISSING: DOWNLOAD_DIR" >&2
  usage;
fi

if [ -d "$DOWNLOAD_DIR" ];
then
  echo "DOWNLOAD_DIR ($DOWNLOAD_DIR) cannot exist" >&2
  usage;
fi

echo -ne "Making scripts executable under ${APPENGINE_HOME}/bin ...";
find "${APPENGINE_HOME}/bin" -type f -name "*.sh" -exec chmod a+x {} + 1>/dev/null
echo "ok"

echo "Running appcfg.sh -A jasify-schedule -V $VERSION download_app $DOWNLOAD_DIR"
if ! $TOOL -A jasify-schedule -V $VERSION download_app $DOWNLOAD_DIR;
then
  exit 1;
else
  exit 0
fi
