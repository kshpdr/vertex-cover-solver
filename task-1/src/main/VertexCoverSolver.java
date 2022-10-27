package main;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VertexCoverSolver {

    public static List<Vertex> findMinimalVertexCover(Graph G){
        int k = 0;
        while(true){
            List<Vertex> solution = solveVertexCover(G, k);
            if (solution != null){
//                System.out.println(solution);
                return solution;
            }
            k++;
        }
    }

    public static List<Vertex> solveVertexCover(Graph graph, int k){
        List<Vertex> solution = new ArrayList<>();
        if (k < 0){ return null; }
        if (!graph.hasEdges()) { return new ArrayList<>(); }
        Edge randomEdge = graph.getRandomEdge();

        solution = solveVertexCover(new Graph(graph).deleteVertex(randomEdge.getFirstVertex()), k-1);
        if (solution != null) {
            solution.add(randomEdge.getFirstVertex());
            return solution;
        }

        solution = solveVertexCover(new Graph(graph).deleteVertex(randomEdge.getSecondVertex()), k-1);
        if (solution != null) {
            solution.add(randomEdge.getSecondVertex());
            return solution;
        }

        return null;
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputParser inputParser = new InputParser(args[0]);

        List<String> stringEdges = inputParser.parseEdges();

        ArrayList<Edge> edges = new ArrayList<>();
        for (String stringEdge : stringEdges){
            Edge edge = new Edge(new Vertex(stringEdge.substring(0, 1)), new Vertex(stringEdge.substring(2,3)));
            edges.add(edge);
        }

        Graph graph = new Graph(edges);

//        System.out.println(graph);
//        System.out.println(graph.getRandomEdge());
//
//        System.out.println(findMinimalVertexCover(graph).size());
        List<Vertex> solution = solveVertexCover(graph, findMinimalVertexCover(graph).size());
        for (Vertex vertex : solution){
            System.out.println(vertex);
        }
    }
}
