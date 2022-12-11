import java.util.ArrayList;
import java.util.List;

public class BooleanCombinations {

    public static void main(String[] args) {
        // Read the size of the ArrayList from the input
        int size = 8;

        // Create an empty ArrayList of boolean values
        List<Boolean> values = new ArrayList<>(size);

        // Fill the list with default values
        for (int i = 0; i < size; i++) {
            values.add(false);
        }

        // Compute all possible combinations of boolean values
        List<List<Boolean>> combinations = computeCombinations(values);

        // Print the result
        System.out.println(combinations);
    }

    // This function reads the size of the ArrayList from the input
    private static int readSizeFromInput() {
        // TODO: Implement this function
        return 0;
    }

    // This function computes all possible combinations of boolean values
    private static List<List<Boolean>> computeCombinations(List<Boolean> values) {
        // Create an empty list of combinations
        List<List<Boolean>> combinations = new ArrayList<>();

        // Base case: If the list of values is empty, return the empty list
        if (values.isEmpty()) {
            return combinations;
        }

        // Recursive case: If the list of values is not empty, compute the combinations
        // of the tail of the list (all elements except the first) and add the first element
        // to each combination, with both true and false values
        else {
            List<Boolean> tail = values.subList(1, values.size());
            List<List<Boolean>> tailCombinations = computeCombinations(tail);

            for (List<Boolean> combination : tailCombinations) {
                List<Boolean> trueCombination = new ArrayList<>(combination);
                trueCombination.add(0, true);
                combinations.add(trueCombination);

                List<Boolean> falseCombination = new ArrayList<>(combination);
                falseCombination.add(0, false);
                combinations.add(falseCombination);
            }

            return combinations;
        }
    }
}
