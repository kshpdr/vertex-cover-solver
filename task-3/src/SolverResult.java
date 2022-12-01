import java.util.Collections;
import java.util.LinkedList;

public class SolverResult{
    public LinkedList<String> resultsList = null;
    public int recursiveSteps;

    public SolverResult() {}

    public void addVertexToResult(String vertexToAdd){
        this.resultsList.add(vertexToAdd);
    }

    public void addMultipleVertexToResult(String[] verticesToAdd){
        Collections.addAll(this.resultsList, verticesToAdd);
    }

    public void increaseRecursiveSteps(){
        this.recursiveSteps++;
    }

    public void setEmptyResultsList(){
        this.resultsList = new LinkedList<>();
    }
}