#!/usr/bin/env bash

#set -e

mydir="$(dirname "$0")"

(( i = 0 ))
cd "$mydir/vids"
for f in $(cat ../1.txt) ; do
    (( i++ ))
    echo
    echo "Downloading #$i: $f"
    echo
    fout0="${i}_src.mp4"
    fout1="${i}.mp4"
    youtube-dl -o $fout0 $f -f 18
    # add keyframe every frame
    yes | ffmpeg -i "$fout0" -acodec copy -c:v libx264 -x264opts keyint=1 "$fout1"
#    rm "$fout0"
done
