import re

DEBUG = True			# True: Enable comments, False: disable comments

# Main method
def main():
	V,E = read_input()	# Parse input from stdin
	S = vertex_cover(V,E)	# Execute V.C. algorithm
	print("\n".join(S))	# Print result

# Parse input graph from stdin
def read_input():
	nodes = int(re.match(r"# (\d+) vertices",input()).group(1))	# Parse number of nodes
	edges = int(re.match(r"# (\d+) edges",input()).group(1))	# Parse number of edges
	V,E = set(),list()
	for i in range(edges):						# Parse each following edge line
		n1,n2 = input().split()
		E.append((n1,n2))
		V.add(n1)
		V.add(n2)
	return V,E

# Find minimal vertex cover method (from lecture)
def vertex_cover(V,E):
	for k in range(0,len(V)):
		if DEBUG: print("# k =",k)
		S = vc_branch(V,E,k)
		if S != None:
			return S
	return V

# Find vertex cover (size k) method (from lecture)
def vc_branch(V,E,k):
	if DEBUG: print("# V={V}  E={E}  k={k}".format(V=V,E=E,k=k))
	if k < 0: return None
	elif len(E) == 0: return set()
	for n in E[0]:
		Vn,En = rem(V,E,n)
		S = vc_branch(Vn,En,k-1)
		if S != None:
			S.add(n)
			return S
	return None

# Remove a node n from graph G=(V,E)
def rem(V,E,v):
	V2 = {n for n in V if n != v}
	E2 = [e for e in E if v not in e]
	return V2,E2

# Call main method
main()
