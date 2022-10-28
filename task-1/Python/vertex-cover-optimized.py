#!/usr/bin/env python3

from re import match
from signal import signal, SIGINT
from time import sleep

MEM = {}        # Dictionary for memorization
LAST_K = 0      # For debugging only (last-k: xxx)
RECURSION = 0   # For debugging only (#recursive steps: xxx)

# MAIN METHOD
def run():
    # Try parse input ... display msg in case of error
    try: E,D = read_input()
    except Exception as e: return print("# ERROR: Could not parse input!")
    # Extract set of nodes from MAPPING dict (str->int)
    V = set(D.keys())
    # Execute vertex cover algorithm (from lecture)
    S = vertex_cover(V,E)
    # Print result lines
    [print(D[node]) for node in S]

# Memorization method (store partial results in variable to avoid future computations)
def mem(V:set, E:list, k:int, n:any=None) -> set:
    # Use intelligent mapping from (G and k) to recognize "similiar" graphs
    id = make_id(V,E,k,n)
    if id not in MEM:
        # Compute partial result once ... save it for future use
        # Additional optimization: Only compute En if really needed (= not memorized yet)
        En = [e for e in E if n not in e] if not n is None else E
        res = vc_branch(V,En,k)
        MEM[id] = res
        return res
    else: return MEM[id] # Provide already computed partial result (instead of recomputing it again)

# Intelligent Mapping (from Graph G and iteration k for memorization): 
# 1) Compute degree(v) for all v in V
# 2) Sort list of degrees (int)
# 3) Concat list of int's and iteration parameter k
# 4) Represent list of ints + [k] as str => Representation of G + k
def make_id(V:set, E:list, k:int, n:any):
    # Compute degree(V)
    D = {v:0 for v in V if v != n}
    for u,v in E:
        if n in (u,v): continue
        D[u] += 1
        D[v] += 1
    # Extract and sort list of degrees
    nodes = list(D.values())
    nodes.sort()
    # Represent degree list and parameter k as string
    return "-".join(("-".join([str(x) for x in nodes]),str(k)))

# Read input graph from stdin
def read_input():
    # Parse header line: "#V E"
    nodes,edges = [int(inp) for inp in match(r"#(\d+) (\d+)",input()).groups()]
    V,E = set(),list()      # V = Set of nodes, E = List of node tuples
    D,D2 = dict(),dict()    # D = Mapping (node->int), D2 = Reverse mapping (int->node)
    for i in range(edges):
        # Parse edge line: "u v"
        e = match(r"([a-zA-Z0-9_]+) ([a-zA-Z0-9_]+)",input()).groups()
        for node in e:
            if node not in V:
                # Add node to set
                V.add(node)
                # Save int->node and node->int mapping
                d = len(D)
                D[d],D2[node] = node,d
        # Add edge to list
        E.append(tuple(D2[n] for n in e))
    return E,D

# Vertex Cover (based on 2^k algorithm from lecture)
def vertex_cover(V:set, E:list):
    for k in range(len(V)):
        LAST_K = k
        S = mem(V,E,k)      # Use memorization (instead of calling 'vc_branch' method directly)
        if not S is None: return S
    return V

# Recursive vertex cover branching (from lecture)
def vc_branch(V:set, E:list, k:int):
    global RECURSION
    RECURSION += 1
    if k < 0: return None
    elif len(E) == 0: return set()
    for node in E[0]:
        V.remove(node)              # Remove node from set (HINT: edges will be deleted later in 'mem' method)
        S = mem(V,E,k-1,node)       # Use memoriration or do recursive computation if not computed already. 
        V.add(node)                 # Re-insert node to set
        if not S is None:
            S.add(node)
            return S
    return None

# print additional comment lines when SIGINT received
def debug_before_shutdown(sig,frame):
    print("#recursive steps: {}".format(RECURSION))
    print("#last-k: {}".format(LAST_K))
    exit(1)

# Start 'run' method
if __name__ == "__main__":
    signal(SIGINT,debug_before_shutdown)    # For debugging only
    run()
    print("#recursive steps: {}".format(RECURSION))
    print("#last-k: {}".format(LAST_K))