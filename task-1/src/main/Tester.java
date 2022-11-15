package main;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static main.VertexCoverSolver.findMinimalVertexCover;

public class Tester {

    public static void main(String[] args) throws IOException {
        File dir = new File("vc-data-students/1-random/");
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.getName().contains("dimacs")){
                // parse dimacs file
                FileReader reader = new FileReader("vc-data-students/1-random/" + file.getName());
                InputParser inputParser = new InputParser(reader);

                List<String> stringEdges = inputParser.parseEdges();
                ArrayList<Edge> edges = new ArrayList<>();
                for (String stringEdge : stringEdges){
                    String[] stringVertices = stringEdge.split(" ");
                    Edge edge = new Edge(new Vertex(stringVertices[0]), new Vertex(stringVertices[1]));
                    edges.add(edge);
                }
                // create graph and construct bipartite graph
                HashMapGraph graph = new HashMapGraph(edges);
                BipartiteGraph bipartiteGraph = new BipartiteGraph(graph);

                // compute maximal matching bound
                int lowerBound = bipartiteGraph.findMaximumMatchingSize();
                lowerBound /= 2;

                // parse a solution from *.solution file
                BufferedReader br = new BufferedReader(new FileReader("vc-data-students/1-random/" + file.getName().substring(0, file.getName().length() - 6) + "solution"));
                String solution = br.readLine();

                // output the results
                System.out.println("Lower bound: " + lowerBound);
                System.out.println("Solution: " + solution);

                // throw an exception if test not passed
                assertEquals(String.valueOf(lowerBound), solution);
            }
        }
    }
}

