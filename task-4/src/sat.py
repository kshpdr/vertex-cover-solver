#!/usr/bin/env python3

from sys import stdin
from re import match
from time import time
from pysat.examples.rc2 import RC2
from pysat.formula import WCNF, IDPool

def main():
    t1 = time()
    # Init Weighted SAT Solver instance
    wcnf = WCNF()
    vpool = IDPool(start_from=1)

    # Init datastructures (V,E)
    V,R = set(),list()
    # Parse input graph (from stdin) line by line ...
    for line in stdin:
        line = line.strip()
        # Parse reduced vertices from Reduction Rules output (if exist)
        if line.startswith("# reduced vertex: "):
            R.append(line[18:])
        # Skip comments and empty lines
        if line.startswith("#") or len(line) == 0: continue
        
        # Parse edge line
        e = match(r"([a-zA-Z0-9_]+) ([a-zA-Z0-9_]+)",line).groups()
        u,v = e

        # Add clauses to Weighted SAT Solver
        wcnf.append([vpool.id(u),vpool.id(v)])
        for v in e:
            if v not in V:
                wcnf.append([-vpool.id(v)],weight=1)
                V.add(v)

    # Set solver algorithm for Weighted SAT Solver
    with RC2(wcnf, solver="m22") as rc2:
        t2 = time()
        print("### PARSE INPUT + INIT WCNF: {:.2f}".format(t2-t1))

        # Compute solution ...
        model = rc2.compute()
        t3 = time()
        print("### SOLVE WCNF: {:.2f}".format(t3-t2))

        # Extract and print solution
        solution = [v for v in V if model[vpool.id(v)-1] > 0]
        t4 = time()
        print("### EXTRACT SOLUTION: {:.2f}".format(t4-t3))

        if len(solution) > 0: print("\n".join(solution))
        if len(R) > 0: print("\n".join(R))
        t5 = time()
        print("### PRINT SOLUTION: {:.2f}".format(t5-t4))

if __name__ == '__main__':
    main()