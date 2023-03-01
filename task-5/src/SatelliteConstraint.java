import java.util.HashMap;
import java.util.HashSet;

public class SatelliteConstraint implements Constraint {
    public HashMap<Vertex, HashSet<Vertex>> initialAdjList = new HashMap<>();

    public SatelliteConstraint(HashMap<Vertex, HashSet<Vertex>> initialAdjList){
        for (Vertex vertex : initialAdjList.keySet()){
            this.initialAdjList.put(vertex, new HashSet<>(initialAdjList.get(vertex)));
        }
    }

    public boolean isSatisfied(Graph graph, HashSet<Vertex> solution){
        for (Vertex vertex : solution){
            if (!initialAdjList.containsKey(vertex)) continue;
            for (Vertex neighbor : initialAdjList.get((vertex))){
                if (solution.contains(neighbor)) continue;
                HashSet<Vertex> exclusiveNeighbors = new HashSet<>(initialAdjList.get(vertex));
                exclusiveNeighbors.remove(neighbor);
                exclusiveNeighbors.removeAll(initialAdjList.get(neighbor));
                if (solution.containsAll(exclusiveNeighbors) && !exclusiveNeighbors.isEmpty()){
                    return false;
                }
            }
        }
        return true;
    }
}
