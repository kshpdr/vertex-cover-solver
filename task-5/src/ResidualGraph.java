import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ResidualGraph {
    Vertex s = new Vertex("s", "s".hashCode());
    Vertex t = new Vertex("t", "t".hashCode());

    public HashMap<Vertex, HashSet<Vertex>> adjacentMap = new HashMap<>();

    public ResidualGraph(BipartiteGraph bipartiteGraph, Map<Vertex, Vertex> matching){

    }

}
