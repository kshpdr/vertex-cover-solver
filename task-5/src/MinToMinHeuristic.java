import java.util.*;

public class MinToMinHeuristic {
    public static HashSet<Vertex> getUpperBoundMinToMin(HashMap<Vertex,HashSet<Vertex>> adjMap){
        HashSet<Vertex> solution = new HashSet<>();
        // Initialize additional datastructure
        HashMap<Integer,HashSet<Vertex>> degreeMap = new HashMap<>();
        TreeSet<Integer> degrees = new TreeSet<>();
        Vertex minDegreeVertex = null;
        int minDegree = adjMap.keySet().size();
        // Loop all vertices in graph
        for (Vertex v : adjMap.keySet()){
            // Save max-degree-vertex (for future use ...)
            int degree = adjMap.get(v).size();
            if (degree < minDegree){
                minDegreeVertex = v;
                minDegree = degree;
            }
            degrees.add(degree);

            // Add vertex to degreeMap datastructure
            HashSet<Vertex> bucket = degreeMap.computeIfAbsent(degree, k -> new HashSet<>());
            bucket.add(v);
        }

        // Apply (min-to-min) greedy heuristic
        while (adjMap.size() > 0){
            // Choose min-degree vertex tmp
            Vertex tmp = minDegreeVertex;
            // Find next min-degree vertex from neighbors from tmp
            Vertex v = null;
            int minDeg = adjMap.size()+1;
            for (Vertex n : adjMap.get(tmp)){
                int d = adjMap.get(n).size();
                if (minDeg > d){
                    minDeg = d;
                    v = n;
                }
            }

            // Add v to the VC-solution
            solution.add(v);

            // remove v from graph (adjacency datastructure)
            HashSet<Vertex> neighbors = adjMap.remove(v);
            // remove v from (degree datastructure)
            int degree = neighbors.size();
            HashSet<Vertex> bucket = degreeMap.get(degree);
            bucket.remove(v);
            if (bucket.size() == 0) {
                degreeMap.remove(degree);
                degrees.remove(degree);
            }

            // remove v from it's neighbors
            for (Vertex n : neighbors){
                // remove v from neighbor n (adjacency datasturcture)
                HashSet<Vertex> nextNeighbors = adjMap.get(n);
                // decrease degree of neighbor n (delete from bucket ... later: put one bucket below)
                HashSet<Vertex> oldBucket = degreeMap.get(nextNeighbors.size());
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
                    HashSet<Vertex> nextBucket = degreeMap.get(nextNeighbors.size());
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
                minDegreeVertex = null;
            }
        }
        // Print solution
        return solution;
    }
}

