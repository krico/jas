#!/bin/bash
set -ev
case "$JAS_BUILD_MODE" in
  ci)
  mvn test
  ;;
  e2e)
  exit 0
  ;;
  *)
    echo "Unknown build mode [$JAS_BUILD_MODE]";
    exit 1;
  ;;
esac


if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
  echo "NOT PULL"
fi