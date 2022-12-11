import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Permutator {
    static Set<String> permutations = new HashSet<>();

    static void permutate(String str, String ans) {
        // If string is empty
        if (str.length() == 0) {
            permutations.add(ans);
            return;
        }

        for (int i = 0; i < str.length(); i++) {
            // ith character of str
            char ch = str.charAt(i);

            // Rest of the string after excluding
            // the ith character
            String ros = str.substring(0, i)
                    + str.substring(i + 1);

            // Recurvise call
            permutate(ros, ans + ch);
        }
    }

    // Driver code
    public static void main(String[] args) {
        String s0 = "tttttttttt";
        String s1 = "tttttttttf";
        String s2 = "ttttttttff";
        String s3 = "tttttttfff";
        String s4 = "ttttttffff";
        String s5 = "tttttfffff";
        String s6 = "ttttffffff";
        String s7 = "tttfffffff";
        String s8 = "ttffffffff";
        String s9 = "tfffffffff";
        String s10 = "ffffffffff";
        String[] strings = new String[]{s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10};

        for (String s : strings){
            permutate(s, "");
        }

        permutations.forEach(System.out::println);
        System.out.println(permutations.size());
    }
}
