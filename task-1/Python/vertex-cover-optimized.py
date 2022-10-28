#!/usr/bin/env python3

from re import match

MEM = {}

def run():
    try:
        E,D = read_input()
    except Exception as e:
        print("# ERROR: Could not parse input!")
        return
    V2 = set(D.keys())
    S = vertex_cover(V2,E)
    [print(D[node]) for node in S]

def mem(V,E,k,n=None):
    #id = "-".join((str(x) for x in (V,k)))
    id = make_id(V,E,k,n)
    if id not in MEM:
        En = [e for e in E if n not in e] if n != None else E
        res = vc_branch(V,En,k)
        MEM[id] = res
        return res
    else: return MEM[id]

def make_id(V,E,k,n):
    D = {v:0 for v in V if v != n}
    for u,v in E:
        if n in (u,v): continue
        D[u] += 1
        D[v] += 1
    nodes = list(D.values())
    nodes.sort()
    return "-".join(("-".join([str(x) for x in nodes]),str(k)))

def read_input():
    nodes,edges = [int(inp) for inp in match(r"#(\d+) (\d+)",input()).groups()]
    V,E,D,D2 = set(),list(),dict(),dict()
    for i in range(edges):
        e = match(r"([a-zA-Z0-9_]+) ([a-zA-Z0-9_]+)",input()).groups()
        for node in e:
            if node not in V:
                V.add(node)
                d = len(D)
                D[d],D2[node] = node,d
        E.append(tuple(D2[n] for n in e))
    return E,D

def vertex_cover(V,E):
    for k in range(len(V)):
        S = mem(V,E,k)
        if S != None: return S
    return V

def vc_branch(V,E,k):
    if k < 0: return None
    elif len(E) == 0: return set()
    for node in E[0]:
        V.remove(node)
        S = mem(V,E,k-1,node)
        V.add(node)
        if S != None:
            S.add(node)
            return S
    return None

if __name__ == "__main__":
    run()