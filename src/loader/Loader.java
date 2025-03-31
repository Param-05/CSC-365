//*************************************************************************************************
//
//	Brandon LaPointe & Param Rajguru
//	CSC365 - Professor Doug Lea
//	Loader.java
//
package loader;

import common.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import com.google.gson.Gson;	// Uses GSON 2.8.2 jar to parse JSON

public class Loader {
	
	// Class Constants & Variables
	private static final int MAX_NUM = 10_000;					// Maximum number of businesses to add to object array
	private static final String[] FILLER_WORDS = {				// List of filler words to remove from reviews		
		"a", "about", "above", "across", "actually", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "amoungst", "amount", "an", "and", "another", "any", "anyhow", "anyone", "anything", "anyway", "anywhere", "are", "around", "as", "at",
		"b", "back", "basically", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom", "but", "by",
		"c", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry",
		"d", "de", "describe", "detail", "do", "done", "down", "due", "during",
		"e", "each", "eg", "eight", "either", "eleven", "else", "elsewhere", "empty", "enough", "esp", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except",
		"f", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further",
		"g", "get", "give", "go", "going", "gone",
		"h", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred",
		"i", "i'd", "ie", "if", "i'll", "i'm", "in", "inc", "indeed", "instead", "interest", "into", "is", "it", "its", "it's", "itself", "ive", "i've",
		"j", "just",
		"k", "keep",
		"l", "last", "latter", "latterly", "least", "left", "less", "like", "literally", "ltd",
		"m", "made", "make", "makes", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself",
		"n", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere",
		"o", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own",
		"p", "part", "per", "perhaps", "place", "please", "put",
		"q",
		"r", "rather", "re", "right",
		"s", "said", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system",
		"t", "take", "ten", "than", "that", "that's", "that'll", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "they're", "they've", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two",
		"u", "un", "under", "until", "up", "upon", "us",
		"v", "very", "via",
		"w", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would",
		"x",
		"y", "yet", "you", "your", "yours", "yourself", "yourselves",
		"z"};
	
	// JSON File Names
	private static String businessesAddress = "yelp_dataset_business.json";							// Address of truncated business JSON file used (Truncated Yelp Academic Dataset Business)
	private static String reviewsAddress = "yelp_dataset_review.json";								// Address of truncated review JSON file used (Yelp Academic Dataset Review)
	
	// TFIDF Variables
	private static Business[] businesses;															// Array of business objects
	private static ArrayList<Review> reviews = new ArrayList<Review>();								// Array List of review objects
	private static ArrayList<String> catAryList = new ArrayList<String>();							// Expandable array list containing all of the categories from all businesses
	private static ArrayList<ArrayList<String>> catAryAryList = new ArrayList<ArrayList<String>>();	// Array list containing string arrays of categories from all businesses
	private static ArrayList<String> revAryList = new ArrayList<String>();							// Expandable array list containing all of the reviews from all businesses
	private static ArrayList<ArrayList<String>> revAryAryList = new ArrayList<ArrayList<String>>();	// Array list containing array lists of strings of reviews from all businesses
	
	// BusinessData Folder Variables
	private static String folderName = "BusinessData";												// Name of folder where files for all businesses are stored
	private static File folder = new File(folderName);												// File folder created from String folderName
	
	// K-Means Clustering Variables
    public static HashMap<Integer, List<Point>> cluster_points = new HashMap<Integer, List<Point>>();
    private static int NUM_CLUSTERS = 10;
    private static int MAX_ITER = 1000;
    private static List<Point> points; 
    private static List<Point> clusters;
    
	// Businesses HashMaps
	private static HashMap <String, String> bName_bId = new HashMap <String, String>();								// HashMap of business name to business id
	private static HashMap <String, String> bId_bName = new HashMap <String, String>();								// HashMap of business id to business name
	private static HashMap <String, String[]> bId_category = new HashMap <String, String[]>();						// HashMap of business id to business categories
	private static HashMap <Integer, String> index_bId = new HashMap <Integer, String>();							// HashMap of index to business id
	private static HashMap <String, Integer> bId_index = new HashMap <String, Integer>();							// HashMap of business id to index
	private static HashMap <String, Double> category_tfidf = new HashMap <String, Double>();						// HashMap of categories to tfIdf
	private static HashMap <String, String> bId_address = new HashMap <String, String>();							// HashMap of business id to address
	
	// Reviews HashMaps
	private static HashMap <String, String> reviewId_bId = new HashMap <String, String>();							// HashMap of review id to business id
	private static HashMap <String, ArrayList<String>> bId_reviewIds = new HashMap <String, ArrayList<String>>();	// HashMap of businessId to reviewId ArrayList
	private static HashMap <String, String> reviewId_text = new HashMap <String, String>();							// HashMap of reviewId to reviewText
	private static HashMap <String, Double> reviewWord_tfidf = new HashMap <String, Double>();						// HashMap of reviewWord to tfIdf
	
