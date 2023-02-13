import sys

def complement_graph_cmd(output_file):
    # Read input graph from the command line
    nodes, edges = map(int, input().strip('#').split())
    input_graph = set()
    for i in range(edges):
        node1, node2 = map(int, input().strip().split())
        input_graph.add((node1, node2))

    # Create a set of all possible edges
    all_edges = set()
    for i in range(1, nodes+1):
        for j in range(i+1, nodes+1):
            all_edges.add((i, j))

    # Find the complement graph by subtracting input graph from all possible edges
    complement_graph = all_edges - input_graph

    # Write complement graph to the output file
    with open(output_file, 'w') as f:
        f.write(f'p edge {nodes} {len(complement_graph)}\n')
        for edge in complement_graph:
            f.write(f'e {edge[0]} {edge[1]}\n')

def complement_graph(input_file, output_file):
    # Read input graph from the file
    with open(input_file, 'r') as f:
        lines = f.readlines()
    nodes, edges = map(int, lines[0].strip('#').split())
    input_graph = set()
    for line in lines[1:]:
        node1, node2 = map(int, line.strip().split())
        input_graph.add((node1, node2))

    # Create a set of all possible edges
    all_edges = set()
    for i in range(1, nodes+1):
        for j in range(i+1, nodes+1):
            all_edges.add((i, j))

    # Find the complement graph by subtracting input graph from all possible edges
    complement_graph = all_edges - input_graph

    # Write complement graph to the output file
    with open(output_file, 'w') as f:
        f.write(f'p edge {nodes} {len(complement_graph)}\n')
        for edge in complement_graph:
            f.write(f'e {edge[0]} {edge[1]}\n')

if __name__ == '__main__':
    print("wow")
    #complement_graph(sys.argv[1], "complement/" + sys.argv[1].split('.')[0] +  "_complement." + sys.argv[1].split('.')[1])
	#complement_graph_cmd("../../task-5/scripts/complement/output.txt")
    complement_graph_cmd("./complement/output.txt")
