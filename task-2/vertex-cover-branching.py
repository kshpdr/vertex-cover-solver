#!/usr/bin/env python3

from re import match
from signal import signal,SIGINT
from sys import stdin

RECURSION = 0
LAST_K = 0

# Main method
def main():
	try: V,E = read_input()						# Parse input from stdin
	except Exception as e: return print("[ERROR] Could not parse input graph: "+str(e))
	S = vertex_cover(V,E)						# Execute V.C. algorithm
	if len(S) > 0: print("\n".join(S))			# Print result

# Parse input graph from stdin
def read_input():
	V,E = set(),list()
	for line in stdin:										# Parse each following edge line
		line = line.strip()
		if line.startswith("#") or len(line.strip()) == 0: continue
		e = match(r"([a-zA-Z0-9_]+) ([a-zA-Z0-9_]+)",line).groups()
		E.append(e)
		for n in e: V.add(n)
	E.sort()
	return V,E

# Find minimal vertex cover method (from lecture)
def vertex_cover(V,E):
	for k in range(len(V)):
		LAST_K = k
		S = vc_branch(E,k)
		if S != None: return S
	return V

# Find vertex cover (size k) method (from lecture)
def vc_branch(E,k):
	global RECURSION
	RECURSION += 1
	if k < 0: return None
	elif len(E) == 0: return list()

	# Calculate node degree (for each node)
	D = node_degrees(E)

	# Check if Kn (complete graph)
	if len(E) == len(D)*(len(D)-1)/2:
		#print(D)
		return list(D.keys())[1:]
	
	# Find max degree node
	max_v = max(D,key=lambda k: len(D[k]))
	#print("#vc_branch():",V,max_v)
	max_ne = D[max_v]
	max_deg = len(max_ne)

	# Check for lower bound (max_degree <= 2)
	if max_deg <= 2:
		S = solve_directly(E,D,k)
		if not S is None:
			return S
	# Try G\{v}
	En = [e for e in E if max_v not in e]
	S = vc_branch(En,k-1)
	if not S is None:
		S.append(max_v)
		return S
	# Try G\ne(v)
	En = [e for e in E if sum([v in max_ne for v in e]) == 0]
	S = vc_branch(En,k-max_deg)
	if not S is None:
		for v in max_ne:
			S.append(v)
		return S
	# No solution
	return None

def solve_directly(E,D,k):
	visited = set()
	D2 = {k:list(v) for k,v in D.items()}
	S = []
	while len(D2) > 0:
		min_v = min(D2,key=lambda k:len(D2[k]))
		path = dfs(D2,min_v)
		visited |= set(path)
		D3 = {}
		S += path[1::2]
		for v,ne in D2.items():
			if v != min_v and v not in path:
				D3[v] = [u for u in ne if u not in path]
				if len(D3[v]) == 0: del D3[v]
		D2 = D3
	return S if len(S) <= k else None

def dfs(D,node):
	path = []
	tmp,last = node,None
	while True:
		path.append(tmp)
		if last != None and tmp == node: break
		for v in D[tmp]:
			if v != last:
				last = tmp
				tmp = v
				break
		else: break
	return path

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
