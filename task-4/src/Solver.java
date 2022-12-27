import java.util.*;

public class Solver {
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		HashSet<String> solution = new HashSet<>();
		StringBuilder sb = new StringBuilder();
		while (sc.hasNext()){
			String line = sc.nextLine();
			if (line.startsWith("#") || line.length() == 0) continue;
			String[] edge = line.split(" ");
			String u = edge[0];
			String v = edge[1];
			if (!solution.contains(u) && !solution.contains(v)){
				solution.add(u);
				sb.append(u+"\n");
			}
		}
		sc.close();
		System.out.print(sb);
	}
}
