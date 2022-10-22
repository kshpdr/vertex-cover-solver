/*
 * THIS VERSION IS NOT OPTIMISED. IT WAS PRORAMMED AS "BADLY" AS POSSIBLE ...
 * NOW WE HAVE TO THINK ABOUT SOME OPTIMIZATION STRATEGIES !!!
 */

import java.util.*;

public class VertexCover {
    public static ArrayList<String> vertex_cover(ArrayList<String> nodes,ArrayList<String[]> edges){
        // Test for vertex-cover of size k=[0 ... |V|]
        for (int k=0;k<nodes.size();k++){
            ArrayList<String> S = vc_branch(nodes,edges,k);
            if (S != null) return S;
        }
        return nodes;
    }
    public static ArrayList<String> vc_branch(ArrayList<String> nodes,ArrayList<String[]> edges,int k){
        if (k < 0) return null;
        else if (edges.size() == 0) return new ArrayList<>();
        for (String node : edges.get(0)){
            // Copy nodes list (but remove current 'node')
            ArrayList<String> nodes2 = new ArrayList<>();
            for (String n : nodes) {
                if (!n.equals(node)) nodes2.add(n);
            }
            // Copy edges list (but remove edges containing current 'node')
            ArrayList<String[]> edges2 = new ArrayList<>();
            for (String[] edge : edges){
                if (!(edge[0].equals(node) || edge[1].equals(node))) edges2.add(edge);
            }
            // Test reduced graph for vertex-cover (size k-1)
            ArrayList<String> S = vc_branch(nodes2, edges2, k-1);
            if (S != null) {
                S.add(node);
                return S;
            }
        }
        return null;
    }
    public static void main(String[] args){
        // STEP 1: Parse Input
        Scanner scanner = new Scanner(System.in);
        int nodes_num = Integer.parseInt(scanner.nextLine().split("\\s")[1]);
        int edges_num = Integer.parseInt(scanner.nextLine().split("\\s")[1]);
        ArrayList<String[]> edges = new ArrayList<>();                  // edges => ArrayList of 2-dimensional String-Array
        ArrayList<String> nodes = new ArrayList<>();                    // nodes => 1-dimensional String-Array
        for (int i=0;i<edges_num;i++){
            String[] edge = scanner.nextLine().split("\\s");
            edges.add(edge);
            if (!nodes.contains(edge[0])) nodes.add(edge[0]);
            if (!nodes.contains(edge[1])) nodes.add(edge[1]);
        }
        scanner.close();
        // STEP 2: Run vertex-cover algorithm
        ArrayList<String> result = vertex_cover(nodes,edges);
        // Step 3: Print result
        for (String node : result){
            System.out.println(node);
        }
    }
}