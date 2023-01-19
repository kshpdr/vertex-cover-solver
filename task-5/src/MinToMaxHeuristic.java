import java.util.*;

public class MinToMaxHeuristic {
    
    // WHILE ITERATION: Compute min-to-max heuristic for given graph class instance
    public static int getUpperBound(Graph graph){
        
        // ------------------- INITIALIZE OWN GRAPH DATASTRUCTURES -------------------------- \\ 
        
        HashMap<MyVertex,HashSet<MyVertex>> adjMap = new HashMap<>();
        HashMap<String,MyVertex> idMap = new HashMap<>();
        int idCounter = 0;
        
        HashMap<Vertex,HashSet<Vertex>> graphAdjMap = graph.getAdjVertices();
        for (Vertex v1 : graphAdjMap.keySet()){
            for (Vertex v2 : graphAdjMap.get(v1)){
                // Skip reverse edges
                if (v1.id > v2.id) continue;

                String[] edge = {v1.name,v2.name};
                MyVertex u = idMap.get(edge[0]);
                if (u == null){
                    u = new MyVertex(edge[0],idCounter++);
                    idMap.put(edge[0],u);
                }
                MyVertex v = idMap.get(edge[1]);
                if (v == null){
                    v = new MyVertex(edge[1],idCounter++);
                    idMap.put(edge[1],v);
                }
                
                // Add (u -> v) to graph
                HashSet<MyVertex> neighbors = adjMap.get(u);
                if (neighbors == null){
                    neighbors = new HashSet<>();
                    adjMap.put(u,neighbors);
                }
                neighbors.add(v);
                // Add (v -> u) to graph
                neighbors = adjMap.get(v);
                if (neighbors == null){
                    neighbors = new HashSet<>();
                    adjMap.put(v,neighbors);
                }
                neighbors.add(u);
            }
        }

        // Initialize additional datastructure
        HashMap<Integer,HashSet<MyVertex>> degreeMap = new HashMap<>();
        TreeSet<Integer> degrees = new TreeSet<>();
        MyVertex minDegreeVertex = null;
        int minDegree = adjMap.keySet().size();
        // Loop all vertices in graph
        for (MyVertex v : adjMap.keySet()){
            // Save min-degree-vertex (for future use ...)
            int degree = adjMap.get(v).size();
            if (degree < minDegree){
                minDegreeVertex = v;
                minDegree = degree;
            }
            degrees.add(degree);

            // Add vertex to degreeMap datastructure
            HashSet<MyVertex> bucket = degreeMap.get(degree);
            if (bucket == null){
                bucket = new HashSet<>();
                degreeMap.put(degree,bucket);
            }
            bucket.add(v);
        }


        // ------------------- COMPUTE MIN2MAX HEURISTIC -------------------------- \\

        int solutionSize = 0;
        // Apply (min-to-min) greedy heuristic
        while (adjMap.size() > 0){
            // Choose min-degree vertex tmp
            MyVertex tmp = minDegreeVertex;
            // Find next min-degree vertex from neighbors from tmp
            MyVertex v = null;
            int maxDeg = 0;
            for (MyVertex n : adjMap.get(tmp)){
                int d = adjMap.get(n).size();
                if (maxDeg < d){
                    maxDeg = d;
                    v = n;
                }
            }

            // Add v to the VC-solution
            solutionSize++;
            
            // remove v from graph (adjacency datastructure)
            HashSet<MyVertex> neighbors = adjMap.remove(v);
            // remove v from (degree datastructure)
            int degree = neighbors.size();
            HashSet<MyVertex> bucket = degreeMap.get(degree);
            bucket.remove(v);
            if (bucket.size() == 0) {
                degreeMap.remove(degree);
                degrees.remove(degree);
            }

            // remove v from it's neighbors
            for (MyVertex n : neighbors){
                // remove v from neighbor n (adjacency datasturcture)
                HashSet<MyVertex> nextNeighbors = adjMap.get(n);
                // decrease degree of neighbor n (delete from bucket ... later: put one bucket below)
                HashSet<MyVertex> oldBucket = degreeMap.get(nextNeighbors.size());
                oldBucket.remove(n);
                if (oldBucket.size() == 0) {
                    degreeMap.remove(nextNeighbors.size());
                    degrees.remove(nextNeighbors.size());
                }
                
                nextNeighbors.remove(v);
                // delete 0-degree vertices
                if (nextNeighbors.size() == 0) adjMap.remove(n);
                else {
                    // decrease degree of neigbor n (re-insert one bucket below)
                    HashSet<MyVertex> nextBucket = degreeMap.get(nextNeighbors.size());
                    if (nextBucket == null){
                        nextBucket = new HashSet<>();
                        degreeMap.put(nextNeighbors.size(),nextBucket);
                        degrees.add(nextNeighbors.size());
                    }
                    nextBucket.add(n);
                }
            }
            // Re-calculate min-degree vertex
            if (degrees.size() > 0){
                minDegree = degrees.first();
                minDegreeVertex = degreeMap.get(minDegree).iterator().next();
            }
            else {
                minDegree = 0;
                minDegreeVertex = null;
            }
        }
        // Return solution size of min2max-heuristic as upper-bound
        return solutionSize;
    }

