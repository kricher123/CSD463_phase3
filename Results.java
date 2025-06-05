
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Results {

    public static void print_results() throws FileNotFoundException, IOException {
        Map<String, Integer> qrels = new HashMap<>();

        try ( BufferedReader reader = new BufferedReader(new FileReader("qrels.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                String key = parts[0] + "-" + parts[2];
                int relevance = Integer.parseInt(parts[3]);
                qrels.put(key, relevance);
            }
        }

        int total = 0;
        int relevant = 0;
        int irrelevant = 0;
        int unknown = 0;

        try ( BufferedReader reader = new BufferedReader(new FileReader("results.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                String key = parts[0] + "-" + parts[2];
                total++;

                if (qrels.containsKey(key)) {
                    int rel = qrels.get(key);
                    if (rel > 0) {
                        relevant++;
                    } else {
                        irrelevant++;
                    }
                } else {
                    unknown++;
                }
            }
        }

        System.out.println("Total results: " + total);
        System.out.println("Relevant found: " + relevant);
        System.out.println("Irrelevant found: " + irrelevant);
        System.out.println("Unknown (not in qrels): " + unknown);


    }

}
