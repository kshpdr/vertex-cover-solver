import java.io.IOException;
import java.util.*;

public class ResidualGraph {
    public BipartiteGraph bipartiteGraph;
    public Map<Vertex, Vertex> matching;
    public Vertex s = new Vertex("s", -1);
    public Vertex t = new Vertex("t", -2);

    public HashMap<Vertex, HashSet<Edge>> adjMap = new HashMap<>();
    public HashSet<Edge> edges = new HashSet<>();

    public HashMap<Vertex, HashSet<Edge>> residualAdjMap = new HashMap<>();
    public HashSet<Edge> residualEdges = new HashSet<>();

    public HashSet<Vertex> lpOne = new HashSet<>();
    public HashSet<Vertex> lpZero = new HashSet<>();

    // for strongly connected components
    private int index = 0;
    private Stack<Vertex> stack = new Stack<>();
    private HashSet<Vertex> visited = new HashSet<>();

    public ResidualGraph(BipartiteGraph bipartiteGraph, Map<Vertex, Vertex> matching){
        this.bipartiteGraph = bipartiteGraph;
        this.matching = matching;

        adjMap.put(s, new HashSet<>());
        adjMap.put(t, new HashSet<>());
        residualAdjMap.put(s, new HashSet<>());
        residualAdjMap.put(t, new HashSet<>());

        for (Vertex vertex : bipartiteGraph.left){
            adjMap.putIfAbsent(vertex, new HashSet<>());
            // from s to left
            Edge startEdge = new Edge(s, vertex, 1, matching.containsKey(vertex) && !matching.get(vertex).name.equals("nil") ? 1 : 0);
            addEdge(startEdge);

            for (Vertex neighbor : bipartiteGraph.adjacentMap.get(vertex)){
                adjMap.putIfAbsent(neighbor, new HashSet<>());
                // from left to right
                Edge middleEdge = new Edge(vertex, neighbor, Integer.MAX_VALUE, matching.containsKey(vertex) && matching.get(vertex).equals(neighbor) ? 1 : 0);
                addEdge(middleEdge);

                // from right to t
                Edge endEdge = new Edge(neighbor, t, 1, matching.containsValue(neighbor) ? 1 : 0);
                addEdge(endEdge);
            }
        }
    }

    public void addEdge(Edge edge){
        edges.add(edge);
        adjMap.get(edge.from).add(edge);
        addEdgeToResidualGraph(edge);
    }

    public void addEdgeToResidualGraph(Edge edge){
        if (edge.capacity - edge.flow > 0) {
            Edge newEdge = new Edge(edge.from, edge.to, edge.capacity - edge.flow, 0);
            residualAdjMap.putIfAbsent(edge.from, new HashSet<>());
            residualAdjMap.get(edge.from).add(newEdge);
            residualEdges.add(newEdge);
        }
        if (edge.flow > 0) {
            Edge newEdge = new Edge(edge.to, edge.from, edge.flow, 0);
            residualAdjMap.putIfAbsent(edge.to, new HashSet<>());
            residualAdjMap.get(edge.to).add(newEdge);
            residualEdges.add(newEdge);
        }
    }

    public void computeLp(){
        for (Vertex left : bipartiteGraph.left){
            for (Vertex right : bipartiteGraph.right){
                if (!left.name.equals(right.name)) continue;
                if (residualEdges.contains(new Edge(s, left, 0, 0))){
                    if (!isReachableFromS(right)){
                        lpZero.add(left);
//                        removeEdgeWith(left, right);
                    }
                }
                else if (!residualEdges.contains(new Edge(s, left, 0, 0))){
                    if (isReachableFromS(right)){
                        lpOne.add(left);
//                        removeEdgeWith(left, right);
                    }
                }
                break;
            }
        }
    }

    public boolean isValidComponent(HashSet<Vertex> component){
        for (Vertex vertex : component){
            for (Edge edge : residualAdjMap.get(vertex)){
                if (edge.to.equals(s) || edge.to.equals(t)) continue;
                if (!component.contains(edge.to)){
                    return false;
                }
            }
        }
        return true;
    }

    public void removeEdgeWith(Vertex left, Vertex right){
        HashSet<Edge> edgesToRemove = new HashSet<>();
        HashSet<Edge> residualEdgesToRemove = new HashSet<>();

        for (Edge edge : this.edges){
            if (edge.from.name.equals(left.name) || edge.to.name.equals(left.name)) {
                edgesToRemove.add(edge);
                adjMap.remove(left);
                adjMap.remove(right);
                for (Vertex neighbor : adjMap.keySet()) {
                    adjMap.get(neighbor).removeIf(neighborEdge -> neighborEdge.to.name.equals(left.name));
                }
            }
        }

        for (Edge edge : this.residualEdges){
            if (edge.from.name.equals(left.name) || edge.to.name.equals(left.name)) {
                residualEdgesToRemove.add(edge);
                residualAdjMap.remove(left);
                residualAdjMap.remove(right);
                for (Vertex neighbor : residualAdjMap.keySet()) {
                    residualAdjMap.get(neighbor).removeIf(neighborEdge -> neighborEdge.to.name.equals(left.name));
                }
            }
        }

        this.edges.removeAll(edgesToRemove);
        this.residualEdges.removeAll(residualEdgesToRemove);
    }

