package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CliqueCoverSolver {

    public static List<Vertex> appleFirstRule(HashMapGraph graph){
        List<Vertex> cliqueCoverSolution = new ArrayList<>();
        for (Vertex vertex : graph.getEdges().keySet()){
            if (graph.getEdges().get(vertex).isEmpty()){
                graph.getEdges().remove(vertex);
            }
            else if (graph.getEdges().get(vertex).size() == 1){
                cliqueCoverSolution.add(graph.getEdges().get(vertex).iterator().next());
            }
        }
        return cliqueCoverSolution;
    }

//    public static List<Vertex> findMinimumCliqueCover(HashMapGraph graph){
//        List<Vertex> solution = new ArrayList<>();
//        int k = 0;
//
//        while (solution.isEmpty()){
//            solution = branch(graph, k, solution);
//            k++;
//        }
//    }

//    public static List<Vertex> branch(HashMapGraph graph, int k, List<Vertex> solution){
//        if (isClique(graph, solution)) { return solution; }
//        reduce(G, k);
//    }

    public static void reduce(HashMapGraph graph, int k){

    }

    public static boolean isClique(HashMapGraph graph, List<Vertex> solution){
        for (Vertex vertex : graph.getEdges().keySet()) {
            if (!solution.contains(vertex)) {
                for (Vertex neighbour : graph.getEdges().get(vertex)) {
                    if (!solution.contains(neighbour)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        InputParser inputParser = new InputParser();

        List<String> stringEdges = inputParser.parseEdges();

        ArrayList<Edge> edges = new ArrayList<>();
        for (String stringEdge : stringEdges){
            String[] stringVertices = stringEdge.split(" ");
            Edge edge = new Edge(new Vertex(stringVertices[0]), new Vertex(stringVertices[1]));
            edges.add(edge);
        }

        HashMapGraph graph = new HashMapGraph(edges);

        // check_isClique.dimacs
        List<Vertex> solution = new ArrayList<>();
        solution.add(new Vertex("1"));
        solution.add(new Vertex("8"));
        solution.add(new Vertex("9"));
        System.out.println(isClique(graph, solution));
    }
}
