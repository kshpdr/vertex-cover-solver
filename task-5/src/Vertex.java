import java.util.HashSet;

public class Vertex {
    String name;
    int id;
    int originalId; // Used in Bipartite-Graph
    int degree;
    int complementDegree;
    int dist = 0;
    Integer loss = null;
    Integer gain = null;

    boolean merged;
    Vertex first, second;
    HashSet<Vertex> commonNeighbors;

    //need color for Clique Bound
    int color =-1;

    // for strongly connected components
    int index;
    int lowLink;

//    boolean active =true;


    Vertex(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    public void addMergeInfo(Vertex first, Vertex second, HashSet<Vertex> commonNeighbors){
        this.first = first;
        this.second = second;
        this.commonNeighbors = new HashSet<>(commonNeighbors);
    }

    // equals and hashCode

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        if (!(o instanceof Vertex)) {
            return false;
        }

        return this.id == ((Vertex) o).id;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {

        // sb.append("Vertices: ").append("\n");

        return "Vertex " + this.name + " degree " + this.degree + " color "+ this.color;
    }

//    @Override
//    public int compareTo(Vertex o) {
//
//        return Integer.compare(this.degree, o.degree);
//    }

}
