import javax.sound.midi.SysexMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;

public class FastVC {

    public static void updateLossAndGain(Graph graph, HashSet<Vertex> solution) {
        for (Vertex vertex : graph.getVertices()) {
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

    public static HashSet<Vertex> constructVertexCover(Graph graph){
        HashSet<Vertex> solution = new HashSet<>();
        for (Vertex vertex : graph.getVertices()){
            for (Vertex neighbor : graph.getAdjVertices().get(vertex)){
                if (!solution.contains(vertex) && !solution.contains(neighbor)){
                    if (vertex.degree > neighbor.degree){
                        solution.add(vertex);
                    }
                    else{
                        solution.add(neighbor);
                    }
                }
            }
        }
        //calculate loss of vertices in solution
        for (Vertex vertex : solution){
            vertex.loss = 0;
        }
        for (Vertex vertex : graph.getVertices()) {
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
        for (Vertex vertex : solution){
            if (vertex.loss == 0){
                vertex.loss = null;
                vertex.gain = 0;
                solution.remove(vertex);
                updateLossAndGain(graph, solution);
            }
        }
        return solution;
    }

    public static void removeMinLossVertex(HashSet<Vertex> vertices){
        Vertex minLossVertex = vertices.iterator().next();
        for (Vertex vertex : vertices){
            if (vertex.loss < minLossVertex.loss){
                minLossVertex = vertex;
            }
        }
        vertices.remove(minLossVertex);
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

    public static LinkedList<String> fastVertexCover(Graph graph, int cutoff){
        long startTime = System.nanoTime();
        int step = 1;

        HashSet<Vertex> solution = new HashSet<>();
        HashSet<Vertex> tempSolution = constructVertexCover(graph);
        for (Vertex vertex : graph.getVertices()){
            if (!tempSolution.contains(vertex)){
                vertex.gain = 0;
            }
        }
        while (true){
            if (step % 10 == 0) {
                long finishTime = System.nanoTime();
                long elapsedTime = finishTime - startTime;
                long cutoffTimeInNanos = (long) (cutoff * 1e9);
                if (elapsedTime >= cutoffTimeInNanos) {
                    break;
                }
            }

            if (graph.isVertexCover(tempSolution)){
                solution = new HashSet<>(tempSolution);
                removeMinLossVertex(tempSolution);
                updateLossAndGain(graph, tempSolution);
                continue;
            }
            Vertex u = chooseRandomVertex(tempSolution);
            tempSolution.remove(u);
            updateLossAndGain(graph, tempSolution);
            Vertex v = graph.getVertexWithGreaterGain(tempSolution);
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
        StringBuilder sb = new StringBuilder();
        int solutionSize = 0;

        HashSet<String[]> edges = new HashSet<>();

        String line;
        while (((line = bi.readLine()) != null)) {
            if (!line.contains("#") && !line.isEmpty()) {
                String[] nodes = line.split("\\s+");
                edges.add(nodes);
            }
        }

        long start = System.currentTimeMillis();
        Graph graph = new Graph(edges);
        LinkedList<String> solution = fastVertexCover(graph, 2);

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
