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
	MEM = dict()
	for k in range(len(V)):
		LAST_K = k
		S = mem(MEM,E,k)
		if S != None: return S
	return V

# Find vertex cover (size k) method (from lecture)
def vc_branch(MEM,E,k):
	global RECURSION
	RECURSION += 1
	if k < 0: return None
	elif len(E) == 0: return list()
	for n in E[0]:
		En = [e for e in E if n not in e]
		S = mem(MEM,En,k-1)
		if S != None:
			S.append(n)
			return S
	return None

def mem(MEM,E,k):
	#id = "#".join((str(E),str(k)))
	id = "#".join(("+".join(",".join(e) for e in E),str(k)))
	if id not in MEM:
		S = vc_branch(MEM,E,k)
		MEM[id] = S
		return S
	else: return MEM[id]

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