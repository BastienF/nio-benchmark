#!/bin/bash

date=`date +%s`
while [ $# -gt 0 ]
do
    case "$1" in
  --cpu-time) cpuTime="$2"; shift;;
  --latency-time) latencyTime="$2"; shift;;
  -c) c="$2"; shift;;
  -n) n="$2"; shift;;
  -v) v="-v$2"; shift;;
  --async) async=true;;
  -*) echo >&2 \
      "wrong var: usage: $0 --cpu-time time --latency-time time -c concurrent_users [--async] [-v verbosity]"
      exit 1;;
  *)  break;; # terminate while loop
    esac
    shift
done

if [ -z "$cpuTime" ]; then
  echo >&2 \
      "not enougth vars: usage: $0 --cpu-time time --latency-time time -c concurrent_users [--async] [-v verbosity]"
  exit 1
fi

if [ -z "$latencyTime" ]; then
  echo >&2 \
      "not enougth vars: usage: $0 --cpu-time time --latency-time time -c concurrent_users[--async] [-v verbosity]"
  exit 1
fi

if [ -z "$c" ]; then
  echo >&2 \
      "not enougth vars: usage: $0 --cpu-time time --latency-time time -c concurrent_users[--async] [-v verbosity]"
  exit 1
fi

if [ -z "$n" ]; then
  echo >&2 \
      "not enougth vars: usage: $0 --cpu-time time --latency-time time -c concurrent_users[--async] [-v verbosity]"
  exit 1
fi

if [ -z "$async" ]; then
	async=false
fi
latencyServer="./target/AsyncLatencyServer.jar"
if [ "$async" = true ] ; then
	#latencyServer="./target/AsyncLatencyServer.jar"
	cpuServer="./target/AsyncCPUServer.jar"
	cpuURL="http://localhost:8089/war/async/cpu"
else
	#latencyServer="./target/SyncLatencyServer.jar"
	cpuServer="./target/SyncCPUServer.jar $c"
	cpuURL="http://localhost:8089/war/sync/cpu"
fi

java -jar $latencyServer &
WAITPID_LATENCY=$!
java -jar $cpuServer &
WAITPID_CPU=$!
sleep 30
mkdir results
#echo start warmup
#ab -r -k -c"$c" -n$(( $c + $n/10 )) $v "$cpuURL?cpu=$cpuTime&latency=$latencyTime" 1>/dev/null 2>/dev/null
#echo stop warmup
#sleep 5
ab -r -k -c"$c" -n"$n" $v "$cpuURL?cpu=$cpuTime&latency=$latencyTime" 1>"results/users_$c""_async_$async""_cpu_$cpuTime""_lat_$latencyTime""_$date"".log"
kill -9 $WAITPID_LATENCY
kill -9 $WAITPID_CPU
