import javax.sound.midi.SysexMessage;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class FastVC {

    public static void updateLossAndGain(LightGraph graph, HashSet<Vertex> solution) {
        for (Vertex vertex : graph.getAdjVertices().keySet()) {
            if (solution.contains(vertex)){
                vertex.loss = 0;
                vertex.gain = null;
                for (Vertex neighbor : graph.getAdjVertices().get(vertex)) {
                    if (solution.contains(vertex) && !solution.contains(neighbor)) {
                        vertex.loss++;
                    }
                }
            }
            else {
                vertex.gain = 0;
                vertex.loss = null;
                for (Vertex neighbor : graph.getAdjVertices().get(vertex)){
                    if (!solution.contains(neighbor)){
                        vertex.gain++;
                    }
                }
            }
        }
    }

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

    public static Vertex getMinLossVertex(HashSet<Vertex> vertices){
        Vertex minLossVertex = vertices.iterator().next();
        for (Vertex vertex : vertices){
            if (vertex.loss < minLossVertex.loss){
                minLossVertex = vertex;
            }
        }
        return minLossVertex;
    }

    public static Vertex chooseRandomVertex(HashSet<Vertex> vertices){
        Vertex bestVertex = vertices.iterator().next();
        for (int i = 0; i < 50; i++){
            Vertex vertex = vertices.iterator().next();
            if (bestVertex.loss < vertex.loss){
                bestVertex = vertex;
            }
        }
        return bestVertex;
    }

    public static LinkedList<String> fastVertexCover(LightGraph graph, int cutoff){
        long startTime = System.currentTimeMillis();
        int step = 1;
        HashSet<Vertex> solution = new HashSet<>();

        if (graph.getAdjVertices().keySet().size() == 0){
            return new LinkedList<>();
        }

        HashSet<Vertex> tempSolution = constructVertexCover(graph);
        for (Vertex vertex : graph.getAdjVertices().keySet()){
            if (!tempSolution.contains(vertex)){
                vertex.gain = 0;
            }
        }
        int minSize = tempSolution.size();
        while (System.currentTimeMillis() - startTime < cutoff * 1e3){
            if (graph.isVertexCover(tempSolution)){
                System.out.println("#Found another VC");
                solution = new HashSet<>(tempSolution);

                System.out.println(tempSolution.size());
                if (tempSolution.size() < minSize) {
                    minSize = tempSolution.size();
                    System.out.println("#Found better solution: " + minSize);
                }
                if (step % 5 == 0 && tempSolution.size() == minSize) {
                    break;
                }

                Vertex minLossVertex = getMinLossVertex(tempSolution);
                tempSolution.remove(minLossVertex);
                minLossVertex.gain = minLossVertex.loss;
                minLossVertex.loss = null;
                for (Vertex neighbor : graph.getAdjVertices().get(minLossVertex)){
                    if (solution.contains(neighbor)){
                        neighbor.loss++;
                    }
                    else {
                        neighbor.gain++;
                    }
                }
//                updateLossAndGain(graph, tempSolution);
                continue;
            }
            Vertex u = chooseRandomVertex(tempSolution);
            tempSolution.remove(u);
            updateLossAndGain(graph, tempSolution);
            Vertex v = graph.getVertexWithGreaterGainFromRandomEdge(tempSolution, u);
            tempSolution.add(v);
            updateLossAndGain(graph, tempSolution);
            step++;
        }
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
