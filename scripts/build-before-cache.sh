#!/bin/bash
find $HOME/.m2/ -name _remote.repositories -exec rm -f {} \;
rm -rf $HOME/.m2/com/jasify
echo "Checking cache sizes"
du -hs $JAS_CACHE $JAS_CACHE/* $JAS_CACHE/*/*
exit 0