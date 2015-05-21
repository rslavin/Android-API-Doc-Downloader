package edu.utsa.cs.sefm.docDownloader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rocky on 5/20/2015.
 */
public class Driver {

    public static void main(String[] args) {
        List<String> searchTerms = new ArrayList<>();
        searchTerms.add("location");

        Downloader downloader = new Downloader(searchTerms);

        downloader.download();
        downloader.printSearchResults();
    }

}
