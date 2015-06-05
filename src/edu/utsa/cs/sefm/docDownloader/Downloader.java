package edu.utsa.cs.sefm.docDownloader;

import edu.utsa.cs.sefm.docDownloader.htmlObject.ClassDocumentation;
import edu.utsa.cs.sefm.docDownloader.htmlObject.SearchResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Rocky on 5/20/2015.
 */
public class Downloader {
    private static String google = "http://www.google.com/";
    private static String site = "+site:http:%2F%2Fdeveloper.android.com%2Freference%2F";
    private static String userAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
    public int resultsPerQuery = 10;
    public Map<String, SearchResult> searchResults; // query -> results
    public List<String> errors;


    public Downloader(List<String> searchTerms) {
        this.searchResults = new HashMap<>();
        this.errors = new ArrayList<>();

        // populate searchResults with terms -> null
        for (String searchTerm : searchTerms) {
            searchResults.put(searchTerm, null);
        }

    }

    public void download() {
        // get results for each query
        for (Map.Entry<String, SearchResult> query : searchResults.entrySet()) {
            searchResults.put(query.getKey(), search(query.getKey()));
            // download each result and add them to the SearchResult object
            for (Map.Entry<String, String> result : query.getValue().results.entrySet()) {
                query.getValue().addPage(getDoc(result.getValue()));
                try {
                    //sleep 2 seconds
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.err.println("error sleeping");
                    e.printStackTrace();
                }
            }
            try {
                //sleep 15 seconds
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                System.err.println("error sleeping");
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints all SearchResults
     */
    public void printSearchResults() {
        for (Map.Entry<String, SearchResult> result : searchResults.entrySet()) {
            System.out.println("\n##### New Search #####");
            System.out.println(result.getValue());
        }
    }

    /**
     * Creates a ClassDocumentation object based on the doc's url.
     *
     * @param url URL pointing to class documentation.
     * @return
     */
    private ClassDocumentation getDoc(String url) {
        ClassDocumentation doc = new ClassDocumentation(url);
        try {
            // get page
            Document page = Jsoup.connect(url).get();

            // get Class Inheritance
            Element className = page.select("[class=\"jd-inheritance-class-cell\"").last();
            doc.name = Jsoup.clean(className.toString(), Whitelist.none());

            // get Class Overview
            Element classOverview = page.select("[class=\"jd-descr\"]").first();
            doc.setOverview(Jsoup.clean(classOverview.toString(), Whitelist.none()));

            // get Public Methods
            Elements methodsTable = page.select("[id=\"pubmethods\"] [class=\"jd-linkcol\"]");

            Pattern methodNamePattern = Pattern.compile("\"*>([^>]+)<\\/a");
            Pattern methodParametersPattern = Pattern.compile("span>(\\([^<>]+\\))");
            Pattern methodDescriptionPattern = Pattern.compile("jd-descrdiv\">\\s*\\n(.+)<\\/div", Pattern.DOTALL);
            for (Element rawMethod : methodsTable) {
                // get method name
                Matcher m = methodNamePattern.matcher(rawMethod.toString());
                String methodName = "no name";
                while (m.find())
                    methodName = m.group(1);

                // get method params
                m = methodParametersPattern.matcher(rawMethod.toString());
                String methodParam = "()";
                while (m.find())
                    methodParam = m.group(1);

                // get method description
                m = methodDescriptionPattern.matcher(rawMethod.toString());
                String methodDescription = "no description";
                while (m.find())
                    methodDescription = m.group(1);

                doc.addMethod(methodName + methodParam, Jsoup.clean(methodDescription, Whitelist.none()));
            }

            // get Fields
            Elements fieldsTable = page.select("[id=\"lfields\"] [class*=api]");

            Pattern fieldNamePattern = Pattern.compile("\"*>([^>]+)<\\/a");
            Pattern fieldDescriptionPattern = Pattern.compile("%\">\\s*(.+)<\\/td", Pattern.DOTALL);
            for (Element rawField : fieldsTable) {
                // get method name
                Matcher m = fieldNamePattern.matcher(rawField.toString());
                String fieldName = "no name";
                while (m.find())
                    fieldName = m.group(1);

                // get method description
                m = fieldDescriptionPattern.matcher(rawField.toString());
                String fieldDescription = "no description";
                while (m.find())
                    fieldDescription = m.group(1);

                doc.addField(fieldName, Jsoup.clean(fieldDescription, Whitelist.none()));
            }

        } catch (IOException e) {
            System.err.println("Error retrieving documentation for '" + url + "'");
            errors.add("Error retrieving documentation for '" + url + "'");
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Error retrieving class from " + url);
            errors.add("Error retrieving class for '" + url + "'");
            e.printStackTrace();
        }

        return doc;
    }

    /**
     * Creates a search result based on a set of search terms.
     *
     * @param query Search terms.
     * @return Object containing search results.
     */
    private SearchResult search(String query) {
        SearchResult results = new SearchResult(query);
        try {
            System.out.println("Retrieving: " + google + queryString(query) + site + "&num=" + resultsPerQuery);
            Elements links = Jsoup.connect(google + queryString(query) + site + "&num=" + resultsPerQuery).userAgent(userAgent).referrer(google).get().select("li.g>h3>a");

            for (Element link : links) {
                String title = link.text();
                String url = link.absUrl("href");
                url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

                // Skip packages and non http links
                if (!url.startsWith("http") || url.contains("package-summary") || url.contains("packages.html") || url.contains("classes.html") || url.contains("gms-packages")) {
                    continue;
                }
                results.addResult(title, url);
            }
        } catch (IOException e) {
            System.err.println("Error retrieving search results for '" + query + "'");
            errors.add("Error retrieving search results for '" + query + "'");
            e.printStackTrace();
        }
        return results;
    }

    /**
     * Encodes a search term into a valid query variable.
     *
     * @param term Word(s) to encode.
     * @return Valid query string for use with Google search.
     */
    private String queryString(String term) {
        return "search?q=" + term.replaceAll("\\s+", "+");
    }

}
