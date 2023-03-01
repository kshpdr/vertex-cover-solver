import java.util.HashMap;
import java.util.HashSet;

public class NeighborsConstraint implements Constraint {
    public HashMap<Vertex, HashSet<Vertex>> initialAdjList;

    public NeighborsConstraint(HashMap<Vertex, HashSet<Vertex>> initialAdjList){
        this.initialAdjList = new HashMap<>(initialAdjList);
    }

    public boolean isSatisfied(Graph graph, HashSet<Vertex> solution){
        for (Vertex vertex : solution) {
            if (!initialAdjList.containsKey(vertex)) continue;
            if (solution.containsAll(initialAdjList.get(vertex)) && !initialAdjList.get(vertex).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
