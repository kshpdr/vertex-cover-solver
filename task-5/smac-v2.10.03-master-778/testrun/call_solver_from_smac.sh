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
time=$(tail -n 1 time.txt);
# print output for SMAC-tool
if [[ $time == "Command terminated by signal 9"* ]] || [[ $time == "Command exited with non-zero status"* ]]; then
    # when no time available ... TIMEOUT
    echo "Result of this algorithm run: TIMEOUT, $cutoff_time, 0, $cutoff_time, $seed"
else
    # otherwise ... check solution size
    testcase_solution_file="${testcase%%.dimacs}.solution"
    is_valid=1
    if [ -f $testcase_solution_file ] ; then
        solver_solsize=$(grep -v '#' prog_out.txt | wc -l)
        expected_solsize=$(head -n 1 $testcase_solution_file)
        if [ $solver_solsize -gt $expected_solsize ] ; then
            # CRASHED, if solution size is too large
            echo "CRASHED, $time, 0, $cutoff_time, $seed"
            is_valid=0
        fi
    fi
    # otherwise ... SUCCESS
    if [ $is_valid -eq 1 ] ; then
        echo "Result of this algorithm run: SUCCESS, $time, 0, $time, $seed"
    fi
fi

# remove tmp files
rm prog_out.txt time.txt