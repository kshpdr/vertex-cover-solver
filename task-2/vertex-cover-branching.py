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
    try: V,E = read_input()                       # Parse input from stdin
    except Exception as e: return print("[ERROR] Could not parse input graph: "+str(e))
    E,S0 = apply_reduction(E)                     # Apply reduction rules
    S = vertex_cover(V-set(S0),E)                 # Execute V.C. algorithm (on reduced graph if possible)
    for v in S0: S.append(v)
    if len(S) > 0: print("\n".join(S))            # Print result

# Parse input graph from stdin
def read_input():
    V,E = set(),list()
    for line in stdin:                                        			# Parse each following edge line
        line = line.strip()
        if line.startswith("#") or len(line.strip()) == 0: continue		# Skip comment lines and empty lines
        e = match(r"([a-zA-Z0-9_]+) ([a-zA-Z0-9_]+)",line).groups()
        E.append(e)
        for n in e: V.add(n)
    E.sort()
    return V,E

# Try to apply reduction rule multiple times
def apply_reduction(E):
    S = []                                        # Store partial result of the VC-Solution
    E,v = reduction_rules(E)
    while not v is None:                          # Call the reduction-method repeatedly
        S.append(v)                               # Add removed vertex to partial solution of the VC-Solution
        E,v = reduction_rules(E)
    return E,S                                    # Return reduced graph and partial VC-Solution

# Single reduction rule implemented
def reduction_rules(E):                           
    D = node_degrees(E)                           # Compute node degree for each node
    for v,ne in D.items():
        if len(ne) == 1:                          # Find vertex v with degree = 1
            n = ne[0]                             # Remove the (single) neighbour N(v) from graph
            En = [e for e in E if n not in e]
            return En,n                           # Add N(v) to the VC-Solution
    return E,None   # Reduction rule can't be applied anymore (=> there is no vertex v with degree = 1)

# Find minimal vertex cover (method from lecture)
def vertex_cover(V,E):
    for k in range(len(V)):
        LAST_K = k
        S = vc_branch(E,k)
        if S != None: return S
    return V

# Find vertex cover of size k (method from lecture + improved branching)
def vc_branch(E,k):
    global RECURSION
    RECURSION += 1

    if k < 0: return None
    elif len(E) == 0: return list()
        
    # Calculate node degree (for each node)
    D = node_degrees(E)

    # Find max degree node
    max_v = max(D,key=lambda k: len(D[k]))
    max_ne = D[max_v]
    max_deg = len(max_ne)

    # Try G\{v}
    En = [e for e in E if max_v not in e]
    S = vc_branch(En,k-1)
    if not S is None:
        S.append(max_v)
        return S
    
    # Try G\N(v)
    En = [e for e in E if sum([v in max_ne for v in e]) == 0]
    S = vc_branch(En,k-max_deg)
    if not S is None:
        for v in max_ne:
            S.append(v)
        return S
    
    # No solution
    return None

# Loop through list of edges and create adjacency-hashmap
def node_degrees(E):
    D = {}
    for u,v in E:
        if u in D: D[u].append(v)
        else: D[u] = [v]
        if v in D: D[v].append(u)
        else: D[v] = [u]
    return D

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
