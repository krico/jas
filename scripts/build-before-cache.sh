#!/bin/bash
rm -f $HOME/.m2/repository/org/codehaus/mojo/versions-maven-plugin/maven-metadata-central.xml
find $HOME/.m2/repository -name _remote.repositories -exec rm -f {} \;
mkdir -p /tmp/cache-trick/
mv $HOME/.m2/repository/com/jasify /tmp/cache-trick/
exit 0