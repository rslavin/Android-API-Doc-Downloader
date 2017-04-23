package edu.utsa.cs.sefm.docDownloader.htmlObject;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.utsa.cs.sefm.utils.Calc;

import java.io.StringReader;
import java.util.*;

/**
 * Created by Rocky on 5/20/2015.
 */
public class SearchResult {
    public String query;
    public Map<String, String> results; // title->url mapping
    public List<ClassDocumentation> pages;

    public SearchResult(String query) {
        this.query = query;
        this.pages = new ArrayList<>();
        results = new HashMap<>();
    }

    public void addResult(String title, String url) {
        results.put(title, url);
    }

    public String toString() {
        String ret = "Query: " + query;
        ret += "\nTotal class docs: " + pages.size();
        for (Map.Entry<String, String> result : results.entrySet())
            ret += "\nTitle: " + result.getKey() + "\nURL: " + result.getValue() + "\n------------";
        return ret;
    }

    public void addPage(ClassDocumentation page) {
        pages.add(page);
    }

	public void pruneByTFIDF(int numResultsToKeep) {
		TreeMap<Double, ClassDocumentation> tfIdfMap = new TreeMap<>();
		ArrayList<ArrayList<String>> tokenListByPage = new ArrayList<>();
		
		for(ClassDocumentation doc : pages) {
			PTBTokenizer<CoreLabel> tok = new PTBTokenizer<CoreLabel>(
					new StringReader(doc.getOriginalHTML().text()), 
					new CoreLabelTokenFactory(), "");
			ArrayList<String> tokensInPage = new ArrayList<>();
			while(tok.hasNext())
				tokensInPage.add(tok.next().originalText());
			tokenListByPage.add(tokensInPage);
		}
		for(int i = 0; i < pages.size(); i++) {
			double tfIdfScore = Calc.tf(tokenListByPage.get(i), query);
			tfIdfScore *= Calc.idf(tokenListByPage, query);
			tfIdfMap.put(tfIdfScore, pages.get(i));
		}
		
		pages = new ArrayList<>();
		int i = 0;
		for(ClassDocumentation d : tfIdfMap.values()) {
			pages.add(d);
			i++;
			if (i >= numResultsToKeep)
				return;
		}
	}
}
