package common;

import java.util.ArrayList;

public class Tfidf {
	public static double tf(ArrayList<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / doc.size();
    }
	public static double idf(ArrayList<ArrayList<String>> docs, String term, WordFrequencyTable freqTable) {
        double n = 0;
        n = freqTable.getCount(term);					// Implementation of word frequency table
        return Math.log(((1 + docs.size()) /(1 + n)) + 1);
    }
	public static double tfIdf(ArrayList<String> doc, ArrayList<ArrayList<String>> docs, String term, WordFrequencyTable freqTable) {
        return tf(doc, term) * idf(docs, term, freqTable);
    }
}
