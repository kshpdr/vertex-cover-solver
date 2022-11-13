import java.util.*;

public class Graph {
        private final Map<Vertex, HashSet<Vertex>> adjVertices=new HashMap<>();
        private final ArrayList<String> vertexStringMap = new ArrayList<>();
        private final ArrayList<Vertex> arrayVertex = new ArrayList<>();

        private final Queue<Vertex> vertexHeap = new PriorityQueue<>(Collections.reverseOrder());





    public Graph( HashSet<String[]> edges) {
        int index = 0;


        Map<String, Vertex> stringVertexMap = new HashMap<>();

        for (String[] edge : edges) {


            if (!stringVertexMap.containsKey(edge[0])) {
                Vertex vertex1 = new Vertex(index);
                this.vertexStringMap.add(edge[0]);
                this.adjVertices.put(vertex1, new HashSet<>());
                stringVertexMap.put(edge[0], vertex1);
                this.arrayVertex.add(vertex1);
                index++;
            }
            if (!stringVertexMap.containsKey(edge[1])) {
                Vertex vertex2 = new Vertex(index);
                this.vertexStringMap.add(edge[1]);
                stringVertexMap.put(edge[1], vertex2);
                this.adjVertices.put(vertex2, new HashSet<>());
                this.arrayVertex.add(vertex2);
                index++;
            }

            this.adjVertices.get(stringVertexMap.get(edge[0])).add(stringVertexMap.get(edge[1]));

            this.adjVertices.get(stringVertexMap.get(edge[1])).add(stringVertexMap.get(edge[0]));

            //increasing degrees of vertices
            stringVertexMap.get(edge[0]).degree++;
            stringVertexMap.get(edge[1]).degree++;

        }

        //Putting the vertices in the heap
        vertexHeap.addAll(arrayVertex);

    }

    public Graph(){

    }




    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        //sb.append("Vertices: ").append("\n");
        for (Vertex vertex :this.arrayVertex) {
            sb.append(vertex).append(": ");
            if (this.adjVertices.containsKey(vertex)) {
                for (Vertex adjacentVertex : this.adjVertices.get(vertex)) {
                    sb.append(adjacentVertex).append(" ");
                }
            }

            sb.append("\n");

        }
        sb.append("Heap: ").append(this.vertexHeap);


