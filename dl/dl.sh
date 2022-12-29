#!/usr/bin/env bash

#YTDL=youtube-dl
#YTDL="python39 /usr/local/bin/yt-dlp"
YTDL="yt-dlp"

(( i = 0 ))
for f in $(cat 1.txt) ; do
    (( i++ ))
    echo
    echo "Downloading #$i: $f"
    echo
    $YTDL -o "./vids/${i}_%(title)s.%(ext)s" $f -f 18
done
