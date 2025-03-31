package common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CosineSimilarity {
	public static double cosineSimilarity(ArrayList<String> doc1, ArrayList<String> doc2, Map<String, Double> tfIdfMap) {
		// Create a set of all unique words from both documents
		Set<String> uniqueWords = new HashSet<>();
		uniqueWords.addAll(doc1);
		uniqueWords.addAll(doc2);

		// Calculate dot product and magnitude of vectors for cosine similarity
		double dotProduct = 0;
		double doc1Magnitude = 0;
		double doc2Magnitude = 0;
		// For each word within set of unique words
		for (String word : uniqueWords) {
			// Get TFIDF of word within doc1(business 1 categories) and doc2 (business 2 categories)
			double tfIdf1 = tfIdfMap.getOrDefault(word, 0.0) * countOccurrences(word, doc1);
			double tfIdf2 = tfIdfMap.getOrDefault(word, 0.0) * countOccurrences(word, doc2);
			// Calculate dot product
			dotProduct += tfIdf1 * tfIdf2;
			// Calculate magnitude of vectors
			doc1Magnitude += tfIdf1 * tfIdf1;
			doc2Magnitude += tfIdf2 * tfIdf2;
		}

		// Calculate cosine similarity
		double similarity = dotProduct / (Math.sqrt(doc1Magnitude) * Math.sqrt(doc2Magnitude));
		// Return similarity
		return similarity;
	}
	private static int countOccurrences(String term, ArrayList<String> doc1) {
		int count = 0;
		for (String word : doc1) {
			if (term.equalsIgnoreCase(word)) {
				count++;
				break;
			}
		}
		return count;
	}
}
