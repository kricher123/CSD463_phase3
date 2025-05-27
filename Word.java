import java.util.ArrayList;

public class Word {
    String name;
    int PMID;
    double weight;
    ArrayList<Integer> positions;
    String path;

    public Word(String n, int id, double w, ArrayList<Integer> pos, String p){
        name = new String(n);
        path = new String(p);
        PMID = id;
        weight = w;
        positions = new ArrayList<>(pos);
    }

    public Word(String n, double w, int id){
        name = new String(n);
        weight = w;
        PMID = id;
    }

    public Word(String n, int id){
        name = new String(n);
        PMID = id;
    }

    public Word(Word w){
        this.name = new String(w.name);
        this.path = new String(w.path);
        this.PMID = w.PMID;
        this.weight = w.weight;
        this.positions = new ArrayList<>(w.positions);
    }
}
