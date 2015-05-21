package edu.utsa.cs.sefm.docDownloader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky on 5/21/2015.
 */
public class CSVWriter {
    private static final String DELIMITER = ",";
    private static final String NEWLINE = "\n";
    public String name;
    private FileWriter fw;
    private List<String> rows;

    public CSVWriter(String name) throws IOException {
        this.name = name;
        this.rows = new ArrayList<>();
        this.fw = new FileWriter(name + ".csv");
    }

    public void writeFile() throws IOException {
        for (String row : rows)
            fw.append(row);
        fw.flush();
        fw.close();
    }

    public void addRow(ArrayList<String> row) {
        String newRow = "";
        for (String cell : row) {
            cell = "\"" + cell + "\"";
            if (newRow.length() < 1)
                newRow = cell;
            else
                newRow += DELIMITER + cell;
        }
        rows.add(newRow + NEWLINE);
    }

    public void addRow(String[] row) {
        String newRow = "";
        for (String cell : row) {
            cell = "\"" + cell + "\"";
            if (newRow.length() < 1)
                newRow = cell;
            else
                newRow += DELIMITER + cell;
        }
        rows.add(newRow + NEWLINE);
    }

}
