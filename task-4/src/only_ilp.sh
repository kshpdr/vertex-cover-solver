#!/bin/bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cd $SCRIPT_DIR

python3 ilp_via_cplex.py &> /dev/null
if [ -f solution.tmp ] ; then
    cat solution.tmp
    rm solution.tmp
fi

exit 0
