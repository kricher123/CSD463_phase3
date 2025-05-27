import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import gr.uoc.csd.hy463.Topic;
import gr.uoc.csd.hy463.TopicType;
import gr.uoc.csd.hy463.TopicsReader;

public class searchEngine {


    public static void topicRetrieval(){
        ArrayList<Topic> topics = null;
        try {
            topics = TopicsReader.readTopics("src\\CollectionIndex\\topics.xml");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        // for (Topic topic : topics) { 
        //     System.out.println(topic.getNumber()); 
        //     System.out.println(topic.getType()); 
        //     System.out.println(topic.getSummary()); 
        //     System.out.println(topic.getDescription()); 
        //     System.out.println("---------"); 
        // }
        Map<String, Double> Documents = null;
        for(Topic topic: topics){
            Documents = questionRetrieval(topic.getDescription(),topic.getType());
            double max_score = 0;
            String max_path = "";
            for(Map.Entry<String , Double> entry : Documents.entrySet()){
                Double score = entry.getValue();
                String path = entry.getKey();
                if(max_score<score){
                    max_score = score;
                    max_path = path;
                }
            }
            System.out.println("Answer for Topic " + topic.getNumber() + " is: " + max_path + " with score of:" + max_score);
        }
        // for(Map.Entry<String , Double> entry : Documents.entrySet()){
        //     System.out.println(entry.getKey() + " " + entry.getValue());
        // }
    }

    public static ArrayList<String> readStopWords() {
        ArrayList<String> ret = new ArrayList<>();
        FileReader FileInEn;
        try {
            FileInEn = new FileReader("src\\Stopwords\\stopwordsEn.txt");
            FileReader FileInGr = new FileReader("src\\Stopwords\\stopwordsGr.txt");
            BufferedReader reader = new BufferedReader(FileInEn);
            String tmp = reader.readLine();
            while (tmp != null) {
                ret.add(tmp);
                tmp = reader.readLine();
            }
            reader = new BufferedReader(FileInGr);
            tmp = reader.readLine();
            while (tmp != null) {
                ret.add(tmp);
                tmp = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    static String editQuery(String query) {
        ArrayList<String> stopwords = readStopWords();
        query = query.toLowerCase().replaceAll("\\p{Punct}", " ");
        String[] tokens = query.split("\s+");
        StringBuilder cleaned = new StringBuilder();

        for (String token : tokens) {
            if (!stopwords.contains(token) && !token.isBlank()) {
                String stemmed = Utilities.stem(token);
                cleaned.append(stemmed).append(" ");
            }
        }

        return cleaned.toString().trim();
    }

    public static Map<String,Double> questionRetrieval(String line, TopicType category){
        String categoryString = category.toString();
        ArrayList<Word> words = Utilities.read_words();
        String query = new String(line);

        String clean_query = editQuery(query);
        //System.out.println(clean_query);

        String[] query_terms = clean_query.split("\s+");
        Map<Integer, Double> documentScores = new HashMap<>();
        Map<String, Double> Ret = new HashMap<>();

        for (String term : query_terms) {
            ArrayList<Word> postings = Utilities.get_word(term, words);
            for (Word w : postings) {
                documentScores.put(w.PMID, documentScores.getOrDefault(w.PMID, 0.0) + w.weight);
            }
        }

        //System.out.println("Vector Space Model Results:");
        documentScores.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .forEach(entry -> {
                    int pmid = entry.getKey();
                    double score = entry.getValue();
                    String path = words.stream().filter(w -> w.PMID == pmid).findFirst().map(w -> w.path).orElse("Unknown");
                    //System.out.println("Document: " + path + " | Score: " + score);
                    Ret.put(path , score);
                });
        return Ret;
    }
}
