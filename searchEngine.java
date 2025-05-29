
import java.io.*;
import java.util.*;
import gr.uoc.csd.hy463.*;
import java.util.stream.Collectors;

public class searchEngine {

    public static void topicRetrieval() {
        ArrayList<Topic> topics = null;
        try {
            topics = TopicsReader.readTopics("src/CollectionIndex/topics.xml");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        ArrayList<String> outputLines = new ArrayList<>();
        String runName = "";

        for (Topic topic : topics) {
            Map<String, Double> documents = questionRetrieval(topic.getDescription(), topic.getType());

            List<Map.Entry<String, Double>> sortedDocs = new ArrayList<>(
                    documents.entrySet()
                            .stream()
                            .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                            .limit(1000)
                            .collect(Collectors.toList())
            );


            int rank = 1;
            for (Map.Entry<String, Double> entry : sortedDocs) {
                String pmcid = extractPMCID(entry.getKey());
                double score = entry.getValue();
                String line = topic.getNumber() + " 0 " + pmcid + " " + rank + " " + score + " " + runName;
                outputLines.add(line);
                rank++;
            }
        }

        writeResultsToFile(outputLines, "results.txt");
    }

    private static String extractPMCID(String path) {
        File file = new File(path);
        String name = file.getName();
        int dotIndex = name.lastIndexOf('.');
        return (dotIndex != -1) ? name.substring(0, dotIndex) : name;
    }

    private static void writeResultsToFile(List<String> lines, String filename) {
        try ( BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            System.out.println("Successfully saved " + filename);
        } catch (IOException e) {
            System.err.println("Error while creating " + filename);
            e.printStackTrace();
        }
    }

    public static ArrayList<String> readStopWords() {
        ArrayList<String> ret = new ArrayList<>();
        try {
            BufferedReader en = new BufferedReader(new FileReader("src/Stopwords/stopwordsEn.txt"));
            BufferedReader gr = new BufferedReader(new FileReader("src/Stopwords/stopwordsGr.txt"));
            String tmp;
            while ((tmp = en.readLine()) != null) {
                ret.add(tmp.trim());
            }
            while ((tmp = gr.readLine()) != null) {
                ret.add(tmp.trim());
            }
            en.close();
            gr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String editQuery(String query) {
        ArrayList<String> stopwords = readStopWords();
        query = query.toLowerCase().replaceAll("\\p{Punct}", " ");
        String[] tokens = query.split("\\s+");
        StringBuilder cleaned = new StringBuilder();
        for (String token : tokens) {
            if (!stopwords.contains(token) && !token.isBlank()) {
                String stemmed = Utilities.stem(token);
                cleaned.append(stemmed).append(" ");
            }
        }
        return cleaned.toString().trim();
    }

    public static Map<String, Double> questionRetrieval(String query, TopicType category) {
        ArrayList<Word> words = Utilities.read_words();
        String clean_query = editQuery(query);
        String[] query_terms = clean_query.split("\\s+");
        Map<Integer, Double> docScores = new HashMap<>();
        Map<String, Double> results = new HashMap<>();

        for (String term : query_terms) {
            ArrayList<Word> postings = Utilities.get_word(term, words);
            for (Word w : postings) {
                docScores.put(w.PMID, docScores.getOrDefault(w.PMID, 0.0) + w.weight);
            }
        }

        docScores.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(entry -> {
                    int pmid = entry.getKey();
                    double score = entry.getValue();
                    String path = words.stream()
                            .filter(w -> w.PMID == pmid)
                            .findFirst()
                            .map(w -> w.path)
                            .orElse("Unknown");
                    results.put(path, score);
                });

        return results;
    }
}
