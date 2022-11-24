public class Vertex implements Comparable<Vertex> {
    int label;
    int degree;
    int dist = 0;

    public Vertex(Integer label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Vertex)) {
            return false;
        }

        return this.label == ((Vertex) o).label;
    }

    @Override
    public int hashCode() {
        return this.label;
    }

    @Override
    public String toString() {
        return "Vertex " + this.label + " degree " + this.degree;
    }

    @Override
    public int compareTo(Vertex o) {
        return Integer.compare(this.degree, o.degree);
    }
}
