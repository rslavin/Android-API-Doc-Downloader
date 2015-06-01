package edu.utsa.cs.sefm.docDownloader;

import edu.utsa.cs.sefm.docDownloader.htmlObject.ClassDocumentation;
import edu.utsa.cs.sefm.docDownloader.htmlObject.SearchResult;
import edu.utsa.cs.sefm.docDownloader.utils.CSVWriter;
import edu.utsa.cs.sefm.docDownloader.utils.MySQLConnection;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        List<String> phraseList = new ArrayList<>();
        phraseList.add("location");


        MySQLConnection sql = new MySQLConnection(mysqlHost, mysqlUser, mysqlPass);
        Downloader downloader = null;
//        try {
        downloader = new Downloader(phraseList);//getPhrases(phraseFile));
//        } catch (IOException e) {
//            System.err.println("Unable to open phrase file");
//            e.printStackTrace();
//        }
        downloader.download();
        updateDB(downloader, sql);

        /*try {
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

    private static void updateDB(Downloader dl, MySQLConnection sql) {
        try {
            for (Map.Entry<String, SearchResult> search : dl.searchResults.entrySet()) {
                String searchTerm = search.getKey();
                int termID = sql.getID("search_terms", "term", sql.escapeSQL(searchTerm));
                if (termID < 0)
                    termID = sql.insert("INSERT INTO search_terms (term) VALUES ('" + sql.escapeSQL(searchTerm) + "')");
                for (ClassDocumentation classDoc : search.getValue().pages) {
                    // check if the class doc has already been inserted
                    ResultSet existingDoc = sql.select("SELECT id FROM class_docs WHERE url = '" + sql.escapeSQL(classDoc.url) + "'");
                    int classID = sql.getID("class_docs", "url", sql.escapeSQL(classDoc.url));
                    if (classID < 0) {
                        // if the class doc is new, insert it
                        classID = sql.insert("INSERT INTO class_docs (name, url, description) VALUES ('" + sql.escapeSQL(classDoc.name) + "', '" + sql.escapeSQL(classDoc.url) + "', '" + sql.escapeSQL(classDoc.getOverview()) + "')");
                    }
                    existingDoc.close();
                    // create relationship
                    sql.insert("INSERT INTO search_class_relation (term_id, class_doc_id) VALUES ('" + termID + "', '" + classID + "')");
                    // insert the class' methods
                    for (Map.Entry<String, String> method : classDoc.publicMethods.entrySet()) {
                        System.out.println("INSERT INTO method_docs (method, description, class_id) VALUES ('" + sql.escapeSQL(method.getKey()) + "', '" + sql.escapeSQL(method.getValue()) + "', '" + classID + "')");
                        sql.insert("INSERT INTO method_docs (method, description, class_id) VALUES ('" + sql.escapeSQL(method.getKey()) + "', '" + sql.escapeSQL(method.getValue()) + "', '" + classID + "')");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
