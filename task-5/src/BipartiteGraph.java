import java.util.*;

public class BipartiteGraph {
    public Vertex nilVertex = new Vertex("nil", -1);
    static final int INF = Integer.MAX_VALUE;

    public ArrayList<Vertex> left = new ArrayList<>();
    public ArrayList<Vertex> right = new ArrayList<>();
    public HashMap<Vertex,HashSet<Vertex>> adjacentMap = new HashMap<>();
    public HashMap<Integer,Integer[]> idMap = new HashMap<>();
    public int indexCounter = 0;

    HashMap<Vertex, Vertex> pairLeft, pairRight;
    HashMap<Vertex, Integer> dist;

    public BipartiteGraph(Graph graph) {
        nilVertex.dist = INF;
        for (Vertex vertex : graph.getVertices()) {
            Vertex v = new Vertex(vertex.name, indexCounter++);
            v.originalId = vertex.id;
            left.add(v);

            Vertex u = new Vertex(vertex.name, indexCounter++);
            u.originalId = vertex.id;
            right.add(u);

            Integer[] indices = {indexCounter-2,indexCounter-1};
            idMap.put(vertex.id,indices);
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
                    HashSet<Vertex> neighborSet = adjacentMap.computeIfAbsent(leftFirst, k -> new HashSet<>());
                    neighborSet.add(rightSecond);
                    // leftFirst <- rightSecond
                    neighborSet = adjacentMap.computeIfAbsent(rightSecond, k -> new HashSet<>());
                    neighborSet.add(leftFirst);
                    
                    // leftSecond -> rightFirst
                    neighborSet = adjacentMap.computeIfAbsent(leftSecond, k -> new HashSet<>());
                    neighborSet.add(rightFirst);
                    // leftSecond <- rightFirst
                    neighborSet = adjacentMap.computeIfAbsent(rightFirst, k -> new HashSet<>());
                    neighborSet.add(leftSecond);
                }
            }
        }
    }

    public void removeVertex(Vertex v){
        Integer[] indices = idMap.get(v.id);
        if (indices != null){
            Vertex vertex = new Vertex(v.name,indices[0]);
            int leftVertexIndex = left.indexOf(vertex);
            if (leftVertexIndex != -1){
                Vertex leftVertex = left.get(leftVertexIndex);
                removeVertexFromAdjacencyMap(leftVertex);
                left.remove(leftVertex);
            }

            vertex = new Vertex(v.name,indices[1]);
            int rightVertexIndex = right.indexOf(vertex);
            if (rightVertexIndex != -1){
                Vertex rightVertex = right.get(rightVertexIndex);
                removeVertexFromAdjacencyMap(rightVertex);
                right.remove(rightVertex);
            }
        }
    }

    public void addEdge(Vertex vertex, Vertex neighbor){
        Vertex leftFirst, leftSecond, rightFirst, rightSecond;
        if (!idMap.containsKey(vertex.id)) {
            leftFirst = new Vertex(vertex.name, indexCounter++);
            leftFirst.originalId = vertex.id;
            left.add(leftFirst);

            rightFirst = new Vertex(vertex.name, indexCounter++);
            rightFirst.originalId = vertex.id;
            right.add(rightFirst);

            Integer[] indices = {indexCounter - 2, indexCounter - 1};
            idMap.put(vertex.id, indices);
        }
        else {
            Integer[] idsVertex = idMap.get(vertex.id);
            leftFirst = new Vertex(vertex.name, idsVertex[0]);
            rightFirst = new Vertex(vertex.name, idsVertex[1]);
        }

        if (!idMap.containsKey(neighbor.id)) {
            leftSecond = new Vertex(neighbor.name, indexCounter++);
            leftSecond.originalId = neighbor.id;
            left.add(leftSecond);

            rightSecond = new Vertex(neighbor.name, indexCounter++);
            rightSecond.originalId = neighbor.id;
            right.add(rightSecond);

            Integer[] indices2 = {indexCounter-2,indexCounter-1};
            idMap.put(neighbor.id,indices2);
        }
        else {
            Integer[] idsNeighbors = idMap.get(neighbor.id);
            leftSecond = new Vertex(neighbor.name, idsNeighbors[0]);
            rightSecond = new Vertex(neighbor.name, idsNeighbors[1]);
        }
        // REMOVE: vertex from left,right lists
        if (!left.contains(leftFirst)) left.add(leftFirst);
        if (!left.contains(leftSecond)) left.add(leftSecond);
        if (!right.contains(rightFirst)) right.add(rightFirst);
        if (!right.contains(rightSecond)) right.add(rightSecond);
        // REMOVE: vertex from adjMap
        // leftFirst -> rightSecond
        HashSet<Vertex> neighborSet = adjacentMap.computeIfAbsent(leftFirst, k -> new HashSet<>());
        neighborSet.add(rightSecond);
        // leftFirst <- rightSecond
        neighborSet = adjacentMap.computeIfAbsent(rightSecond, k -> new HashSet<>());
        neighborSet.add(leftFirst);

        // leftSecond -> rightFirst
        neighborSet = adjacentMap.computeIfAbsent(leftSecond, k -> new HashSet<>());
        neighborSet.add(rightFirst);
        // leftSecond <- rightFirst
        neighborSet = adjacentMap.computeIfAbsent(rightFirst, k -> new HashSet<>());
        neighborSet.add(leftSecond);
    }


    private void removeVertexFromAdjacencyMap(Vertex v){
        HashSet<Vertex> neighbors = adjacentMap.remove(v);
        if (neighbors != null){
            for (Vertex n : neighbors){
                HashSet<Vertex> nextNeighbors = adjacentMap.get(n);
                if (nextNeighbors != null){
                    nextNeighbors.remove(v);
                    if (nextNeighbors.size() == 0){
                        adjacentMap.remove(n);
                    }
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
//                        if (!dist.containsKey(pairRight.get(neighbor)))
//                            System.out.println(123);
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



//    public static void main(String[] args) throws IOException {
//        InputParser inputParser = new InputParser();
//
//        //        Map<Vertex, Vertex> maximumMatching = graph.getBipartiteGraph().findMaximumMatching();
////        System.out.println(maximumMatching);
//    }
}

