package edu.utsa.cs.sefm.docDownloader.htmlObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rocky on 5/20/2015.
 */
public class ClassDocumentation {
    public String name;
    public String url;
    public Map<String, String> publicMethods; // methodSig -> description
    private String overview;

    public ClassDocumentation(String url) {
        this.overview = null;
        this.name = "no name";
        this.url = url;
        this.publicMethods = new HashMap<>();
    }

    public String getOverview() {
        return this.overview;
    }

    public void setOverview(String overview) {
        this.overview = overview.replaceFirst("Class Overview", "");
    }

    public void addMethod(String method, String description) {
        publicMethods.put(method, description);
    }

    public void printMethods() {
        for (Map.Entry<String, String> method : publicMethods.entrySet()) {
            System.out.println("Method: " + method.getKey() + "\nDescription: " + method.getValue());
        }
    }


}
