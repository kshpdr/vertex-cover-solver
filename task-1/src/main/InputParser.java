package main;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files

public class InputParser {
    public Scanner scanner;

    public InputParser(String fileName) throws FileNotFoundException {
        this.scanner = new Scanner(new File(fileName));
    }

    public List<String> parseEdges() {
        List<String> stringEdges = new ArrayList<>();
        String firstLine = scanner.nextLine();
        String secondLine = scanner.nextLine();
        while (scanner.hasNextLine()) {
            String data = scanner.nextLine();
            stringEdges.add(data);
        }
        return stringEdges;
    }
}
