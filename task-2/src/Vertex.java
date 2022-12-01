public class Vertex {
    String name;
    int id;
    int dist = 0;

    public Vertex(Integer id, String name) {
        this.id = id;
        this.name = name;
    boolean active =true;


    Vertex(Integer label) {
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

        return this.id == ((Vertex) o).id;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return "Vertex " + this.name;
    }
}
