package edu.utsa.cs.sefm.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky on 5/13/2015.
 */
public class Calc {

    /**
     * Calculates the term frequency of a term, given a list of all terms in a set.
     * @param allTerms All terms in a given set.
     * @param term Term among the set to count tf for.
     * @return TF
     */
    public static double tf(List<String> allTerms, String term){
        double count = 0;
        for(String t : allTerms){
            if(t.equals(term))
                count++;
        }
        return count / allTerms.size();
    }

    /**
     * Calculates the inverse document frequency of a term, given all terms in all sets.
     * @param docsTerms List of all Lists of terms.
     * @param term Term to calculate idf for.
     * @return IDF
     */
    public static double idf(ArrayList<ArrayList<String>> docsTerms, String term) {
        double count = 0;
        for(List<String> doc : docsTerms){
            for(String t : doc){
                if(t.equals(term)){
                    count++;
                    break;
                }
            }
        }

        return 1 + Math.log(docsTerms.size() / count);
    }
}