    // INITIAL: Compute min-to-max heuristic for given HashSet (of edges)
    public static int getUpperBound(HashSet<String[]> edges){

        // ------------------- INITIALIZE OWN GRAPH DATASTRUCTURES -------------------------- \\ 
        
        HashMap<MyVertex,HashSet<MyVertex>> adjMap = new HashMap<>();
        HashMap<String,MyVertex> idMap = new HashMap<>();
        int idCounter = 0;
        
        for (String[] edge : edges){
            MyVertex u = idMap.get(edge[0]);
            if (u == null){
                u = new MyVertex(edge[0],idCounter++);
                idMap.put(edge[0],u);
            }
            MyVertex v = idMap.get(edge[1]);
            if (v == null){
                v = new MyVertex(edge[1],idCounter++);
                idMap.put(edge[1],v);
            }
            
            // Add (u -> v) to graph
            HashSet<MyVertex> neighbors = adjMap.get(u);
            if (neighbors == null){
                neighbors = new HashSet<>();
                adjMap.put(u,neighbors);
            }
            neighbors.add(v);
            // Add (v -> u) to graph
            neighbors = adjMap.get(v);
            if (neighbors == null){
                neighbors = new HashSet<>();
                adjMap.put(v,neighbors);
            }
            neighbors.add(u);
        }

        // Initialize additional datastructure
        HashMap<Integer,HashSet<MyVertex>> degreeMap = new HashMap<>();
        TreeSet<Integer> degrees = new TreeSet<>();
        MyVertex minDegreeVertex = null;
        int minDegree = adjMap.keySet().size();
        // Loop all vertices in graph
        for (MyVertex v : adjMap.keySet()){
            // Save min-degree-vertex (for future use ...)
            int degree = adjMap.get(v).size();
            if (degree < minDegree){
                minDegreeVertex = v;
                minDegree = degree;
            }
            degrees.add(degree);

            // Add vertex to degreeMap datastructure
            HashSet<MyVertex> bucket = degreeMap.get(degree);
            if (bucket == null){
                bucket = new HashSet<>();
                degreeMap.put(degree,bucket);
            }
            bucket.add(v);
        }


        // ------------------- COMPUTE MIN2MAX HEURISTIC -------------------------- \\

        int solutionSize = 0;
        // Apply (min-to-min) greedy heuristic
        while (adjMap.size() > 0){
            // Choose min-degree vertex tmp
            MyVertex tmp = minDegreeVertex;
            // Find next min-degree vertex from neighbors from tmp
            MyVertex v = null;
            int maxDeg = 0;
            for (MyVertex n : adjMap.get(tmp)){
                int d = adjMap.get(n).size();
                if (maxDeg < d){
                    maxDeg = d;
                    v = n;
                }
            }

            // Add v to the VC-solution
            solutionSize++;
            
            // remove v from graph (adjacency datastructure)
            HashSet<MyVertex> neighbors = adjMap.remove(v);
            // remove v from (degree datastructure)
            int degree = neighbors.size();
            HashSet<MyVertex> bucket = degreeMap.get(degree);
            bucket.remove(v);
            if (bucket.size() == 0) {
                degreeMap.remove(degree);
                degrees.remove(degree);
            }

            // remove v from it's neighbors
            for (MyVertex n : neighbors){
                // remove v from neighbor n (adjacency datasturcture)
                HashSet<MyVertex> nextNeighbors = adjMap.get(n);
                // decrease degree of neighbor n (delete from bucket ... later: put one bucket below)
                HashSet<MyVertex> oldBucket = degreeMap.get(nextNeighbors.size());
                oldBucket.remove(n);
                if (oldBucket.size() == 0) {
                    degreeMap.remove(nextNeighbors.size());
                    degrees.remove(nextNeighbors.size());
                }
                
                nextNeighbors.remove(v);
                // delete 0-degree vertices
                if (nextNeighbors.size() == 0) adjMap.remove(n);
                else {
                    // decrease degree of neigbor n (re-insert one bucket below)
                    HashSet<MyVertex> nextBucket = degreeMap.get(nextNeighbors.size());
                    if (nextBucket == null){
                        nextBucket = new HashSet<>();
                        degreeMap.put(nextNeighbors.size(),nextBucket);
                        degrees.add(nextNeighbors.size());
                    }
                    nextBucket.add(n);
                }
            }
            // Re-calculate min-degree vertex
            if (degrees.size() > 0){
                minDegree = degrees.first();
                minDegreeVertex = degreeMap.get(minDegree).iterator().next();
            }
            else {
                minDegree = 0;
                minDegreeVertex = null;
            }
        }
        // Return solution size of min2max-heuristic as upper-bound
        return solutionSize;
    }
}

class MyVertex {
    String name;
    int id;

    public MyVertex(String name, int id){
        this.name = name;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        else if (!(o instanceof MyVertex)) return false;
        else return this.id == ((MyVertex) o).id;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.name;
    }
}