	// Similarity Metric HashMaps
	private static HashMap <String, String> bId_initCategories = new HashMap<String, String>();						// HashMap of bId to initializer categories
	private static HashMap <String, Double> bId_categorySimilarity = new HashMap <String, Double>();				// HashMap of bId to categorySimilarity
	private static HashMap <String, Double> bId_reviewSimilarity = new HashMap <String, Double>();					// HashMap of bId to reviewSimilarity
	
	// Word Frequency Tables
	static WordFrequencyTable categoryWordFrequencyTable = new WordFrequencyTable();
	static WordFrequencyTable reviewWordFrequencyTable = new WordFrequencyTable();
	
	// Assignment 3 Additions
	private static HashMap <String, Double> bId_latitude = new HashMap <String, Double>();
	private static HashMap <String, Double> bId_longitude = new HashMap <String, Double>();
	
	public static void main(String[] args) {
		
		// Convert JSON file to business object array
		System.out.println("Fetching business json file data...");
		getJsonBusinesses();
		
		// Populate Business HashMaps
		System.out.println("Populating Business Hashmaps...");
		populateBusinessHashmaps();
		
		// Create a NeighborMap and add the neighboring businesses to it
        System.out.println("Populating Neighboring Businesses Hashmap...");
        populateDistanceHashmaps();
		
		// Initialize Persistent Hash Table
		System.out.println("Initializing Persistent Hash Table...");
		initializeBusinessIdFileNameHashTable();
		
		// Convert JSON file to reviews object array
		System.out.println("Fetching review json file data...");
		getJsonReviews();
		
		// Populate Review HashMaps
		System.out.println("Populating Review Hashmaps...");
		populateReviewsHashmaps();
		
		// Calculate Similarity Metrics of Categories and Reviews
		calculateSimilarityMetrics();
        
        System.out.println("Gathering Points...");
        points = getPoints();
        
        System.out.println("Clustering Points...");
        clusters = clusterPoints(points, NUM_CLUSTERS, MAX_ITER);

        System.out.println("Creating Clustering Points...");
        createClusterPoints(points);

		System.out.println("Writing business data to files...");
		writeBusinessesToFiles();
		
		System.out.println("Loader Processing Complete");
	}
	private static void getJsonBusinesses() {
		
		businesses = new Business[MAX_NUM];		// Array of business objects limited to MAX_NUM class constant
		
		try (BufferedReader reader = new BufferedReader(new FileReader(businessesAddress))) {
			 
			 // Class Variables
			 String line;						// Line of JSON file read in through buffered reader as a string
			 int index = 0;						// Index for adding to array of businesses
			 
			 // While line is not null
			 while ((line = reader.readLine()) != null) {		 
				 // Create new business object from JSON file line taking in business object variables
				 Business business = new Gson().fromJson(line, Business.class);
				 // Insert business information into object array of businesses
				 businesses[index] = business;				 
				 // Increment index counter
				 index++;
			 }
	    } catch(IOException e) {
	    	e.printStackTrace();
	    }
	}
	private static void getJsonReviews() {		
		try (BufferedReader reader = new BufferedReader(new FileReader(reviewsAddress))) {
			 
			 // Class Variables
			 String line;						// Line of JSON file read in through buffered reader as a string
			 int index = 0;						// Index for adding to array of businesses
			 
			 // While line is not null
			 while ((line = reader.readLine()) != null) {		 
				 // Create new review object from JSON file line taking in review object variables
				 Review review = new Gson().fromJson(line, Review.class);
				 // Insert business information into object array of reviews
				 reviews.add(index, review);				 
				 // Increment index counter
				 index++;
			 }
	    } catch(IOException e) {
	    	e.printStackTrace();
	    }
	}
	private static void populateBusinessHashmaps() {
		
		ArrayList<String> categories = new ArrayList<String>();			// Array initialized to hold categories of a single business
		
		// For each business within the array of businesses
		for (int b = 0; b < businesses.length; b++) {
			// If business categories are not null
			if (businesses[b].categories != null) {
				// Create array list of categories
				categories = new ArrayList<String>(Arrays.asList(businesses[b].categories));
				String[] categoriesArray = businesses[b].categories.split(", ");
				for (String category : categoriesArray) {
					// Add to array list containing all categories of all businesses
					catAryList.add(category);
					// Add to category word frequency table
					categoryWordFrequencyTable.add(category);
				}
				// Add to array list containing all array lists of categories of all businesses
				catAryAryList.add(categories);
			    // Put values into hashmaps
				bName_bId.put(businesses[b].name, businesses[b].business_id); 						// Hashmap of name->id
				bId_bName.put(businesses[b].business_id, businesses[b].name); 						// Hashmap of id->name
			    bId_category.put(businesses[b].business_id, businesses[b].categories.split(", ")); 	// Hashmap of id->categories
			    index_bId.put(b, businesses[b].business_id); 										// Hashmap of index->id (helps with accessing business_id from index)
			    bId_index.put(businesses[b].business_id, b); 										// Hashmap of id->index (helps with accessing catAryAryList)
			    bId_address.put(businesses[b].business_id, businesses[b].address);					// Hashmap of id->address
			    bId_latitude.put(businesses[b].business_id, businesses[b].latitude);
			    bId_longitude.put(businesses[b].business_id, businesses[b].longitude);
			}
		}
		System.out.println("Calculating Category TFIDFs...");
		// Traverse through populated businesses array to get TFIDF of each category
		for (int b = 0; b < businesses.length; b++) {			
			// If business categories are not null
			if (businesses[b].categories != null) {
				categories = new ArrayList<String>(Arrays.asList(businesses[b].categories));
				String[] categoriesArray = businesses[b].categories.split(", ");
				for (String category : categoriesArray) {
					if (!category_tfidf.containsKey(category)) {
						// Calculate tfidf and apply to category_tfidf hashmap
						category_tfidf.put(category, Tfidf.tfIdf(catAryList, catAryAryList, category, categoryWordFrequencyTable));
					}
				}
			}
		}
	}
	private static void populateDistanceHashmaps() {
		NeighborMap neighborMap = new NeighborMap();
		for (Business business : businesses) {
			List<Pair<Business, Double>> closestNeighbors = findClosestNeighbors(business, businesses);
			neighborMap.addNeighbor(business.business_id, closestNeighbors.get(1));
			neighborMap.addNeighbor(business.business_id, closestNeighbors.get(2));
			neighborMap.addNeighbor(business.business_id, closestNeighbors.get(3));
			neighborMap.addNeighbor(business.business_id, closestNeighbors.get(4));
			/*
			System.out.println("Target Business: " + business.name);
			System.out.println("Target Business Address: " + business.address + ", " + business.city + ", " + business.state);
			System.out.println("Closest Neighbor 1 Name: " + closestNeighbors.get(1).getKey().name);
			System.out.println("Closest Neighbor 1 Address: " + closestNeighbors.get(1).getKey().address + ", " + closestNeighbors.get(1).getKey().city + ", " + closestNeighbors.get(1).getKey().state);
			System.out.println("Closest Neighbor 1 Distance: " + closestNeighbors.get(1).getValue() + " km");
			System.out.println("Closest Neighbor 2 Name: " + closestNeighbors.get(2).getKey().name);
			System.out.println("Closest Neighbor 2 Address: " + closestNeighbors.get(2).getKey().address + ", " + closestNeighbors.get(2).getKey().city + ", " + closestNeighbors.get(2).getKey().state);
			System.out.println("Closest Neighbor 2 Distance: " + closestNeighbors.get(2).getValue() + " km");
			System.out.println("Closest Neighbor 3 Name: " + closestNeighbors.get(3).getKey().name);
			System.out.println("Closest Neighbor 3 Address: " + closestNeighbors.get(3).getKey().address + ", " + closestNeighbors.get(3).getKey().city + ", " + closestNeighbors.get(3).getKey().state);
			System.out.println("Closest Neighbor 3 Distance: " + closestNeighbors.get(3).getValue() + " km");
			System.out.println("Closest Neighbor 4 Name: " + closestNeighbors.get(4).getKey().name);
			System.out.println("Closest Neighbor 4 Address: " + closestNeighbors.get(4).getKey().address + ", " + closestNeighbors.get(4).getKey().city + ", " + closestNeighbors.get(4).getKey().state);
			System.out.println("Closest Neighbor 4 Distance: " + closestNeighbors.get(4).getValue() + " km");
			System.out.println("****************************************************************************");
			*/
		}
		try {
			neighborMap.saveMap();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void populateReviewsHashmaps() {
		
		ArrayList<String> reviewsArray = new ArrayList<String>();
		HashMap<String, Double> idfValues = new HashMap<String, Double>();

		// Add reviews to hashMaps
		for (int x = 0; x < reviews.size(); x++) {
			// If review text is not null
			if (reviews.get(x).text != null) {
			    // Put values into hashmaps
				if (bId_reviewIds.containsKey(reviews.get(x).business_id))
					bId_reviewIds.get(reviews.get(x).business_id).add(reviews.get(x).review_id);
				else {
					bId_reviewIds.put(reviews.get(x).business_id, new ArrayList<String>());
					bId_reviewIds.get(reviews.get(x).business_id).add(reviews.get(x).review_id);
				}
				reviewId_bId.put(reviews.get(x).review_id, reviews.get(x).business_id);
				reviewId_text.put(reviews.get(x).review_id, reviews.get(x).text);
			}
		}
		System.out.println("Calculating IDF Values...");
	    // Calculate IDF values for all terms in the reviews
	    for (int r = 0; r < reviews.size(); r++) {
	        if (reviews.get(r).text != null && bId_bName.containsKey(reviews.get(r).business_id)) {
	            reviewsArray = removeFillerWords(reviews.get(r).text);
	            for (String word : reviewsArray) {
	                if (!idfValues.containsKey(word)) {
	                    double idfValue = Tfidf.idf(revAryAryList, word, reviewWordFrequencyTable);
	                    idfValues.put(word, idfValue);
	                }
	            }
	        }
	    }

	    System.out.println("Filling Review Array Lists...");
	    // For each review within the array of reviews
	    for (int r = 0; r < reviews.size(); r++) {
	        // If review text is not null & bId_reviewId hashMap contains key value of review business id
	        if (reviews.get(r).text != null && bId_bName.containsKey(reviews.get(r).business_id)) {
	            // Put individual words of review into revAryList
	            reviewsArray = removeFillerWords(reviews.get(r).text);
	            for (String word : reviewsArray) {
	                revAryList.add(word);
	                reviewWordFrequencyTable.add(word);
	            }
	            // Put review array list into revAryAryList
	            revAryAryList.add(reviewsArray);
	        }
	    }

	    System.out.println("Calculating Review TFIDFs...");
	    // Traverse through populated businesses array to get TFIDF of each category
	    for (int r = 0; r < reviews.size(); r++) {
	        // If review text is not null & bId_reviewId hashMap contains key value of review business id
	        if (reviews.get(r).text != null && bId_bName.containsKey(reviews.get(r).business_id)) {
	            reviewsArray = removeFillerWords(reviews.get(r).text);
	            for (String word : reviewsArray) {
	                if (!reviewWord_tfidf.containsKey(word)) {
	                    // Retrieve pre-calculated IDF value from idfValues HashMap
	                    double idfValue = idfValues.get(word);
	                    // Calculate tfIdf and apply to reviewWord_tfidf hashMap
	                    double tfidfValue = Tfidf.tf(reviewsArray, word) * idfValue;
	                    reviewWord_tfidf.put(word, tfidfValue);
	                }
	            }
	        }
	    }
	}
	private static void initializeBusinessIdFileNameHashTable() {
        PersistentHashtable bId_fileName = new PersistentHashtable();
        
        for (Business business : businesses) {
        	bId_fileName.put(business.business_id, folderName + File.separator + business.business_id + ".bin");
        }        
        // save the hashTable to a file
        try {
            bId_fileName.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	private static ArrayList<String> removeFillerWords(String review) {
		ArrayList<String> shortenedReview = new ArrayList<String>();
		String[] reviewArray = review.split("[\\n\\s~!@#$%^&*()_+`1234567890\\-=;:\\\",<.>/?|\\[\\]]+"); // Removes all counts of spaces, new lines, numbers, and most symbols
		for (String word : reviewArray) {
			if (!Arrays.asList(FILLER_WORDS).contains(word.toLowerCase()) && !word.equals(""))
				shortenedReview.add(word);
		}
		return shortenedReview;
	}
	public static void writeBusinessesToFiles() {
	//***************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************
	//
	//                                  FORMAT OF BUSINESS DATA BYTEBUFFER:                            ___________________________________________________________________________________________________________________                                                     ______________________                   _________________________
	//                                                                                                //                                             ASSIGNMENT 3 ADDITIONS                                              \\                                                   //    *revIdsCount    \\                 //      *reviewCount     \\
	//	 -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	//  |     INT    ||    INT    ||  STRING  ||     INT    ||  STRING  ||      INT      ||  STRING  ||    INT     || STRING ||    INT      || STRING ||     INT     ||    DOUBLE   ||   DOUBLE   ||   DOUBLE  ||   INT   ||       INT        ||    STRING    ||     INT     ||     INT     || STRING ||      INT     ||     INT      ||  STRING  ||       DOUBLE       ||      DOUBLE      ||       INT     ||
	//  | totalBytes ||  bIdBytes ||   bId    || bNameBytes ||  bName   || bAddressBytes || bAddress || bCityBytes || bCity  || bStateBytes || bState || bPostalCode ||  bLatitude  || bLongitude ||   bStars  || bIsOpen || bCategoriesBytes ||  bCategories || revIdsCount || revIdBytes  || revId  || reviewCount  || reviewBytes  ||  review  || categorySimilarity || reviewSimilarity || clusterNumber ||
	//	 -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	//
	//***************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************************
		int totalByteLength;
		int bIdLength;
		int bNameLength;
		int bAddressLength;
		
		// Assignment 3 Additions
		int bCityLength;
		int bStateLength;
		//***********************
		
		int bCategoriesLength;
		int revIdsByteLength;
		int reviewsByteLength;
		int revIdsCount;
		int reviewCount;
		String bId;
		String bName;
		String bAddress;
		
		// Assignment 3 Additions
		String bCity;
		String bState;
		int bPostalCode;
		double bLatitude;
		double bLongitude;
		double bStars;
		int bIsOpen;
		//************************
		
		String bCategories;
		double x;
		double y;
		int cluster;
		
		// Create the BusinessData folder if it does not exist
		if (!folder.exists()) {
			folder.mkdir();
		}
        PersistentHashtable hashtable = new PersistentHashtable();
        // load the hashTable from the file
        try {
            hashtable.load();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
		// Write data for each business to separate file
		for (Business business : businesses) {
			// Create new ArrayLists for revIds and reviews
			ArrayList<String> revIds = new ArrayList<String>();
			ArrayList<ArrayList<String>> reviews = new ArrayList<ArrayList<String>>();
			// Get data needed for file
			bId = business.business_id;
			bIdLength = bId.getBytes().length;
			bName = business.name;
			bNameLength = bName.getBytes().length;
			bAddress = business.address;
			bAddressLength = bAddress.getBytes().length;
			
			bCity = business.city;
			bCityLength = business.city.getBytes().length;
			bState = business.state;
			bStateLength = business.state.getBytes().length;
			bPostalCode = business.postalCode;
			bLatitude = business.latitude;
			bLongitude = business.longitude;
			bStars = business.stars;
			bIsOpen = business.isOpen;
			
			bCategories = business.categories;
			if (bCategories == null) {
				bCategories = "None";
			}
			bCategoriesLength = bCategories.getBytes().length;
			revIdsByteLength = 0;
			for (String reviewId : bId_reviewIds.get(bId)) {
				revIds.add(reviewId);
				revIdsByteLength += Integer.BYTES + reviewId.getBytes().length;
			}
			for (String reviewId : revIds) {
				reviews.add(removeFillerWords(reviewId_text.get(reviewId)));
			}
			reviewsByteLength = 0;
			for (ArrayList<String> review : reviews) {
				reviewsByteLength += (Integer.BYTES + review.toString().getBytes().length);
			}
			
			revIdsCount = revIds.size();
			reviewCount = revIdsCount;
			
			if (bId_categorySimilarity.get(bId) != null) {
				x = bId_categorySimilarity.get(bId);
			}
			else {
				x = 0.0;
			}
			if (bId_reviewSimilarity.get(bId) != null) {
				y = bId_reviewSimilarity.get(bId);
			}
			else {
				y = 0.0;
			}
			Point bIdPoints = new Point(x, y);
			cluster = 0;
			for (Point point : points) {
				if (x == point.x && y == point.y) {
					cluster = point.clusterID;
				}
			}
			
			totalByteLength = Integer.BYTES + bIdLength + Integer.BYTES + bNameLength + Integer.BYTES + bAddressLength + Integer.BYTES + bCityLength + Integer.BYTES + bStateLength + Integer.BYTES + Double.BYTES + Double.BYTES + Double.BYTES + Integer.BYTES + Integer.BYTES + bCategoriesLength + Integer.BYTES + revIdsByteLength + Integer.BYTES + reviewsByteLength + Double.BYTES + Double.BYTES + Integer.BYTES;
			
			// Allocate ByteBuffer for data
			ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + Integer.BYTES + bIdLength + Integer.BYTES + bNameLength + Integer.BYTES + bAddressLength + Integer.BYTES + bCityLength + Integer.BYTES + bStateLength + Integer.BYTES + Double.BYTES + Double.BYTES + Double.BYTES + Integer.BYTES + Integer.BYTES + bCategoriesLength + Integer.BYTES + revIdsByteLength + Integer.BYTES + reviewsByteLength + Double.BYTES + Double.BYTES + Integer.BYTES);
			
			// Write data to file under current directory/BusinessData/"bId".bin
			try (FileOutputStream out = new FileOutputStream(hashtable.get(bId));
				 FileChannel outChannel = out.getChannel()) {
					 buffer.putInt(totalByteLength);
					 buffer.putInt(bIdLength);
					 buffer.put(bId.getBytes());
					 buffer.putInt(bNameLength);
					 buffer.put(bName.getBytes());
					 buffer.putInt(bAddressLength);
					 buffer.put(bAddress.getBytes());
					 
					 // Assignment 3 Additions
					 buffer.putInt(bCityLength);
					 buffer.put(bCity.getBytes());
					 buffer.putInt(bStateLength);
					 buffer.put(bState.getBytes());
					 buffer.putInt(bPostalCode);
					 buffer.putDouble(bLatitude);
					 buffer.putDouble(bLongitude);
					 buffer.putDouble(bStars);
					 buffer.putInt(bIsOpen);
					 //**************************
					 
					 buffer.putInt(bCategoriesLength);
					 buffer.put(bCategories.getBytes());
					 buffer.putInt(revIdsCount);
					 for (String reviewId : revIds) {
						 buffer.putInt(reviewId.getBytes().length);
						 buffer.put(reviewId.getBytes());
					 }
					 buffer.putInt(reviewCount);
					 for (ArrayList<String> review : reviews) {
						 buffer.putInt(review.toString().getBytes().length);
						 buffer.put(review.toString().getBytes());
					 }
					 buffer.putDouble(x);
					 buffer.putDouble(y);
					 buffer.putInt(cluster);
					 buffer.flip();
					 outChannel.write(buffer);
					 buffer.clear();
			} catch (IOException e) {
				System.err.println("Error writing input to file: " + e.getMessage());
				return;
			}
		}		
	}
	public static void readBusinessIdFromFile(String bId) {
		int totalByteSize;
		int bIdByteLength;
		int bNameByteLength;
		int bAddressByteLength;
		int bCategoriesByteLength;
		int revIdByteLength;
		int reviewByteLength;
		int revIdsCount;
		int reviewCount;
		int count;
		String bizId;
		String bName;
		String bAddress;
		
		// Assignment 3 Additions
		int bCityByteLength;
		String bCity;
		int bStateByteLength;
		String bState;
		int bPostalCode;
		double bLatitude;
		double bLongitude;
		double bStars;
		int bIsOpen;
		//************************
		
		String bCategories;
		double x;
		double y;
		int cluster;
		String revId;
		String reviewString;
		String fileName;
		ArrayList<String> revIds = new ArrayList<String>();
		ArrayList<String> review = new ArrayList<String>();
		ArrayList<ArrayList<String>> reviews = new ArrayList<ArrayList<String>>();
		ByteBuffer byteSizeBuffer = ByteBuffer.allocate(Integer.BYTES);
		PersistentHashtable hashtable = new PersistentHashtable();
        // load the hashTable from the file
        try {
            hashtable.load();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        fileName = hashtable.get(bId);

		// Try reading file input stream channel from file
		try (FileInputStream inputStream = new FileInputStream(fileName);
			 FileChannel inChannel = inputStream.getChannel()) {
			
			// Clear byteSizeBuffer
			byteSizeBuffer.clear();
			
			// Read inChannel and write to byteSizeBuffer
			inChannel.read(byteSizeBuffer);
			
			// Flip byteSizeBuffer
			byteSizeBuffer.flip();
			
			// Get totalByteSize from byteSizeBuffer
			totalByteSize = byteSizeBuffer.getInt();
			System.out.println("Total Byte Size: " + totalByteSize);
			
			// Allocate buffer of totalByteSize bytes
			ByteBuffer buffer = ByteBuffer.allocate(totalByteSize);
			
			// Read inChannel and write to buffer
			inChannel.read(buffer);
			
			// Flip buffer
			buffer.flip();
			
			// Read bIdByteLength from buffer
			bIdByteLength = buffer.getInt();
			
			// Read bizId from buffer
			byte[] bizIdBytes = new byte[bIdByteLength];
			buffer.get(bizIdBytes);
			bizId = new String(bizIdBytes);
			
			System.out.println("bIdByteLength: " + bIdByteLength);
			System.out.println("bizId: " + bizId);
			
			// Read bNameByteLength from buffer
			bNameByteLength = buffer.getInt();
			
			// Read bNameByteLength from buffer
			byte[] bNameBytes = new byte[bNameByteLength];
			buffer.get(bNameBytes);
			bName = new String(bNameBytes);
			
			System.out.println("bNameByteLength: " + bNameByteLength);
			System.out.println("bName: " + bName);
			
			// Read bAddressByteLength from buffer
			bAddressByteLength = buffer.getInt();
			
			// Read bAddress from buffer
			byte[] bAddressBytes = new byte[bAddressByteLength];
			buffer.get(bAddressBytes);
			bAddress = new String(bAddressBytes);
			
			System.out.println("bAddressByteLength: " + bAddressByteLength);
			System.out.println("bAddress: " + bAddress);
			
			// Assignment 3 Additions ***********************
			
			// Read bCityByteLength from buffer
			bCityByteLength = buffer.getInt();
			// Read bCity from buffer
			byte[] bCityBytes = new byte[bCityByteLength];
			buffer.get(bCityBytes);
			bCity = new String(bCityBytes);
			
			// Read bStateByteLength from buffer
			bStateByteLength = buffer.getInt();
			// Read bState
			byte[] bStateBytes = new byte[bStateByteLength];
			buffer.get(bStateBytes);
			bState = new String(bStateBytes);
			
			// Read bPostalCode from buffer
			bPostalCode = buffer.getInt();
			// Read bLatitude from buffer
			bLatitude = buffer.getDouble();
			// Read bLongitude from buffer
			bLongitude = buffer.getDouble();
			// Read bStars from buffer
			bStars = buffer.getDouble();
			// Read bIsOpen from buffer
			bIsOpen = buffer.getInt();
			//***********************************************
			
			// Read bCategoriesByteLength from buffer
			bCategoriesByteLength = buffer.getInt();
			
			// Read bCategories from buffer
			byte[] bCategoriesBytes = new byte[bCategoriesByteLength];
			buffer.get(bCategoriesBytes);
			bCategories = new String(bCategoriesBytes);
			
			System.out.println("bCategoriesByteLength: " + bCategoriesByteLength);
			System.out.println("bCategories: " + bCategories);
			
			// Read revIdsCount from buffer
			revIdsCount = buffer.getInt();
			System.out.println("reviewCount: " + revIdsCount);
			
			// Read revIds from buffer
			for (count = 0; count < revIdsCount; count++) {
				revIdByteLength = buffer.getInt();
				byte[] revIdBytes = new byte[revIdByteLength];
				buffer.get(revIdBytes);
				revId = new String(revIdBytes);
				revIds.add(revId);
			}
			
			System.out.println("revIds: ");
			for (String rev : revIds) {
				System.out.println(rev);
			}
			
			// Read reviewCount from buffer
			reviewCount = buffer.getInt();			
			System.out.println("reviewCount: " + reviewCount);
			
			// Read reviews from buffer
			for (count = 0; count < reviewCount; count++) {
				reviewByteLength = buffer.getInt();
				byte[] reviewBytes = new byte[reviewByteLength];
				buffer.get(reviewBytes);
				reviewString = new String(reviewBytes);
				
				// Remove "[" and "]" at beginning and end of review string
				reviewString = reviewString.substring(1);
				reviewString = reviewString.substring(0, reviewString.length() - 1);
				
				// Convert reviewString into review ArrayList<String>
				review = new ArrayList<String>(Arrays.asList(reviewString));
				
				// Add review ArrayList<String> to reviews ArrayList<ArrayList<String>>
				reviews.add(review);
			}
			
			System.out.println("reviews: ");
			for (ArrayList<String> rev : reviews) {
				System.out.println(rev);
			}
			
			// Get x-coordinate, y-coordinate, and cluster number from buffer
			x = buffer.getDouble();
			y = buffer.getDouble();
			cluster = buffer.getInt();
			
			System.out.println("X-Coordinate: " + x);
			System.out.println("Y-Coordinate: " + y);
			System.out.println("Cluster #: " + cluster);
			
		} catch (IOException e) {
			System.err.println("Error reading input from file: " + e.getMessage());
            return;
		}
	}
    private static void calculateSimilarityMetrics () {
    	calculateCategorySimilarities();
		calculateReviewSimilarities();
    }
    private static void calculateCategorySimilarities() {
        System.out.println("Calculating Category Similarities...");
        for (Business business : businesses) {
            if (business.categories != null) {
                String[] businessCategories = business.categories.split(", ");
                String initCategory = businessCategories[0];
                bId_initCategories.put(business.business_id, initCategory);
                double cachedSimilarity = bId_categorySimilarity.getOrDefault(business.business_id, -1.0);
                if (cachedSimilarity == -1.0 || !initCategory.equals(bId_initCategories.get(business.business_id))) {
                    double similarity = CosineSimilarity.cosineSimilarity(new ArrayList<String>(Arrays.asList(initCategory)), new ArrayList<String>(Arrays.asList(businessCategories)), category_tfidf);
                    bId_categorySimilarity.put(business.business_id, similarity);
                }
            }
        }
    }
    private static void calculateReviewSimilarities() {
	    System.out.println("Calculating Review Similarities...");
	    
	    // Get initializer reviewWords from single bId's reviews
	    ArrayList<String> initReviewWords = new ArrayList<String>();
	    for (String bRevId : bId_reviewIds.get("GRMKMKiNabc9DNwCMNgG0Q")) {
	    	String review = reviewId_text.get(bRevId);
	    	initReviewWords = removeFillerWords(review);
	    }
	    
	    // For each business, calculate cosine similarity of review words compared to initializer review words
	    for (Business business : businesses) {
	        ArrayList<String> reviewWords = new ArrayList<String>();
	        // For each entry of the bId_reviewId hashmap with business id as key
	        for (String reviewId : bId_reviewIds.get(business.business_id)) {
		    	String review = reviewId_text.get(reviewId);
		    	reviewWords = removeFillerWords(review);
	        }
	        // Get cosine similarity
	        double x = CosineSimilarity.cosineSimilarity(initReviewWords, reviewWords, reviewWord_tfidf);
	        if (Double.isNaN(x)) {
		        bId_reviewSimilarity.put(business.business_id, 0.0);
	        }
	        else {
		        bId_reviewSimilarity.put(business.business_id, x);
	        }
	        //System.out.println("Review similarity of " + business.business_id + " : " + bId_reviewSimilarity.get(business.business_id));
	    }
    }
    private static void createClusterPoints(List<Point> points){
        List<Point>[] cluster_list = new ArrayList[NUM_CLUSTERS];
        for (int i = 0; i < NUM_CLUSTERS; i++) {
        	// Create a new array with 10 array lists of point objects
            cluster_list[i] = new ArrayList<Point>();
        }

        for (Point p: points){
            cluster_list[p.clusterID].add(p);
        }
        for (int i = 0; i < NUM_CLUSTERS; i++){
            cluster_points.put(i, cluster_list[i]);
        }
    }
    private static ArrayList<Point> getPoints(){
    	ArrayList<Point> points = new ArrayList<Point>();
         int numPoints = 10000;
         double x;
         double y;
         // Access the similarity metric from the files
         
         // Generate points;
         for (int i = 0; i < numPoints; i++) {
        	 String businessId = businesses[i].business_id;
        	 if (bId_categorySimilarity.get(businessId) != null) {
        		 x = bId_categorySimilarity.get(businessId);
        	 }
        	 else {
        		 x = 0.0;
        	 }
        	 if (bId_reviewSimilarity.get(businessId) != null) {
        		 y = bId_reviewSimilarity.get(businessId); 
        	 }
        	 else {
        		 y = 0.0;
        	 }
        	 Point point = new Point(x, y);
        	 point.addBusinessId(businessId);
        	 points.add(point);
         }
         return points;
    }
	private static List<Point> clusterPoints(List<Point> points, int numClusters, int MAX_ITER) {
        Random rand = new Random();
        // Create clusters
        List<Point> clusters = new ArrayList<>();
        for (int i = 0; i < numClusters; i++) {
            double x = rand.nextDouble();
            double y = rand.nextDouble();
            clusters.add(new Point(x, y));
        }
        // Cluster the points
        int iter = 0;
        while (true) {
            List<List<Point>> clustersList = new ArrayList<>(numClusters);
            for (int i = 0; i < numClusters; i++) {
                clustersList.add(new ArrayList<>());
            }
            for (Point point : points) {
                int nearestCluster = findNearestCluster(point, clusters);
                clustersList.get(nearestCluster).add(point);
            }
            List<Point> newClusters = new ArrayList<>();
            for (List<Point> cluster : clustersList) {
                if (cluster.isEmpty()) {
                    newClusters.add(clusters.get(rand.nextInt(numClusters)));
                } else {
                    newClusters.add(calculateMean(cluster));
                }
            }
            iter++;
            if (newClusters.equals(clusters) || iter > MAX_ITER) {
                break;
            } else {
                clusters = newClusters;
            }
        }
        return clusters;
    }
    private static int findNearestCluster(Point point, List<Point> clusters) {
        double minDist = Double.MAX_VALUE;
        int nearestCluster = -1;
        for (int i = 0; i < clusters.size(); i++) {
            double dist = point.distance(clusters.get(i));
            if (dist < minDist) {
                minDist = dist;
                nearestCluster = i;
            }
        }
        point.addToCluster(nearestCluster);
        return nearestCluster;
    }
    private static Point calculateMean(List<Point> points) {
        double sumX = 0.0;
        double sumY = 0.0;
        for (Point point : points) {
            sumX += point.x;
            sumY += point.y;
        }
        double meanX = sumX / points.size();
        double meanY = sumY / points.size();
        return new Point(meanX, meanY);
    }
    private static List<Pair<Business, Double>> findClosestNeighbors(Business target, Business[] businesses) {
        final int NUM_NEIGHBORS = 5; // Number of closest neighbors to find (neighbors[0] will be the target business itself)
        List<Pair<Business, Double>> neighbors = new ArrayList<>();	// Neighbors list

        // Make copy of businesses array to sort
        Business[] sortedBusinesses = Arrays.copyOf(businesses, businesses.length);
        
        // Sort businesses by distance to target using haversine formula
        Arrays.sort(sortedBusinesses, Comparator.comparingDouble(business ->Haversine. haversine(target, business)));

        // Add the closest NUM_NEIGHBORS businesses to the neighbors list with their distances
        for (int i = 0; i < NUM_NEIGHBORS; i++) {
            double distance = Haversine.haversine(target, sortedBusinesses[i]);
            neighbors.add(new Pair<Business, Double>(sortedBusinesses[i], distance));
        }
        return neighbors;
    }
}