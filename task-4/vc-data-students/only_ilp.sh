#!/bin/bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cd $SCRIPT_DIR

python3 ilp_via_cplex.py | grep -v "KeyboardInterrupt"

exit 0
