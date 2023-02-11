import java.util.*;

public class Vertex {
    String name;
    int id;
    int originalId; // Used in Bipartite-Graph
    int degree;
    int complementDegree;
    int dist = 0;
    
    // Global variables required for TWIN-rule
    boolean merged = false;
    Vertex u,v;
    HashSet<Vertex> Nu;

    //need color for Clique Bound
    int color =-1;

//    boolean active =true;


    Vertex(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    public void addMergeInformation(Vertex v, Vertex u, HashSet<Vertex> Nu){
        this.merged = true;
        this.v = v;
        this.u = u;
        this.Nu = Nu;
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
        return this.id;
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
