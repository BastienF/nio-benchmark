#!/bin/bash

date=`date +%s`
while [ $# -gt 0 ]
do
    case "$1" in
  --cpu-time) cpuTime="$2"; shift;;
  --latency-time) latencyTime="$2"; shift;;
  --async) aws=true;;
  -*) echo >&2 \
      "wrong var: usage: $0 --cpu-time time --latency-time time [--async]"
      exit 1;;
  *)  break;; # terminate while loop
    esac
    shift
done

if [ -z "$cpuTime" ]; then
  echo >&2 \
      "not enougth vars: usage: $0 --cpu-time time --latency-time time [--async]"
  exit 1
fi

if [ -z "$latencyTime" ]; then
  echo >&2 \
      "not enougth vars: usage: $0 --cpu-time time --latency-time time [--async]"
  exit 1
fi

if [ -z "$async" ]; then
	async=false
fi

if [ "$async" = true ] ; then
	latencyServer="./target/AsyncLatencyServer.jar"
	cpuServer="./target/AsyncCPUServer.jar"
	cpuURL="http://localhost:8082/war/async/cpu"
else
	latencyServer="./target/SyncLatencyServer.jar"
	cpuServer="./target/SyncCPUServer.jar"
	cpuURL="http://localhost:8082/war/sync/cpu"
fi

java -jar $latencyServer &
WAITPID_LATENCY=$!
java -jar $cpuServer &
WAITPID_CPU=$!
sleep 1
echo "$cpuURL?cpu=$cpuTime&latency=$latencyTime"
ab -r -k -c10000 -n1000000 "$cpuURL?cpu=$cpuTime&latency=$latencyTime"
kill -9 $WAITPID_LATENCY
kill -9 $WAITPID_CPU
