import java.util.*;

public class Graph {
        private final Map<Vertex, HashSet<Vertex>> adjVertices=new HashMap<>();
        private final Map<Vertex, String> vertexStringMap = new HashMap<>();
        private final ArrayList<Vertex> arrayVertex = new ArrayList<>();

        private final Queue<Vertex> vertexHeap = new PriorityQueue<>(Collections.reverseOrder());





    public Graph( HashSet<String[]> edges) {
        int index = 0;


        Map<String, Vertex> stringVertexMap = new HashMap<>();

        for (String[] edge : edges) {


            if (!stringVertexMap.containsKey(edge[0])) {
                Vertex vertex1 = new Vertex(index);
                this.vertexStringMap.put(vertex1, edge[0]);
                this.adjVertices.put(vertex1, new HashSet<>());
                stringVertexMap.put(edge[0], vertex1);
                this.arrayVertex.add(vertex1);
                index++;
            }
            if (!stringVertexMap.containsKey(edge[1])) {
                Vertex vertex2 = new Vertex(index);
                this.vertexStringMap.put(vertex2, edge[1]);
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




    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        //sb.append("Vertices: ").append("\n");
        for (Vertex vertex :this.vertexStringMap.keySet()) {
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
        for (Vertex vertex :this.vertexStringMap.keySet()) {

            if (this.adjVertices.containsKey(vertex)) {
                isEmpty = isEmpty && this.adjVertices.get(vertex).isEmpty();
            }


        }
        return isEmpty;
    }

//    public Vertex[] getRandomEdge () {
//        Vertex[] randomEdge = new Vertex[2];
////        List<Vertex> keysAsArray = new ArrayList<Vertex>(adjVertices.keySet());
////        Random r = new Random();
////        Vertex firstVertex = keysAsArray.get(r.nextInt(keysAsArray.size()));
////        while(adjVertices.get(firstVertex).isEmpty()){
////            r = new Random();
////            firstVertex = keysAsArray.get(r.nextInt(keysAsArray.size()));
////        }
////        randomEdge[0] = new Vertex(firstVertex.label);
////        Random rand = new Random();
////        int size = adjVertices.get(firstVertex).size();
////        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
////        int i = 0;
////        for(Vertex vertex: adjVertices.get(firstVertex))
////        {
////            if (i == item)
////                randomEdge[1] = vertex;
////            i++;
////        }
//        for (Vertex vertex : adjVertices.keySet()) {
//            if(!adjVertices.get(vertex).isEmpty()){
//                randomEdge[0] = vertex;
//                break;
//            }
//        }
//
//        randomEdge[1] = adjVertices.get(randomEdge[0]).iterator().next();
//
//
//        return randomEdge;
//    }

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
        return vertexStringMap.get(vertex);

    }

    // Encode (G,k) as String for memorization
//    public String encodeGraph(int k){
//        StringBuilder sb = new StringBuilder();
//        ArrayList<Vertex> V = new ArrayList<>(this.vertexStringMap.keySet());
//        Collections.sort(V);
//
//        for (Vertex v : V){
//            sb.append(v.label);
//            sb.append(":");
//            ArrayList<Vertex> neighbours;
//            if(this.adjVertices.containsKey(v)) {
//                neighbours = new ArrayList<>(this.adjVertices.get(v));
//            }else{
//                neighbours = new ArrayList<>();
//            }
//            Collections.sort(neighbours);
//            for (Vertex v2 : neighbours){
//                sb.append(v2.label);
//                sb.append("-");
//            }
//            sb.append(",");
//        }
//        sb.append("#").append(k);
//        return sb.toString();
//    }

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
}
