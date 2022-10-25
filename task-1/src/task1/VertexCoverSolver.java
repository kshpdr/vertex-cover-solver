package task1;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class VertexCoverSolver {

    public void solveVertexCover(Graph G){
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputParser inputParser = new InputParser("example-input.txt");

        int verticesAmount = inputParser.parseVerticesAmount();
        List<String> stringEdges = inputParser.parseEdges();
        Graph graph = new Graph(verticesAmount, stringEdges);

        System.out.println(graph);
    }
}
