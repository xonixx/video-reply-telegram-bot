#!/usr/bin/env bash

(( i = 0 ))
for f in $(cat 1.txt) ; do
    (( i++ ))
    echo
    echo "Downloading #$i: $f"
    echo
    youtube-dl -o "./vids/${i}_%(title)s.%(ext)s" $f -f 18
done
