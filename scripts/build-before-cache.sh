#!/bin/bash
#rm -f $HOME/.m2/repository/org/codehaus/mojo/versions-maven-plugin/maven-metadata-central.xml
find $HOME/.m2/repository -name _remote.repositories -exec rm -f {} \;
rm -rf $HOME/.m2/repository/com/jasify
echo "Checking cache sizes"
du -hs $HOME/.m2 $HOME/.bower $HOME/.npm schedule/schedule-appengine/node schedule/schedule-appengine/node_modules

exit 0