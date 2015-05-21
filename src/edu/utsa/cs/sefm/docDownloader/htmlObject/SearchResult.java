package edu.utsa.cs.sefm.docDownloader.htmlObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rocky on 5/20/2015.
 */
public class SearchResult {
    public String query;
    public Map<String, String> results; // title->url mapping
    public List<ClassDocumentation> pages;

    public SearchResult(String query) {
        this.query = query;
        this.pages = new ArrayList<>();
        results = new HashMap<>();
    }

    public void addResult(String title, String url) {
        results.put(title, url);
    }

    public String toString() {
        String ret = "Query: " + query;
        ret += "\nTotal class docs: " + pages.size();
        for (Map.Entry<String, String> result : results.entrySet())
            ret += "\nTitle: " + result.getKey() + "\nURL: " + result.getValue() + "\n------------";
        return ret;
    }

    public void addPage(ClassDocumentation page) {
        pages.add(page);
    }
}
