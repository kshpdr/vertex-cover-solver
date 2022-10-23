import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class Solver {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String firstLine = scanner.nextLine().substring(1);
        int numberEdges = Integer.parseInt(firstLine.split("#")[0].split(" ")[0]);
        int numberVertex = Integer.parseInt(firstLine.split("#")[0].split(" ")[1]);
        HashMap<String,LinkedList<String>> graph = new HashMap<>();
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            String node1 = line.split(" ")[0];
            String node2 = line.split(" ")[1];
            if (!graph.containsKey(node1)){
                graph.put(node1,new LinkedList<>());
            }
            graph.get(node1).add(node2);

        }

        for (String name: graph.keySet()) {
            String value = graph.get(name).toString();
            System.out.println(name + " " + value);
        }
        //PrintWriter out = new PrintWriter(System.out);
        //out.print("2");
        //out.close()
    }




}
