#!/bin/bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
cd $SCRIPT_DIR

echo "# =============== APPLY REDUCTION RULES ==============="
# Execute java program (reduction rules)
reduction_output=$(java Graph)
rc=$?
# Evaluate exit-code
if [ $rc -eq 0 ] ; then
    grep -P '#' <<< $reduction_output | grep -v "#reduced-vertex: "
    grep -Po '#reduced-vertex: (\K.+)' <<< $reduction_output
    echo "# ================= APPLY ILP SOLVER =================="
    python ilp_via_cplex.py <<< $reduction_output
    rc=$?
    if [ $rc -ne 0 ] ; then
        echo "# ilp-solver exit-code: $rc"
        exit $rc
    fi
else
    echo "# reduction rules exit-code: $rc"
    exit $rc
fi
exit 0