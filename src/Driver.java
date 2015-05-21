import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;

/**
 * Created by Rocky on 5/20/2015.
 */
public class Driver {

    public static void main(String[] args) {
        String google = "http://www.google.com/";
        String search = "search?q=location";
        search += "+site:http:%2F%2Fdeveloper.android.com%2Freference%2F";
        search += "&num=50";
        String userAgent = "Android Phrase Analyzer 1.0 (+http://galadriel.cs.utsa.edu)";
        userAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";

        try {
            Elements links = Jsoup.connect(google + search).userAgent(userAgent).referrer(google).get().select("li.g>h3>a");

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
