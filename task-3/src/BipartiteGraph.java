import java.io.IOException;
import java.util.*;

public class BipartiteGraph {
    public Vertex nilVertex = new Vertex("nil", -1);
    static final int INF = Integer.MAX_VALUE;

    public ArrayList<Vertex> left = new ArrayList<>();
    public ArrayList<Vertex> right = new ArrayList<>();
    public List<Edge> edges = new ArrayList<>();

    HashMap<Vertex, Vertex> pairLeft, pairRight;
    HashMap<Vertex, Integer> dist;

    public BipartiteGraph(Graph graph) {
        nilVertex.dist = INF;
        for (Vertex vertex : graph.getVertices()) {
            if (vertex.active) {
                left.add(new Vertex(vertex.name, vertex.id));
                right.add(new Vertex(vertex.name, vertex.id));
            }
        }

        for (Edge edge : graph.getListEdges()) {
            if (edge.v.active && edge.w.active) {
                Vertex leftFirst = left.get(left.indexOf(edge.getFirstVertex()));
                Vertex leftSecond = left.get(left.indexOf(edge.getSecondVertex()));

                Vertex rightFirst = right.get(right.indexOf(edge.getFirstVertex()));
                Vertex rightSecond = right.get(right.indexOf(edge.getSecondVertex()));
                edges.add(new Edge(leftFirst, rightSecond));
                edges.add(new Edge(leftSecond, rightFirst));
            }
        }
    }

    // horcropft-carp algorithm for maximum matching in bipartite graphs
    public int findMaximumMatchingSize() {
        int result = 0;

        pairLeft = new HashMap<>(left.size() + 1);
        pairRight = new HashMap<>(right.size() + 1);
        dist = new HashMap<>(left.size() + 1);

        for (Vertex vertex : left) {
            pairLeft.put(vertex, nilVertex);
            dist.put(vertex, 0);
        }

        for (Vertex vertex : right) {
            pairRight.put(vertex, nilVertex);
        }

        // Keep updating the result while
        // there is an augmenting path.
        while (bfs()) {
            // Find a free vertex
            for (Vertex vertex : pairLeft.keySet())
                // If current vertex is free and there is
                // an augmenting path from current vertex
                if (pairLeft.get(vertex) == nilVertex && dfs(vertex))
                    result++;
        }

        return result;
    }

    // returns true, when augmenting path exists
    public boolean bfs() {
        Queue<Vertex> queue = new LinkedList<>();
        for (Vertex vertex : pairLeft.keySet()) {
            if (pairLeft.get(vertex) == nilVertex) {
                // u is not matched
                dist.replace(vertex, 0);
                queue.add(vertex);
            } else
                dist.replace(vertex, INF);
        }
        dist.put(nilVertex, INF);
        while (!queue.isEmpty()) {
            Vertex vertex = queue.poll();
            if (dist.get(vertex) < dist.get(nilVertex)) {
                for (Edge edge : edges) {
                    if (edge.getFirstVertex() == vertex) {
                        Vertex neighbor = edge.getSecondVertex();
                        if (dist.get(pairRight.get(neighbor)) == INF) {
                            dist.replace(pairRight.get(neighbor), dist.get(vertex) + 1);
                            queue.add(pairRight.get(neighbor));
                        }
                    }
                }
            }
        }
        return (dist.get(nilVertex) != INF);
    }

    public boolean dfs(Vertex vertex) {
        if (vertex != nilVertex) {
            for (Edge edge : edges) {
                if (edge.getFirstVertex() == vertex) {
                    // Adjacent to u
                    Vertex neighbor = edge.getSecondVertex();
                    // Follow the distances set by BFS
                    if (dist.get(pairRight.get(neighbor)) == dist.get(vertex) + 1) {
                        if (dfs(pairRight.get(neighbor))) {
                            pairRight.replace(neighbor, vertex);
                            pairLeft.replace(vertex, neighbor);
                            return true;
                        }
                    }
                }
            }
            dist.replace(vertex, INF);
            return false;
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        InputParser inputParser = new InputParser();

        List<String> stringEdges = inputParser.parseEdges();

        HashSet<String[]> edges = new HashSet<>();
        for (String stringEdge : stringEdges) {

            String[] nodes = stringEdge.split("\\s+");
            edges.add(nodes);

        }

//        Graph graph = new Graph(edges);
//        BipartiteGraph bipartiteGraph = new BipartiteGraph(graph);
//        System.out.println(bipartiteGraph.findMaximumMatchingSize());
    }
}
