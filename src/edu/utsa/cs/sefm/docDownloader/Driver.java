package edu.utsa.cs.sefm.docDownloader;

import edu.utsa.cs.sefm.docDownloader.htmlObject.ClassDocumentation;
import edu.utsa.cs.sefm.docDownloader.htmlObject.SearchResult;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Rocky on 5/20/2015.
 */
public class Driver {

    public static void main(String[] args) {
        List<String> searchTerms = new ArrayList<>();
        String filename = "output";
        searchTerms.add("location");

        Downloader downloader = new Downloader(searchTerms);

        downloader.download();

        try {
            createCSV(downloader, filename);
            createReport(downloader, filename);
        } catch (IOException e) {
            System.err.println("Unable to write csv file");
            e.printStackTrace();
        }
    }

    private static void createCSV(Downloader dl, String filename) throws IOException {
        CSVWriter csv = new CSVWriter(filename);

        // header row
        csv.addRow(new String[]{"Phrase", "Class", "Class Description", "Method", "Method Description"});

        // for each search result
        for (Map.Entry<String, SearchResult> search : dl.searchResults.entrySet()) {
            String searchTerm = search.getKey();
            // for each class in the search result
            for (ClassDocumentation classDoc : search.getValue().pages) {
                String className = classDoc.name;
                String classDesc = classDoc.getOverview();
                // for each method in the class
                for (Map.Entry<String, String> method : classDoc.publicMethods.entrySet())
                    csv.addRow(new String[]{searchTerm, className, classDesc, method.getKey(), method.getValue()});
            }
        }
        csv.writeFile();
    }

    private static void createReport(Downloader dl, String filename) throws IOException {
        FileWriter fw = new FileWriter(filename + ".txt");

        // for each search result
        for (Map.Entry<String, SearchResult> search : dl.searchResults.entrySet()) {
            fw.append("Phrase: " + search.getKey());
            // for each class in the search result
            for (ClassDocumentation classDoc : search.getValue().pages) {
                fw.append("\n  Class: " + classDoc.name);
                fw.append("\n  Class Description: " + classDoc.getOverview());
                // for each method in the class
                for (Map.Entry<String, String> method : classDoc.publicMethods.entrySet()) {
                    fw.append("\n    Method: " + method.getKey());
                    fw.append("\n    Method Description: " + method.getValue());
                    fw.append("\n    ------------------");
                }
            }
            fw.append("\n\n");
        }
        fw.flush();
        fw.close();
    }

}
