#!/bin/bash
find $MAVEN_REPOSITORY -name _remote.repositories -exec rm -f {} \;
rm -rf $MAVEN_REPOSITORY/com/jasify
echo "Checking cache sizes"
du -hs $JAS_CACHE/*/*
exit 0