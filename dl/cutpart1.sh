#!/usr/bin/env bash

file=$1
start=$2
duration=$3

file_out=${file/.mp4/_cut.mp4}

if [[ -z "$duration" ]]
then
  duration_cmd=""
else
  duration_cmd="-t $duration"
fi

yes | ffmpeg -ss "$start" -i "$file" $duration_cmd  -c copy "$file_out"

echo $file_out


