import java.util.HashMap;
import java.util.HashSet;

public class NeighborsConstraint implements Constraint {
    public HashMap<Vertex, HashSet<Vertex>> initialAdjList;

    public NeighborsConstraint(HashMap<Vertex, HashSet<Vertex>> initialAdjList){
        this.initialAdjList = new HashMap<>(initialAdjList);
    }

    public boolean isSatisfied(HashSet<Vertex> solution){
        for (Vertex vertex : solution) {
            if (initialAdjList.get(vertex).size() != 0 && solution.containsAll(initialAdjList.get(vertex)) ) {
                return false;
            }
        }
        return true;
    }
}
