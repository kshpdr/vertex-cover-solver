public class Vertex implements Comparable<Vertex>{
    int label;
    Vertex(int label) {
        this.label = label;
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



        return this.label == ((Vertex) o).label;
    }

    @Override
    public int hashCode() {

        return this.label;
    }


    @Override
    public String toString() {
        return String.valueOf(this.label);
    }

    @Override
    public int compareTo(Vertex other) {
        return this.label - other.label;
    }

}
