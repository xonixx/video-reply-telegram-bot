#!/usr/bin/env bash

file=$1
start=$2
duration=$3

file=${file/.MP4/.mp4}

file_tmp=${file/.mp4/_tmp.mp4}
file_out=${file/.mp4/_cut.mp4}
#file_out1=${file/.mp4/_cut1.mp4}
#file_out2=${file/.mp4/_cut2.mp4}

#yes | ffmpeg -i "$file" -c copy "$file_out"
#yes | ffmpeg -i "$file_out"  -acodec copy -c:v libx264 -x264opts keyint=1 "$file_out1"
#yes | ffmpeg -i "$file_out1" -acodec copy -c:v libx264 -x264opts keyint=100 "$file_out2"

# add keyframe every frame
yes | ffmpeg -i "$file" -acodec copy -c:v libx264 -x264opts keyint=1 "$file_tmp"

# cut part
yes | ffmpeg -ss "$start" -i "$file_tmp" -t "$duration" -acodec copy -c:v libx264 -x264opts keyint=100 "$file_out"

echo $file_out


