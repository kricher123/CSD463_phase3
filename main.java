
public class main {

    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        searchEngine.topicRetrieval();
        searchEngine.analyzeRelevanceByTopicType();


        Results.print_results();
        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        double seconds = duration / 1_000_000_000.0;
        System.out.printf("Time taken: %.4f seconds%n", seconds);

    }
}
