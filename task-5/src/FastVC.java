import javax.sound.midi.SysexMessage;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class FastVC {

    public static HashSet<Vertex> constructVertexCover(LightGraph graph){
        HashSet<Vertex> solution = new HashSet<>();
        for (Vertex vertex : graph.getAdjVertices().keySet()){
            for (Vertex neighbor : graph.getAdjVertices().get(vertex)){
                if (!solution.contains(vertex) && !solution.contains(neighbor)){
                    if (graph.getAdjVertices().get(vertex).size() > graph.getAdjVertices().get(neighbor).size()){
                        solution.add(vertex);
                        vertex.loss = 0;
                        break;
                    }
                    else{
                        solution.add(neighbor);
                        neighbor.loss = 0;
                    }
                }
            }
        }
        for (Vertex vertex : graph.getAdjVertices().keySet()) {
            for (Vertex neighbor : graph.getAdjVertices().get(vertex)) {
                if (solution.contains(vertex) && !solution.contains(neighbor)){
                    vertex.loss++;
                }
                else if (!solution.contains(vertex) && solution.contains(neighbor)){
                    neighbor.loss++;
                }
            }
        }
        //remove redundant vertices
        Iterator<Vertex> it = solution.iterator();
        while (it.hasNext()) {
            Vertex vertex = it.next();
            if (vertex.loss == 0){
                vertex.loss = null;
                vertex.gain = 0;
                it.remove();

                for (Vertex neighbor : graph.getAdjVertices().get(vertex)){
                    neighbor.loss++;
                }
//                updateLossAndGain(graph, solution);
            }
        }
        return solution;
    }

    public static LinkedList<String> fastVertexCover(LightGraph graph, int cutoff){
        long startTime = System.currentTimeMillis();
        int step = 1;
        HashSet<Vertex> solution = new HashSet<>();

        if (graph.getAdjVertices().keySet().size() == 0){
            return new LinkedList<>();
        }

        HashSet<Vertex> tempSolution = constructVertexCover(graph);
        solution = tempSolution;
        return getStringSolution(solution);
    }

    public static LinkedList<String> getStringSolution(HashSet<Vertex> vertices){
        LinkedList<String> solution = new LinkedList<>();
        for (Vertex vertex : vertices){
            solution.add(vertex.name);
        }
        return solution;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bi = new BufferedReader(new InputStreamReader(System.in));
//        BufferedReader bi = new BufferedReader(new FileReader("/Users/koselev/Desktop/AlgEng/algorithm-engineering/task-4/vc-data-students/2-social-networks/87-cnr-2000.graph.dimacs"));
//        BufferedReader bi = new BufferedReader(new FileReader("/Users/koselev/Desktop/AlgEng/algorithm-engineering/task-4/vc-data-students/2-social-networks/03-adjnoun.graph.dimacs"));
        StringBuilder sb = new StringBuilder();
        int solutionSize = 0;

        HashSet<String[]> edges = new HashSet<>();

        String line;
        LightGraph lightGraph = new LightGraph();
        HashMap<Integer, Vertex> vertices = new HashMap<>();
        while (((line = bi.readLine()) != null)) {
            if (line.contains("#")){
                Integer numVertices = Integer.parseInt(line.substring(1).split("\\s+")[0]);
            }
            else if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
//                edges.add(nodes);
                Vertex vertex = new Vertex(nodes[0], Integer.parseInt(nodes[0]));
                Vertex neighbor = new Vertex(nodes[1], Integer.parseInt(nodes[1]));
                vertices.putIfAbsent(Integer.parseInt(nodes[0]), vertex);
                vertices.putIfAbsent(Integer.parseInt(nodes[1]), neighbor);
                lightGraph.addEdge(vertices.get(Integer.parseInt(nodes[0])), vertices.get(Integer.parseInt(nodes[1])));
            }
        }

        System.out.println("#Graph is read");
        long start = System.currentTimeMillis();
        LinkedList<String> solution = fastVertexCover(lightGraph, 50);

        for (String v : solution){
            sb.append(v);
            sb.append("\n");
            solutionSize++;
        }

        sb.append("#sol size: ").append(solutionSize).append("\n");
        String resultStr = sb.toString();
        System.out.print(resultStr);

        long end = System.currentTimeMillis();
        float sec = (end - start) / 1000F;
        System.out.println("#time: " + sec);
    }
}