package task1;

import java.util.ArrayList;
import java.util.List;
import static task1.Edge.*;

public class Graph {
    public int verticesAmount;
    public List<Edge> edges = new ArrayList<>();

    public Graph(int verticesAmount, List<String> stringEdges){
        this.verticesAmount = verticesAmount;

        for (String stringEdge : stringEdges){
            edges.add(new Edge(stringEdge.charAt(0), stringEdge.charAt(2)));
        }
    }

    @Override
    public String toString() {
        return "Graph{" +
                "verticesAmount=" + verticesAmount +
                ", edges=" + edges +
                '}';
    }
}
