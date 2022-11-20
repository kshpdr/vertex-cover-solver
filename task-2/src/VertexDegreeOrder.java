import java.util.*;

public class VertexDegreeOrder {
    private final HashMap<Integer, HashSet<Vertex>> degreeMap = new HashMap<>();
    private int maxDegree = 0;
    SortedSet<Integer> sortedAvailableDegrees = new TreeSet<>(Collections.reverseOrder());

    public  VertexDegreeOrder() {}

    public void addVertex(Vertex v){
        if (!degreeMap.containsKey(v.degree)){
            HashSet<Vertex> tmpSet = new HashSet<>();
            tmpSet.add(v);
            degreeMap.put(v.degree,tmpSet);
            sortedAvailableDegrees.add(v.degree);
        } else {
            degreeMap.get(v.degree).add(v);
        }
        if (maxDegree<v.degree) maxDegree = v.degree;
    }

    public void putBack(Vertex v,int delta){
        if (!degreeMap.containsKey(v.degree)){
            HashSet<Vertex> tmpSet = new HashSet<>();
            tmpSet.add(v);
            degreeMap.put(v.degree,tmpSet);
            sortedAvailableDegrees.add(v.degree);
        } else {
            degreeMap.get(v.degree).add(v);
        }
        if (maxDegree<v.degree) maxDegree = v.degree;
        if(degreeMap.containsKey(v.degree-delta)){
            degreeMap.get(v.degree-delta).remove(v);
        }
    }

    @Override
    public String toString() {
        return degreeMap + " Max Degree: "+ maxDegree;
    }

    public void increaseDegreeOfVertex(Vertex v, int delta){
        if(degreeMap.containsKey(v.degree-delta)) degreeMap.get(v.degree-delta).remove(v);
        if (!degreeMap.containsKey(v.degree)){
            HashSet<Vertex> tmpSet = new HashSet<>();
            tmpSet.add(v);
            degreeMap.put(v.degree,tmpSet);
            sortedAvailableDegrees.add(v.degree);
        } else {
            degreeMap.get(v.degree).add(v);
        }
        if (maxDegree<v.degree) maxDegree = v.degree;
    }

    public void decreaseDegreeOfVertex(Vertex v,int delta) {
        if (!degreeMap.containsKey(v.degree)) {
            HashSet<Vertex> tmpSet = new HashSet<>();
            tmpSet.add(v);
            degreeMap.put(v.degree, tmpSet);
            sortedAvailableDegrees.add(v.degree);
        } else {
            degreeMap.get(v.degree).add(v);
        }

        if (degreeMap.containsKey(v.degree + delta)){
            degreeMap.get(v.degree + delta).remove(v);
            if ((v.degree + delta==this.maxDegree) && degreeMap.get(v.degree + delta).isEmpty()){
                int degree = v.degree+delta;
                while(degree> 0 && this.degreeMap.containsKey(degree) && this.degreeMap.get(degree--).isEmpty()){
                    this.maxDegree--;

                }
            }
        }
    }

    public void removeVertex(Vertex v){
        this.degreeMap.get(v.degree).remove(v);
        if(v.degree == this.maxDegree && this.degreeMap.get(v.degree).isEmpty()) {
            for(Object degree: this.sortedAvailableDegrees){
                if(this.degreeMap.containsKey((int) degree) && !this.degreeMap.get((int) degree).isEmpty()){
                    this.maxDegree= (int) degree;
                    break;
                }
            }
        }
    }

    public Vertex getVertexWithMaxDegree(){
        return this.degreeMap.get(this.maxDegree).iterator().next();
    }

    public boolean isEmpty(){
        return this.degreeMap.isEmpty() || this.degreeMap.get(this.maxDegree).isEmpty();
    }
}
