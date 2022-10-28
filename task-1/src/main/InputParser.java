package main;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner; // Import the Scanner class to read text files

public class InputParser {
    public BufferedReader reader;

    public InputParser() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public List<String> parseEdges() throws IOException {
        List<String> stringEdges = new ArrayList<>();
        String line;

        while ((!(line = this.reader.readLine()).isEmpty())){
            if (!line.contains("#")){
                stringEdges.add(line);
            }
        }
        return stringEdges;
    }
}
