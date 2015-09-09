#!/bin/bash

readonly SCHEDULE_VERSION=1.0.3-SNAPSHOT
readonly APPENGINE_VERSION=1.9.24
readonly MAVEN_HOME=~/.m2
readonly APPENGINE_HOME="${MAVEN_HOME}/repository/com/google/appengine/appengine-java-sdk/${APPENGINE_VERSION}/appengine-java-sdk/appengine-java-sdk-${APPENGINE_VERSION}"
readonly TOOL="${APPENGINE_HOME}/bin/endpoints.sh"
readonly BASE_DIR=$(cd $(dirname $0)/..; pwd)
readonly WAR_PATH=schedule/schedule-appengine/target/schedule-appengine-${SCHEDULE_VERSION}
readonly WEB_XML=${WAR_PATH}/WEB-INF/web.xml

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

if [ ! -d "${WAR_PATH}" ];
then
  echo "Missing WAR_PATH=$WAR_PATH" >&2
  exit 1
fi

echo -ne "Making scripts executable under ${APPENGINE_HOME}/bin ...";
find "${APPENGINE_HOME}/bin" -type f -name "*.sh" -exec chmod a+x {} + 1>/dev/null
echo "ok"


endpoints=$(egrep "^\s*com\.jasify\.schedule\.appengine\.spi" $WEB_XML |sed -E -e 's|^.*(com\.jasify\.schedule\.appengine\.spi\.[A-Za-z0-9]+).*$|\1|'|tr '\n' ' ')

echo "Running endpoints.sh"
$TOOL get-discovery-doc --war=${WAR_PATH} --output=target $endpoints