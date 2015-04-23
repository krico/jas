#!/bin/bash
find $HOME/.m2/repository/ -name _remote.repositories -exec rm -f {} \;
rm -rf $HOME/.m2/repository/com/jasify
echo "Checking cache sizes"
du -hs $JAS_CACHE $JAS_CACHE/* $JAS_CACHE/*/*
exit 0