package edu.utsa.cs.sefm.docDownloader.htmlObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rocky on 5/20/2015.
 */
public class SearchResult {
    public String query;
    public Map<String, String> results; // title->url mapping

    public SearchResult(String query) {
        this.query = query;
        results = new HashMap<>();
    }

    public void addResult(String title, String url) {
        results.put(title, url);
    }

    public String toString() {
        String ret = "Query: " + query;
        for (Map.Entry<String, String> result : results.entrySet())
            ret += "\nTitle: " + result.getKey() + "\nURL: " + result.getValue() + "\n------------";
        return ret;
    }
}
