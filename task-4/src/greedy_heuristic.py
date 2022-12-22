#!/usr/bin/env python3

"""
    => Minimal Python Implementation.

    ### Implemented Features:
        - Simple List of Edges (bad!)
		- Creates a copy from Edge-List in each iteration (bad!)
        - Improved Branching
        - Reduction Rule
    ### TODO:
        - Use Hashmap / Heap / other datastructure for storing edges
		- Use Mapping from Vertex-Label to Numeric-ID (String <=> Int)
		- Add/Remove vertices (more efficiently) instead of duplicating complete edge list
        - Understand + implement Clique-Lower-Bound
        - Understand + implement LP-Lower-Bound
"""

from re import match
from signal import signal,SIGINT
from sys import stdin

# Global variables (used for benchmark.sh)
RECURSION = 0
LAST_K = 0
    
# Main method
def main():
    try: E = read_input()                         # Parse input from stdin
    except Exception as e: return print("[ERROR] Could not parse input graph: "+str(e))
    S = vertex_cover(E)                           # Execute V.C. algorithm (on reduced graph if possible)
    if len(S) > 0: print("\n".join(S))            # Print result

# Parse input graph from stdin
def read_input():
    E = dict()
    for line in stdin:                                        	# Parse each following edge line
        line = line.strip()
        if line.startswith("#") or len(line) == 0: continue		# Skip comment lines and empty lines
        e = match(r"([a-zA-Z0-9_]+) ([a-zA-Z0-9_]+)",line).groups()
        u,v = e
        if u in E: E[u].append(v)
        else: E[u] = [v]
        if v in E: E[v].append(u)
        else: E[v] = [u]
    return E

# Find minimal vertex cover (method from lecture)
def vertex_cover(E):

    def get_max(k):
        return len(E[k])
    
    S = list()

    while len(E) > 0:
        # Find max degree vertex
        vertex = max(E,key=get_max)
        # Add it to the solution
        S.append(vertex)
        # Remove it from graph
        ne = E[vertex]
        del E[vertex]
        for n in ne:
            E[n].remove(vertex)
            if len(E[n]) == 0:
                del E[n]
    
    return S

# print additional comment lines when SIGINT received
def debug_before_shutdown(sig,frame):
    print("#recursive steps: {}".format(RECURSION))
    print("#last-k: {}".format(LAST_K))
    exit(1)

# Call main method
if __name__ == "__main__":
    signal(SIGINT,debug_before_shutdown)
    main()
    print("#recursive steps: {}".format(RECURSION))
