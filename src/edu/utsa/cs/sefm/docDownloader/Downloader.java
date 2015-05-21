package edu.utsa.cs.sefm.docDownloader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;

/**
 * Created by Rocky on 5/20/2015.
 */
public class Downloader {
    private static String google = "http://www.google.com/";
    private static String search = "search?q=location";
    private static String site = "+site:http:%2F%2Fdeveloper.android.com%2Freference%2F";
    private static String total = "&num=50";
    //    private String userAgent = "Android Phrase Analyzer 1.0 (+http://galadriel.cs.utsa.edu)";
    private static String userAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";

    public static void download() {


        try {
            Elements links = Jsoup.connect(google + search + site + total).userAgent(userAgent).referrer(google).get().select("li.g>h3>a");

            System.out.println(google + search);
            System.out.println(links);

            for (Element link : links) {
                String title = link.text();
                String url = link.absUrl("href");
                url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

                if (!url.startsWith("http")) {
                    continue;
                }

                System.out.println("Title: " + title);
                System.out.println("URL: " + url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
