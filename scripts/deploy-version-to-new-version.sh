#!/bin/bash

readonly BASE_DIR=$(cd $(dirname $0)/..; pwd)

if ! cd "${BASE_DIR}";
then
  echo "Failed to chdir to BASE_DIR = $BASE_DIR" >&2
  exit 1
fi

usage()
{
  echo "Usage: $(basename $0) FROM_VERSION TO_VERSION";
  echo "";
  echo " downloads FROM_VERSION from server and uploads it to TO_VERSION";
  echo "";
  exit;
}

readonly FROM_VERSION="$1"
readonly TO_VERSION="$2"

if [ -z "$FROM_VERSION" ];
then
  echo "MISSING: FROM_VERSION" >&2
  usage;
fi

if [ -z "$TO_VERSION" ];
then
  echo "MISSING: TO_VERSION" >&2
  usage;
fi

readonly WORK_DIR=$(mktemp -q -d "${BASE_DIR}/target/$(basename $0).XXXXXX");
echo "WORK_DIR=$WORK_DIR"
if ! rmdir "$WORK_DIR";
then
  echo "Failed to remove WORK_DIR" >&2;
  exit 2;
fi

if ! ./scripts/appcfg-download-app.sh $FROM_VERSION $WORK_DIR;
then
  echo "Failed to download..." >&2;
  exit 2;
fi

./scripts/appcfg-upload-app.sh $TO_VERSION $WORK_DIR