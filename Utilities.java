import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import mitos.stemmer.Stemmer;

public class Utilities {

    public static ArrayList<Word> get_word(String n ,ArrayList<Word> words){
        ArrayList<Word> ret = new ArrayList<>();
        for(Word word:words){
            if(word.name.equals(n)){
                ret.add(new Word(word.name , word.weight , word.PMID));
            }
        }
        

        return ret;
    }

    public static String stem(String word) {
        Stemmer.Initialize();
        String tmp = "";
        if (word.matches(".*[α-ωΑ-Ω].*")) {
            int a = word.charAt(0);
            try {
                tmp = new String(word.getBytes("8859_7"), "8859_7");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            tmp = word;
        }
        tmp = Stemmer.Stem(tmp);
        return tmp;
    }

    public static ArrayList<Word> read_words(){
        BufferedReader reader;
        ArrayList<Word> words = new ArrayList<>();
        String curr = null;
        try{
            reader = new BufferedReader(new FileReader("src\\tempCollectionIndex\\InvertedLists\\InvertedIndex.txt"));
            String line = reader.readLine();
            while(line!=null){
                StringTokenizer tokens = new StringTokenizer(line);
                String name = tokens.nextToken();
                curr = name;
                int id = Integer.valueOf(tokens.nextToken());
                double weight = Double.valueOf(tokens.nextToken());
                String token;
                String path = null;
                ArrayList<Integer> positions = new ArrayList<>();
                while(tokens.hasMoreTokens()){
                    token = tokens.nextToken();
                    int position;
                    try{
                        position = Integer.parseInt(token);
                        positions.add(position);
                    }catch(NumberFormatException e){
                        path = token;
                    }
                }
                words.add(new Word(name , id , weight , positions , path));
                line = reader.readLine();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return words;
    }
}
