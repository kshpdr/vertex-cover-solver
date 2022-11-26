#!/usr/bin/env python3

from hopcroftkarp import HopcroftKarp
from math import ceil

class BipartiteGraph:
    def __init__(self,V,E):
        self.D = {}
        for u,v in E:
            lu,rv = "l-"+u,"r-"+v
            lv,ru = "l-"+v,"r-"+u
            if lu not in self.D: self.D[lu] = {rv}
            else: self.D[lu].add(rv)
            if lv not in self.D: self.D[lv] = {ru}
            else: self.D[lv].add(ru)
    def max_matching(self):
        G = HopcroftKarp(self.D)
        M = G.maximum_matching(keys_only=True)
        return M
    def lower_bound(self):
        M = self.max_matching()
        print("#Max-Matching-Size: "+str(len(M)))
        return ceil(len(M)/2)
