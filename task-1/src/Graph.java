import java.util.*;

public class Graph {
    private final Map<Vertex, HashSet<Vertex>> adjVertices=new HashMap<>();
    private final Map<Vertex, String> vertexStringMap = new HashMap<>();

    public Graph(Set<String> verticesString, HashSet<String[]> edges){
        int index = 0;
        Iterator<String> vertexIterator = verticesString.iterator();
        Map<String, Vertex> stringVertexMap = new HashMap<>();

        while(vertexIterator.hasNext()) {
            Vertex vertex = new Vertex(index);
            String vertexString = vertexIterator.next();
            this.vertexStringMap.put(vertex,vertexString);
            stringVertexMap.put(vertexString,vertex);
            this.adjVertices.put(vertex, new HashSet<>());
            index++;
        }

        for (String[] edge : edges) {
            this.adjVertices.get(stringVertexMap.get(edge[0])).add(stringVertexMap.get(edge[1]));
            this.adjVertices.get(stringVertexMap.get(edge[1])).add(stringVertexMap.get(edge[0]));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Vertex vertex :this.vertexStringMap.keySet()) {
            sb.append(vertex).append(": ");
            if (this.adjVertices.containsKey(vertex)) {
                for (Vertex adjacentVertex : this.adjVertices.get(vertex)) {
                    sb.append(adjacentVertex).append(" ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean isEmpty() {
        boolean isEmpty = true;
        for (Vertex vertex :this.vertexStringMap.keySet()) {
            if (this.adjVertices.containsKey(vertex)) {
                isEmpty = isEmpty && this.adjVertices.get(vertex).isEmpty();
            }
        }
        return isEmpty;
    }

    public Vertex[] getRandomEdge () {
        Vertex[] randomEdge = new Vertex[2];
        for (Vertex vertex : adjVertices.keySet()) {
            if(!adjVertices.get(vertex).isEmpty()){
                randomEdge[0] = vertex;
                break;
            }
        }
        randomEdge[1] = adjVertices.get(randomEdge[0]).iterator().next();
        return randomEdge;
    }

    HashSet<Vertex> removeVertex(Vertex vertexToRemove) {
        HashSet<Vertex> adjacentVertices = new HashSet<>();
        Vertex v = new Vertex(vertexToRemove.label);

        for (Vertex tmpVertex : this.adjVertices.keySet()) {
            if (this.adjVertices.get(tmpVertex).remove(v)){
                adjacentVertices.add(new Vertex(tmpVertex.label));
            }
        }
        if (adjVertices.containsKey(vertexToRemove)){

            adjacentVertices.addAll(adjVertices.get(vertexToRemove));
            adjVertices.remove(v);
        }

        return adjacentVertices;
    }

    String getVertexMapping(Vertex vertex) {
        return vertexStringMap.get(vertex);

    }

    // Encode (G,k) as String for memorization
    public String encodeGraph(int k){
        StringBuilder sb = new StringBuilder();
        ArrayList<Vertex> V = new ArrayList<>(this.vertexStringMap.keySet());
        Collections.sort(V);

        for (Vertex v : V){
            sb.append(v.label);
            sb.append(":");
            ArrayList<Vertex> neighbours;
            if(this.adjVertices.containsKey(v)) neighbours = new ArrayList<>(this.adjVertices.get(v));
            else neighbours = new ArrayList<>();
            Collections.sort(neighbours);
            for (Vertex v2 : neighbours){
                sb.append(v2.label);
                sb.append("-");
            }
            sb.append(",");
        }
        sb.append("#").append(k);
        return sb.toString();
    }

    void putVertexBack(Vertex originalVertex, HashSet<Vertex> neighbors){
        Vertex v1 = new Vertex(originalVertex.label);
        if(!adjVertices.containsKey(v1)) adjVertices.put(v1,new HashSet<>());

        for(Vertex neighbor: neighbors){
            Vertex v2 = new Vertex(neighbor.label);
            adjVertices.get(v1).add(v2);
            if(!adjVertices.containsKey(v2)) adjVertices.put(v2,new HashSet<>());
            adjVertices.get(v2).add(v1);
        }
    }
}
