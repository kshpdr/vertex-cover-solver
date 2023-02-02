import java.util.HashMap;
import java.util.HashSet;

public class NeighborsConstraint implements Constraint {
    public HashMap<Vertex, HashSet<Vertex>> initialAdjList = new HashMap<>();

    public NeighborsConstraint(HashMap<Vertex, HashSet<Vertex>> initialAdjList){
        this.initialAdjList = new HashMap<>(initialAdjList);
    }

    public boolean isSatisfied(HashSet<Vertex> solution){
        for (Vertex vertex : solution) {
            if (solution.containsAll(initialAdjList.get(vertex)) && initialAdjList.get(vertex).size() != 0) {
                return false;
            }
        }
        return true;
    }
}
