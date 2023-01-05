#!/bin/bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cd $SCRIPT_DIR

if [ -f solution.tmp ] ; then
    rm solution.tmp
fi
java Graph 2> /dev/null | python3 ilp_via_cplex.py &> /dev/null
if [ -f solution.tmp ] ; then
    cat solution.tmp
    rm solution.tmp
fi

exit 0
