#!/usr/bin/env python3

from sys import stdin
from re import match
from signal import signal,SIGINT

class Graph:
    def __init__(self):
        self.E = dict()
        self.D = dict()
        self.max_vertex = None
        self.max_degree = 0
    def __len__(self) -> int:
        return len(self.E)
    def add_edge(self,u:str,v:str):
        if u in self.E: self.E[u].append(v)
        else: self.E[u] = [v]
        if v in self.E: self.E[v].append(u)
        else: self.E[v] = [u]
    def init_degrees(self):
        next = None
        for v,ne in self.E.items():
            l = len(ne)
            # find max degree vertex
            if next == None or next[1] < l: next = v,l
            # update D
            if l in self.D: self.D[l].append(v)
            else: self.D[l] = [v]
        self.max_vertex = next[0] if next != None else None
        self.max_degree = next[1] if next != None else None
        #self.print()
    def print(self):
        print("# max-v:",self.max_vertex)
        print("# max-d:",self.max_degree)
        print("# E:",self.E)
        print("# D:",self.D)
        print()
    def remove_vertex(self,v:str):
        ne = self.E[v]
        l1 = len(ne)
        del self.E[v]               # remove v from E
        self.D[l1].remove(v)        # remove v from D
        if len(self.D[l1]) == 0: del self.D[l1]
        for n in ne:
            l2 = len(self.E[n])
            self.E[n].remove(v)     # remove v from N(v) in E
            self.D[l2].remove(n)    # remove n in D
            if len(self.D[l2]) == 0: del self.D[l2]     # remove 0-degree vertices in D
            if len(self.E[n]) == 0: del self.E[n]       # remove 0-degree vertices in E
            else:
                l3 = l2-1
                if l3 == 0: continue
                if l3 in self.D: self.D[l3].append(n)
                else: self.D[l3] = [n]
        if len(self.D) > 0:
            self.max_degree = max(self.D.keys())
            self.max_vertex = self.D[self.max_degree][0]
        else:
            self.max_degree = 0
            self.max_vertex = None

# Parse input graph from stdin
def read_input():
    G = Graph()
    for line in stdin:                                        	# Parse each following edge line
        line = line.strip()
        if line.startswith("#") or len(line) == 0: continue		# Skip comment lines and empty lines
        u,v = match(r"([a-zA-Z0-9_]+) ([a-zA-Z0-9_]+)",line).groups()
        G.add_edge(u,v)
    G.init_degrees()
    return G

def vertex_cover_greedy_heuristic(G:Graph):
    #G.print()
    while len(G) > 0:
        v = G.max_vertex
        print(v)
        G.remove_vertex(v)
        #G.print()

# print additional comment lines when SIGINT received
def debug_before_shutdown(sig,frame,graph):
    if graph != None:
        print("\n".join(graph.E.keys()))
        exit(0)
    else: exit(1)

if __name__ == "__main__":
    try: graph = read_input()
    except:
        print("[ERROR] Could not parse input graph!")
        exit(1)
    signal(SIGINT,lambda a,b: debug_before_shutdown(a,b,graph))
    vertex_cover_greedy_heuristic(graph)