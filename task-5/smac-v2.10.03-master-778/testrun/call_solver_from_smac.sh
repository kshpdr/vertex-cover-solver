#!/bin/bash

# hardcoded programm-call for solver
PROGRAMM_NAME="java -jar ../../src/Solver.jar"

# parse wrapper parameters
testcase=$1
cutoff_time=$3
cutoff_length=$4
seed=$5

# parse solver parameters
all_params=($@)
solver_params=${all_params[@]:5}

# call solver (in separate process)
(setsid /usr/bin/time -f "%e" -a -o time.txt timeout --preserve-status -k 10 -s 2 $cutoff_time $PROGRAMM_NAME $testcase ${solver_params[@]} 1> prog_out.txt 2>&1) & PID=$!
trap "{ kill $PID 2>/dev/null; kill -TERM -- -$(pgrep -P $PID)&>/dev/null;}" TERM
trap "{ kill -9 -$PID 2>/dev/null; kill -9 -- -$(pgrep -P $PID)&>/dev/null;}" EXIT

# wait until process finished
wait $PID
$(kill -0 $PID 2>/dev/null || kill -9 -$PID 2>/dev/null;)

# print solver-output to stdout
cat prog_out.txt

# get time
time=$(cat time.txt);
if [[ $time == "Command terminated by signal 9"* ]] || [[ $time == "Command exited with non-zero status"* ]]; then
    # when no time available ... TIMEOUT
    echo "Result of this algorithm run: TIMEOUT, $time, 0, $time, $seed"
else
    # otherwise ... SUCCESS
    echo "Result of this algorithm run: SUCCESS, $time, 0, $time, $seed"
fi

# remove tmp files
rm prog_out.txt time.txt