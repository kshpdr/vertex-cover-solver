package main;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class VertexCoverSolver {

    public static int recursiveSteps = -1;

    public static HashSet<Vertex> reduceOneDegreeVertices(HashMapGraph graph){
        HashSet<Vertex> partialSolution = new HashSet<>();
        HashSet<Vertex> verticesToDelete = new HashSet<>();
        for (Vertex vertex : graph.getEdges().keySet()){
            if (graph.getEdges().get(vertex).size() == 1){
                if (!verticesToDelete.contains(vertex)){
                    Vertex vertexToSave = graph.getEdges().get(vertex).iterator().next();
                    partialSolution.add(graph.getEdges().get(vertex).iterator().next());

                    verticesToDelete.add(vertexToSave);
                    verticesToDelete.add(vertex);
                }
            }
        }

        for (Vertex vertex : verticesToDelete){
            graph.deleteVertex(vertex);
        }
        graph.deleteEmptyAdjacentLists();
        return partialSolution;
    }

    public static List<Vertex> findMinimalVertexCover(HashMapGraph graph, int lowerBound){
        int k = lowerBound;
        while(true){
            List<Vertex> solution = solveVertexCover(new HashMapGraph(graph), k);
            if (solution != null){
                return solution;
            }
            k++;
        }
    }

    public static List<Vertex> solveVertexCover(HashMapGraph graph, int k){
        recursiveSteps++;

        List<Vertex> solution = new ArrayList<>();
        if (k < 0){ return null; }
        if (!graph.hasEdges()) { return solution; }
        Edge randomEdge = graph.getRandomEdge();

        HashSet<Vertex> edgesForFirstVertex = graph.edges.get(randomEdge.getFirstVertex());
        solution = solveVertexCover(graph.deleteVertex(randomEdge.getFirstVertex()), k-1);
        graph.addEdges(randomEdge.getFirstVertex(), edgesForFirstVertex);
        if (solution != null) {
            solution.add(randomEdge.getFirstVertex());
            return solution;
        }

        HashSet<Vertex> edgesForSecondVertex = graph.edges.get(randomEdge.getSecondVertex());
        solution = solveVertexCover(graph.deleteVertex(randomEdge.getSecondVertex()), k-1);
        graph.addEdges(randomEdge.getSecondVertex(), edgesForSecondVertex);
        if (solution != null) {
            solution.add(randomEdge.getSecondVertex());
            return solution;
        }

        return null;
    }

    public static void main(String[] args) throws IOException {
        // parse the input
        InputParser inputParser = new InputParser();
        List<String> stringEdges = inputParser.parseEdges();

        ArrayList<Edge> edges = new ArrayList<>();
        for (String stringEdge : stringEdges){
            String[] stringVertices = stringEdge.split(" ");
            Edge edge = new Edge(new Vertex(stringVertices[0]), new Vertex(stringVertices[1]));
            edges.add(edge);
        }

        // construct graph and corresponding bipartite graph
        HashMapGraph graph = new HashMapGraph(edges);
        BipartiteGraph bipartiteGraph = new BipartiteGraph(graph);

        // compute a lower bound and divide by 2 as vc is a half of max.matchin
        int lowerBound = bipartiteGraph.findMaximumMatchingSize();
        lowerBound /= 2;

        // construct a solution
        List<Vertex> solution = new ArrayList<>();
        solution.addAll(reduceOneDegreeVertices(graph));
        solution.addAll(solveVertexCover(graph, lowerBound));

        // print out solution vertices
        for (Vertex vertex : solution) {
            System.out.println(vertex);
        }
    }
}
