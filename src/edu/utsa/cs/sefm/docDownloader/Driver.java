package edu.utsa.cs.sefm.docDownloader;

import edu.utsa.cs.sefm.docDownloader.htmlObject.ClassDocumentation;
import edu.utsa.cs.sefm.docDownloader.htmlObject.SearchResult;
import edu.utsa.cs.sefm.docDownloader.utils.CSVWriter;
import edu.utsa.cs.sefm.docDownloader.utils.MySQLConnection;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Rocky on 5/20/2015.
 */
public class Driver {

    public static void main(String[] args) {
        String outFile = "output";
        String phraseFile = "phrases.txt";

        String mysqlHost = "jdbc:mysql://192.168.1.200:3306/sefm_android";
        String mysqlUser = "sefm_android";
        String mysqlPass = "sNfdNn7mFtHd76DD";

        // TODO map phrases to apis

        MySQLConnection sql = new MySQLConnection(mysqlHost, mysqlUser, mysqlPass);
        try {
            sql.insert("INSERT INTO test (testcolumn) VALUES ('hello world')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*Downloader downloader = null;
        try {
            downloader = new Downloader(getPhrases(phraseFile));
        } catch (IOException e) {
            System.err.println("Unable to open phrase file");
            e.printStackTrace();
        }
        downloader.download();

        try {
            createCSV(downloader, outFile);
            createReport(downloader, outFile);
        } catch (IOException e) {
            System.err.println("Unable to write csv file");
            e.printStackTrace();
        }*/
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
        fw.append("Retrieval errors:");
        for (String error : dl.errors)
            fw.append("\n  Error: " + error);
        fw.flush();
        fw.close();
    }

    /**
     * Reads phrases from input file into ArrayList line by line.
     *
     * @param filePath Location of phrase file.
     * @return ArrayList of phrases.
     * @throws IOException
     */
    private static ArrayList<String> getPhrases(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        ArrayList<String> phrases = new ArrayList<String>();
        while ((line = br.readLine()) != null)
            phrases.addAll(Arrays.asList(line.replace("\n", "").replace("\r", "").toLowerCase().split(",")));

        br.close();
        while (phrases.remove(" ")) ;
        for (int i = 0; i < phrases.size(); i++)
            phrases.set(i, phrases.get(i).trim());
        return phrases;
    }
}
