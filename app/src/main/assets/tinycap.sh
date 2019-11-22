#!/system/bin/sh

timeout=$1
shift

tinycap "$@" &

sleep $timeout

pkill tinycap 

