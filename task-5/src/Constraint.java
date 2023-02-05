import java.util.HashSet;

public interface Constraint {
    boolean isSatisfied(HashSet<Vertex> solution);
}
