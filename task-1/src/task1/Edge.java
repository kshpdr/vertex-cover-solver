package task1;

public class Edge {
    public final char v;
    public final char w;

    public Edge(char v, char w){
        this.v = v;
        this.w = w;
    }

    @Override
    public String toString() {
        return "Edge{" + v + "-" + w + "}";
    }
}