    public boolean isReachableFromS(Vertex vertex){
        for (Edge edge : residualEdges){
            if (edge.from.equals(t)) continue;
            if (!edge.to.equals(vertex)) continue;
            for (Edge edge2 : residualEdges){
                if (edge2.from.equals(s) && edge2.to.equals(edge.from) && vertex.name.equals("46")){
//                    System.out.println("f");
//                    boolean was = edge2.from.equals(s);
                    return true;
                }
            }
        }
        return false;
    }

    public HashSet<Vertex> computeStronglyConnectedComponent(Vertex vertex){
        HashSet<Vertex> component = new HashSet<>();
        index = 0;
        stack.clear();
        visited.clear();
        tarjan(vertex, component);
        return component;
    }

    private void tarjan(Vertex vertex, HashSet<Vertex> component) {
        if (vertex.equals(s) || vertex.equals(t)) {
            return;
        }

        visited.add(vertex);
        vertex.index = index;
        vertex.lowLink = index;
        index++;
        stack.push(vertex);

        HashSet<Edge> neighbors = residualAdjMap.get(vertex);
        if (neighbors != null) {
            for (Edge edge : neighbors) {
                Vertex neighbor = edge.to;
                if (neighbor.equals(t) || neighbor.equals(s) || stack.contains(findMirror(neighbor))) continue;
                if (!visited.contains(neighbor)) {
                    tarjan(neighbor, component);
                    vertex.lowLink = Math.min(vertex.lowLink, neighbor.lowLink);
                } else if (stack.contains(neighbor)) {
                    vertex.lowLink = Math.min(vertex.lowLink, neighbor.index);
                }
            }
        }

        if (vertex.lowLink == vertex.index) {
            Vertex v = null;
            do {
                v = stack.pop();
                component.add(v);
            } while (v != vertex);
        }
    }



//    public void updateFlow(){
//        for (Vertex left : bipartiteGraph.left){
//            for (Vertex right : bipartiteGraph.right){
//                if (!left.name.equals(right.name) || lpOne.contains(left) || lpZero.contains(left)) continue;
//                boolean updateNeeded = true;
//                for (Edge edge : adjMap.get(left)){
//                    if (edge.flow == 1) updateNeeded = false;
//                }
//                if (updateNeeded) {
//                    for (Edge edge : adjMap.get(s)){
//                        if (edge.to.name.equals(left.name)) edge.flow = 0;
//                    }
//                    for (Edge edge : adjMap.get(right)){
//                        if (edge.to.equals(t)) edge.flow = 0;
//                    }
//                }
//                break;
//            }
//        }
//    }

    public void applyLpReduction(){
        while (true){
            HashSet<Vertex> component = new HashSet<>();
            for (Vertex vertex : residualAdjMap.keySet()){
                if (vertex.equals(s) || vertex.equals(t)) continue;
                component = computeStronglyConnectedComponent(vertex);
                HashSet<Vertex> leftComponent = new HashSet<>();
                HashSet<Vertex> rightComponent = new HashSet<>();
                for (Vertex node : component){
                    if (bipartiteGraph.left.contains(node)) leftComponent.add(node);
                    else rightComponent.add(node);
                }
                if (leftComponent.size() != rightComponent.size()) continue;
                if (!component.isEmpty() && component.size() % 2 == 0 && isValidComponent(component)) {
                    break;
                }
            }

            if (component.size() <= 1) break;

            for (Vertex vertex : component){
                if (bipartiteGraph.left.contains(vertex)) lpZero.add(vertex);
                if (bipartiteGraph.right.contains(vertex)) lpOne.add(vertex);
                removeEdgeWith(vertex, findMirror(vertex));
            }
        }
    }

    public Vertex findMirror(Vertex vertex){
        if (bipartiteGraph.left.contains(vertex)) {
            for (Vertex mirror : bipartiteGraph.right){
                if (vertex.name.equals(mirror.name)) return mirror;
            }
        }
        else {
            for (Vertex mirror : bipartiteGraph.left){
                if (vertex.name.equals(mirror.name)) return mirror;
            }
        }
        return null;
    }


//    public static void main(String[] args) throws IOException {
//        InputParser inputParser = new InputParser(null);
//        HashSet<String[]> edges = inputParser.getEdges();
//        Graph graph = new Graph(edges);
//        ResidualGraph residualGraph = new ResidualGraph(graph.getBipartiteGraph(), graph.getBipartiteGraph().findMaximumMatching());
//        residualGraph.computeLp();
////        residualGraph.updateFlow();
//        residualGraph.applyLpReduction();
//        System.out.println(residualGraph.lpOne);
//    }
}
