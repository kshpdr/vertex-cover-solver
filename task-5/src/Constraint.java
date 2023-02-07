import java.util.HashSet;

public interface Constraint {
    boolean isSatisfied(Graph graph, HashSet<Vertex> solution);
}
