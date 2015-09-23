#!/bin/bash
readonly startTime=$(date +%s);
readonly REPO_URL="git@github.com:krico/jas.git"
readonly startDir=$(pwd)
readonly baseDir=$(cd $(dirname $0)/..; pwd);
readonly script=$(basename $0);
readonly timestamp=$(date +%Y.%m.%d.%H%M)
readonly logFile="${baseDir}/target/$script-${timestamp}.log"
readonly releaseBackup="${baseDir}/target/archive-v${timestamp}.tar.gz"

branch="master";
version=""

while getopts ":b:v:h" opt;
do
  case $opt
  in
    h)
      echo "Usage: $script [-b branch] -v (prod|beta)"
      echo
      echo " Clone repo, build and release to appengine"
      echo
      echo "       -b branch    release from 'branch' (default: 'master')"
      echo "       -v version   release version 'version', valid values are 'prod' or 'beta'."
      echo
      exit 0;
      ;;
    b)
      branch="$OPTARG"
      ;;
    v)
      case "$OPTARG" in prod|beta) version="$OPTARG"; ;; *) echo "Invalid version [$OPTARG]!" >&2; exit 1; ;; esac
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." >&2
      exit 1
      ;;
  esac
done

if [ -z "$version" ];
then
  echo "version (-v) is mandatory!" >&2
  exit 1
fi

# Redirect stdout ( > ) into a named pipe ( >() ) running "tee"
exec > >(tee $logFile)
exec 2>&1


echo
echo "+================================================================================"
echo "| REPOSITORY: $REPO_URL"
echo "| BRANCH:     $branch"
echo "| VERSION:    $version"
echo "| TIMESTAMP:  $timestamp"
echo "+--------------------------------------------------------------------------------"
echo "| LOG:        ${logFile}"
echo "| STARTED:    $(date)"
echo "+================================================================================"
echo

if [ "$version" = "prod" ];
then
  VERSION_OPTS=""
else
  VERSION_OPTS="-Dappengine.app.version=$version"
fi

readonly workDir=$(mktemp -q -d "${baseDir}/target/${script}.XXXXXX");
if [ $? -ne 0 ];
then
  echo "$script: Can't create temp dir, exiting...";
  exit 1;
fi

echo "created [$workDir]";

echo "cloning [$REPO_URL]";

if ! cd "${workDir}";
then
  echo "$script: Can't cd to $workDir: $!";
  exit 1;
fi

git clone "$REPO_URL";

if [ $? -ne 0 ];
then
  echo "$script: Failed to clone: $!";
  exit 1;
fi

cd jas/schedule/schedule-appengine

echo "checking out [$branch]"
git checkout "$branch"

if [ $? -ne 0 ];
then
  echo "$script: Failed to checkout branch [$branch]: $!";
  exit 1;
fi


echo "executing mvn $VERSION_OPTS appengine:update"
if mvn $VERSION_OPTS appengine:update;
then
  readonly tag="v${timestamp}-$version"
  echo "Tagging release [$tag]"
  git tag -a $tag -m "Released to $version from $branch"
  echo "Pushing tags"
  git push origin --tags
  echo -n "Creating releaase backup [$releaseBackup] ...";
  if tar -czf "$releaseBackup" target/schedule-appengine-*;
  then
    echo " ok";
  else
    echo " failed";
  fi
else
  echo "Build failed, will not tag release"
fi

echo "cleaning up ./jas";
cd "$workDir"
rm -rf jas
cd "$startDir"

echo "deleting [$workDir]";
rmdir "${workDir}"

readonly endTime=$(date +%s);

echo
echo "+================================================================================"
echo "| REPOSITORY: $REPO_URL"
echo "| BRANCH:     $branch"
echo "| VERSION:    $version"
echo "| TIMESTAMP:  $timestamp"
echo "+--------------------------------------------------------------------------------"
echo "| FINISHED:   $(date)"
echo "| ELAPSED:    $(($endTime - $startTime)) seconds"
echo "| LOG:        ${logFile}"
echo "+================================================================================"
echo

