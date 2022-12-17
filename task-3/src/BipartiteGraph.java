import java.io.IOException;
import java.util.*;

public class BipartiteGraph {
    public Vertex nilVertex = new Vertex("nil", -1);
    static final int INF = Integer.MAX_VALUE;

    public ArrayList<Vertex> left = new ArrayList<>();
    public ArrayList<Vertex> right = new ArrayList<>();
    public HashMap<Vertex,HashSet<Vertex>> adjacentMap = new HashMap<>();

    HashMap<Vertex, Vertex> pairLeft, pairRight;
    HashMap<Vertex, Integer> dist;

    public BipartiteGraph(Graph graph) {
        nilVertex.dist = INF;
        int indexCounter = 0;
        for (Vertex vertex : graph.getVertices()) {
            Vertex v = new Vertex(vertex.name, indexCounter++);
            v.originalId = vertex.id;
            left.add(v);

            Vertex u = new Vertex(vertex.name, indexCounter++);
            u.originalId = vertex.id;
            right.add(u);
        }

        HashMap<Vertex,HashSet<Vertex>> map = graph.getAdjVertices();
        for (Vertex firstVertex : map.keySet()){
            HashSet<Vertex> neighbors = map.get(firstVertex);
            if (neighbors == null) continue;
            for (Vertex secondVertex : neighbors){
                // Skip reverse edges: (0,1) ok, but not (1,0) again => avoid duplicates
                if (firstVertex.id > secondVertex.id) continue;

                Vertex leftFirst = null;
                Vertex leftSecond = null;
                for (Vertex vertex : left){
                    if (vertex.originalId == firstVertex.id){
                        leftFirst = vertex;
                    }
                    else if (vertex.originalId == secondVertex.id){
                        leftSecond = vertex;
                    }
                }

                Vertex rightFirst = null;
                Vertex rightSecond = null;
                for (Vertex vertex : right){
                    if (vertex.originalId == firstVertex.id){
                        rightFirst = vertex;
                    }
                    else if (vertex.originalId == secondVertex.id){
                        rightSecond = vertex;
                    }
                }

                if (leftFirst != null && leftSecond != null && rightFirst != null && rightSecond != null){
                
                    // leftFirst -> rightSecond
                    HashSet<Vertex> neighborSet = adjacentMap.get(leftFirst);
                    if (neighborSet == null){
                        neighborSet = new HashSet<>();
                        adjacentMap.put(leftFirst,neighborSet);
                    }
                    neighborSet.add(rightSecond);
                    // leftFirst <- rightSecond
                    neighborSet = adjacentMap.get(rightSecond);
                    if (neighborSet == null){
                        neighborSet = new HashSet<>();
                        adjacentMap.put(rightSecond,neighborSet);
                    }
                    neighborSet.add(leftFirst);
                    
                    // leftSecond -> rightFirst
                    neighborSet = adjacentMap.get(leftSecond);
                    if (neighborSet == null){
                        neighborSet = new HashSet<>();
                        adjacentMap.put(leftSecond,neighborSet);
                    }
                    neighborSet.add(rightFirst);
                    // leftSecond <- rightFirst
                    neighborSet = adjacentMap.get(rightFirst);
                    if (neighborSet == null){
                        neighborSet = new HashSet<>();
                        adjacentMap.put(rightFirst,neighborSet);
                    }
                    neighborSet.add(leftSecond);
                }
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
                HashSet<Vertex> neighbors = adjacentMap.get(vertex);
                if (neighbors != null){
                    for (Vertex neighbor : neighbors){
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
            HashSet<Vertex> neighbors = adjacentMap.get(vertex);
            if (neighbors != null){
                for (Vertex neighbor : neighbors){
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

