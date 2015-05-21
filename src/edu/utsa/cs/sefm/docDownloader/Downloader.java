package edu.utsa.cs.sefm.docDownloader;

import edu.utsa.cs.sefm.docDownloader.htmlObject.ClassDocumentation;
import edu.utsa.cs.sefm.docDownloader.htmlObject.SearchResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rocky on 5/20/2015.
 */
public class Downloader {
    private static String google = "http://www.google.com/";
    //    private static String search = "search?q=location";
    private static String site = "+site:http:%2F%2Fdeveloper.android.com%2Freference%2F";
    private static String total = "&num=100";
    private static String userAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";

    private Map<String, SearchResult> searchResults; // query -> results

    public Downloader(List<String> searchTerms) {
        this.searchResults = new HashMap<>();

        // populate searchResults with terms -> null
        for (String searchTerm : searchTerms) {
            searchResults.put(searchTerm, null);
        }
    }

    public void download() {
        for (Map.Entry<String, SearchResult> query : searchResults.entrySet())
            searchResults.put(query.getKey(), search(query.getKey()));
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

        return null;
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
            Elements links = Jsoup.connect(google + queryString(query) + site + total).userAgent(userAgent).referrer(google).get().select("li.g>h3>a");

            for (Element link : links) {
                String title = link.text();
                String url = link.absUrl("href");
                url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

                if (!url.startsWith("http")) {
                    continue;
                }
                results.addResult(title, url);
            }
        } catch (IOException e) {
            System.err.println("Error retrieving search results for '" + query + "'");
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
