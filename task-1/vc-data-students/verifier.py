#!/usr/bin/python3

import sys
if len(sys.argv) != 3:
    print("usage: verifier.py graph.dimacs vc.solution")
    exit(1)

vc = set()
vcfile = sys.argv[2]
graph = sys.argv[1]

with open(vcfile) as fp:
   line = fp.readline()
   while line:
       if line[0] != '#':
           vc.add(int(line.strip()))
       line = fp.readline()

#print("vertex cover size: " + str(len(vc)))
with open(graph) as fp:
   line = fp.readline()
   while line:
       if line[0] != '#':
           v = int(line.strip().split()[0])
           w = int(line.strip().split()[1])
           if v not in vc and  w not in vc:
               print("Uncovered edge: " + line)
               exit(1)
       line = fp.readline()