        return sb.toString();
    }

    public boolean isEmpty() {
        boolean isEmpty = true;
        for (Vertex vertex :this.adjVertices.keySet()) {

                isEmpty = isEmpty && this.adjVertices.get(vertex).isEmpty();



        }
        return isEmpty;
    }


    HashSet<Vertex> removeVertex(Vertex vertexToRemove) {
        HashSet<Vertex> adjacentVertices = new HashSet<>();
//        Vertex v = new Vertex(vertexToRemove.label);

        for (Vertex tmpVertex : this.adjVertices.keySet()) {
            if (this.adjVertices.get(tmpVertex).remove(vertexToRemove)){
                this.arrayVertex.get(tmpVertex.label).degree--;
                this.vertexHeap.remove(tmpVertex);
                this.vertexHeap.add(tmpVertex);
                adjacentVertices.add(new Vertex(tmpVertex.label));
            }
        }
        if (adjVertices.containsKey(vertexToRemove)){

            adjacentVertices.addAll(adjVertices.get(vertexToRemove));
            this.arrayVertex.get(vertexToRemove.label).degree=0;
            this.vertexHeap.remove(vertexToRemove);
            adjVertices.remove(vertexToRemove);
        }

        return adjacentVertices;

    }

    String getVertexMapping(Vertex vertex) {
        return vertexStringMap.get(vertex.label);

    }


    void putVertexBack(Vertex originalVertex, HashSet<Vertex> neighbors){


        if(!adjVertices.containsKey(originalVertex)) adjVertices.put(originalVertex,new HashSet<>());

        for(Vertex neighbor: neighbors){

            adjVertices.get(originalVertex).add(neighbor);
            this.arrayVertex.get(originalVertex.label).degree++;
            this.vertexHeap.remove(originalVertex);
            this.vertexHeap.add(this.arrayVertex.get(originalVertex.label));
            if(!adjVertices.containsKey(neighbor)) adjVertices.put(neighbor,new HashSet<>());
            adjVertices.get(neighbor).add(originalVertex);
            this.arrayVertex.get(neighbor.label).degree++;
            this.vertexHeap.remove(neighbor);
            this.vertexHeap.add(this.arrayVertex.get(neighbor.label));


        }


    }

    HashMap<Vertex,HashSet<Vertex>> removeSetofVertices(HashSet<Vertex> verticesToRemove){

        HashMap<Vertex,HashSet<Vertex>> results = new HashMap<>();

        Iterator<Vertex> it = verticesToRemove.iterator();
        Vertex tmpVertex;
        while (it.hasNext()){
            tmpVertex = it.next();
            results.put(tmpVertex,this.removeVertex(tmpVertex ));
        }
        return results;

    }

    void putManyVerticesBack(HashMap<Vertex,HashSet<Vertex>> verticesBack){

        for (Map.Entry<Vertex, HashSet<Vertex>> entry : verticesBack.entrySet()) {
            putVertexBack(entry.getKey(),entry.getValue());
        }


    }

    Vertex getNextNode(){
        return this.vertexHeap.poll();
    }


    public String[] getMultipleMappings(HashSet<Vertex> setVertices) {
        String[] result = new String[setVertices.size()];

        Iterator<Vertex> it = setVertices.iterator();
        for (int i =0; i<result.length;i++){
            result[i] = this.getVertexMapping(it.next());
        }
        return result;

    }

    Graph getCopy(){
        Graph copy = new Graph();
        //
        for (Vertex vertex: this.arrayVertex){
            Vertex vertexCopy = new Vertex(vertex.label);
            vertexCopy.degree = vertex.degree;
            copy.arrayVertex.add(vertexCopy);
            copy.vertexStringMap.add(this.vertexStringMap.get(vertexCopy.label));

        }

        for (Vertex vertex: this.adjVertices.keySet()){
                for(Vertex neighbor:  this.adjVertices.get(vertex)){
                    if(copy.adjVertices.containsKey(vertex)){
                        copy.adjVertices.get(copy.arrayVertex.get(vertex.label)).add(copy.arrayVertex.get(neighbor.label));
                    }else{
                        HashSet<Vertex> tmpNeighbors = new HashSet<>();
                        tmpNeighbors.add(copy.arrayVertex.get(neighbor.label));
                        copy.adjVertices.put(copy.arrayVertex.get(vertex.label), tmpNeighbors);

                    }

                }

        }

        copy.vertexHeap.addAll(copy.arrayVertex);
        return copy;

    }

    HashSet<Vertex> getMaximalClique(){
        HashSet<Vertex> clique = new HashSet<>();
        Vertex firstVertex = this.getNextNode();
        clique.add(firstVertex);
        for (Vertex vertex: this.arrayVertex){
            if(!clique.contains(vertex)) {

                if (this.adjVertices.containsKey(vertex) && this.adjVertices.get(vertex).containsAll(clique)) {


                    clique.add(vertex);
                }
            }

        }

        return clique;

    }

    HashSet<HashSet<Vertex>> getApproximateMaximumClique(){
        Graph copyGraph = this.getCopy();
        HashSet<HashSet<Vertex>> result = new HashSet<>();
        HashSet<Vertex> maxClique;
        while(!copyGraph.vertexHeap.isEmpty()){
            maxClique = copyGraph.getMaximalClique();
            result.add(maxClique);
            copyGraph.removeSetofVertices(maxClique);
        }
        return result;


    }

    int getCliqueLowerBound(){
        return this.arrayVertex.size()-this.getApproximateMaximumClique().size();
    }



}
