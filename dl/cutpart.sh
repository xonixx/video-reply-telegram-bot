#!/usr/bin/env bash

file=$1
start=$2
duration=$3
nosound=0

if (( 1 == nosound ))
then
  ffmpeg_cmd="-vcodec copy -filter:a volume=0"
else
#  ffmpeg_cmd="-c copy"
  ffmpeg_cmd=""
fi


file=${file/.MP4/.mp4}
file_out=${file/.mp4/_cut.mp4}

if [[ -z "$duration" ]]
then
  duration_cmd=""
else
  duration_cmd="-t $duration"
fi

yes | ffmpeg -ss "$start" -i "$file" $duration_cmd $ffmpeg_cmd "$file_out"

echo $file_out


