//*************************************************************************************************
//
//	Brandon LaPointe & Param Rajguru
//	CSC365 - Professor Doug Lea
//	Application.java
//
package application;

import common.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Application implements ActionListener {
	
	// Class Constants & Variables
	private static final int NUM_SIMILAR_BUSINESSES = 2;											// Number of similar businesses fetched from array
	private static final int MAX_SEARCH_CATEGORIES = 64;											// Maximum number of categories within a single business for array sizing
	private static ArrayList<String> catAryList = new ArrayList<String>();							// Expandable array list containing all of the categories from all businesses
	private static ArrayList<ArrayList<String>> catAryAryList = new ArrayList<ArrayList<String>>();	// Array list containing string arrays of categories from all businesses
	// Businesses HashMaps
	private static HashMap <String, String> bName_bId = new HashMap <String, String>();				// HashMap of business name to business id
	private static HashMap <String, String> bId_bName = new HashMap <String, String>();				// HashMap of business id to business name
	private static HashMap <String, String[]> bId_category = new HashMap <String, String[]>();		// HashMap of business id to business categories
	private static HashMap <Integer, String> index_bId = new HashMap <Integer, String>();			// HashMap of index to business id
	private static HashMap <String, Integer> bId_index = new HashMap <String, Integer>();			// HashMap of business id to index
	private static HashMap <String, Double> category_tfidf = new HashMap <String, Double>();		// HashMap of categories to tfIdf
	private static HashMap <String, String> bId_address = new HashMap <String, String>();			// HashMap of business id to address
	// K-Means Clustering Variables
    public static HashMap<Integer, List<Point>> cluster_points = new HashMap<Integer, List<Point>>();
    private static int NUM_CLUSTERS = 10;
    private static ArrayList<Point> points = new ArrayList<Point>();
	// GUI objects
	private static JLabel titleBox;				// Title box
	private static JLabel searchLabel;			// Search box label
	private static JTextField searchText;		// Search Text
	private static JButton button;				// Search button
	private static JLabel responseLabel;		// Response box label
	private static JLabel responseLabel2;		// Response box label2
	private static JLabel responseName;			// Result name 1 of similarity search
	private static JLabel responseName2;		// Result name 2 of similarity search
	private static JLabel responseAddress;		// Result address 1 of similarity search
	private static JLabel responseAddress2;		// Result address 2 of similarity search	
	private static JLabel responseCluster;		// Result cluster of searched business
	private static JLabel responseSimilarKey;	// Resulting most similar business key using K-Means Clusters
	private static JLabel responseSimilarName;	// Resulting most similar business name using K-Means Clusters
	private static String[] searchResultNames = new String[NUM_SIMILAR_BUSINESSES];					// Array containing the search result of the 3 most similar business names
	private static String[] searchResultAddresses = new String[NUM_SIMILAR_BUSINESSES];				// Array containing the search result of the 3 most similar business addresses
	private static int searchCluster;
	private static String searchSimilarKey;
	private static String searchSimilarName;
	// Persistent Hash Table of FileNames
	private static PersistentHashtable bId_fileName = new PersistentHashtable();
	// Persistent Hash Map of Neighbors
	private static NeighborMap neighborMap = new NeighborMap();
	// Word Frequency Tables
	static WordFrequencyTable categoryWordFrequencyTable = new WordFrequencyTable();
	// Business Array of Businesses
	private static Business[] businesses;
    
	public static void main (String[] args) {
		// Load persistent hash table
		loadPersistentHashTable();

        // Load the neighbor map
        loadNeighborMap();

        // Populate businesses array from file
		populateBusinessesFromFiles();
		
		// Populate business hashmaps
		populateBusinessHashmaps();
		
		// Populate point hashmaps
		populatePointHashmaps();
		
		// Create graph/adjacency matrix
		populateGraph();
        
        // Start GUI
        startGUI();
	}
	private static void loadPersistentHashTable() {
		System.out.println("Loading Persistent FileName Hash Table...");
        try {
            bId_fileName.load();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
		
	}
    private static void loadNeighborMap() {
    	System.out.println("Loading Persistent Neighbors Hash Map...");
        try {
            neighborMap.loadMap();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading neighbor map: " + e.getMessage());
        }
		
	}
	private static void populateBusinessesFromFiles() {
        System.out.println("Populating Businesses From Files...");
        businesses = new Business[bId_fileName.hashtable.size()];
        int index = 0;        
        // Traverse through persistent hash table to get business data
        for (Entry<String, String> entry : bId_fileName.hashtable.entrySet()) {
        	businesses[index] = readBusinessIdFromFile(entry.getKey());
        	index++;
        }
    }
	private static void populateBusinessHashmaps() {
		ArrayList<String> categories = new ArrayList<String>();			// Array initialized to hold categories of a single business
		
		System.out.println("Populating Business Hash Maps...");
		
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
			}
		}
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
	private static void populatePointHashmaps() {
        for (Business business : businesses) {
	       	 String businessId = business.business_id;
	       	 double x = business.categorySimilarity;
	       	 double y = business.reviewSimilarity;
	       	 Point point = new Point(x, y);
	       	 point.addBusinessId(businessId);
	       	 points.add(point);
        }
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
	public static void startGUI () {
		
		//Create JPanel object
		JPanel panel = new JPanel();
		//Create JFrame object
		JFrame frame = new JFrame();
		
		//Setup frame
		frame.setSize(600, 380);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(panel);
		
		//Setup panel
		panel.setLayout(null);
		
		//Setup title box
		titleBox = new JLabel("----------YELP BUSINESS DATASET SIMILARITY SEARCH TOOL----------");
		titleBox.setBounds(60, 5, 500, 25);
		panel.add(titleBox);
		
		//Setup search label
		searchLabel = new JLabel("Buisiness Name :");
		searchLabel.setBounds(20, 40, 120, 25);
		panel.add(searchLabel);
		
		//Setup search text box
		searchText = new JTextField();
		searchText.setBounds(130, 40, 300, 25);
		panel.add(searchText);
		
		//Setup search button
		button = new JButton("Search");
		button.setBounds(440, 40, 80, 25);
		button.addActionListener(new Application());
		panel.add(button);
		
		//Setup result label
		responseLabel = new JLabel("Cosine Similarity:");
		responseLabel.setBounds(20, 105, 120, 25);
		panel.add(responseLabel);
		
		// Setup first response label set
		responseName = new JLabel("");
		responseName.setBounds(170, 70, 500, 25);
		panel.add(responseName);
		responseAddress = new JLabel("");
		responseAddress.setBounds(170, 90, 500, 25);
		panel.add(responseAddress);
		
		// Setup second response label set
		responseName2 = new JLabel("");
		responseName2.setBounds(170, 120, 500, 25);
		panel.add(responseName2);
		responseAddress2 = new JLabel("");
		responseAddress2.setBounds(170, 140, 500, 25);
		panel.add(responseAddress2);
		
		//Setup result label2
		responseLabel2 = new JLabel("K-Means Similarity:");
		responseLabel2.setBounds(20, 220, 150, 25);
		panel.add(responseLabel2);
		
		// Setup third response label set
		responseCluster = new JLabel("");
		responseCluster.setBounds(170, 200, 500, 25);
		panel.add(responseCluster);
		
		// Setup fourth response label set
		responseSimilarKey = new JLabel("");
		responseSimilarKey.setBounds(170, 220, 500, 25);
		panel.add(responseSimilarKey);
		responseSimilarName = new JLabel("");
		responseSimilarName.setBounds(170, 240, 500, 25);
		panel.add(responseSimilarName);
		
		//Set frame to be visible and in focus
		frame.setVisible(true);
	}
	public void actionPerformed(ActionEvent e) {
		
		// Convert searchText box input to string
		String search = searchText.getText();
		responseName.setText("");
		responseName2.setText("");
		responseAddress.setText("");
		responseAddress2.setText("");
		responseCluster.setText("");
		responseSimilarKey.setText("");
		responseSimilarName.setText("");
		
		// If search field is empty display error message on GUI
		if (search.equalsIgnoreCase("")) {
			responseName.setText("ERROR - Empty Search Text Field");
			responseName2.setText("");
			responseAddress.setText("");
			responseAddress2.setText("");
			responseCluster.setText("");
			responseSimilarKey.setText("");
			responseSimilarName.setText("");
		}
		// Else if search field contains characters
		else if (!search.equalsIgnoreCase("")) {
			// If searched business name is found within businesses array
			if (validName(search, businesses) == true) {
				// Search for similar businesses
				searchResultNames = searchBusinesses(search);
				searchResultAddresses = getSimilarAddresses(search);
				for(int i = 0; i < businesses.length; i++) {
					if(businesses[i].name.equals(search)) {
						searchCluster = businesses[i].cluster;
						searchSimilarKey = getClosestPointBid(businesses[i]);
						searchSimilarName = bId_bName.get(searchSimilarKey);
					}
				}
				List<Pair<Business, Double>> neighbors = neighborMap.getNeighborList(businesses[bId_index.get(bName_bId.get(search))]);
				if (neighbors == null) {
					System.out.println("Error - Null Neighbors List");
				}
				else {
					System.out.println("Neighboring Businesses: ");
					System.out.println(neighbors.get(0).getKey().name + " - " + neighbors.get(0).getValue() + " km away\n" +
									   neighbors.get(1).getKey().name + " - " + neighbors.get(1).getValue() + " km away\n" +
									   neighbors.get(2).getKey().name + " - " + neighbors.get(2).getValue() + " km away\n" +
									   neighbors.get(3).getKey().name + " - " + neighbors.get(3).getValue() + " km away");
				}
				// Display the results on GUI
				responseName.setText("Name : " + searchResultNames[0]);
				responseName2.setText("Name : " + searchResultNames[1]);
				responseAddress.setText("Address : " + searchResultAddresses[0]);
				responseAddress2.setText("Address : " + searchResultAddresses[1]);
				responseCluster.setText("Cluster: " + searchCluster);
				responseSimilarKey.setText("Similar Business Key: " + searchSimilarKey);
				responseSimilarName.setText("Similar Business Name: " + searchSimilarName);
			}
			// Else business name not found within businesses array
			else {
				// Display error message on GUI
				responseName.setText("ERROR - Business not found within dataset");
				responseName2.setText("");
				responseAddress.setText("");
				responseAddress2.setText("");
				responseCluster.setText("");
				responseSimilarKey.setText("");
				responseSimilarName.setText("");
			}
		}
	}
	private static boolean validName(String name, Business[] businesses){
		int i = 0;
		for(i = 0; i < businesses.length; i++) {
			if(businesses[i].name.equals(name)) {
				return true;
			}
		}
		return false;
	}
	private static String[] searchBusinesses(String businessName) {
		// Instantiate string arrays for search and result
		String[] searchCategories = new String[MAX_SEARCH_CATEGORIES];
		String[] resultIds = new String[NUM_SIMILAR_BUSINESSES];
		String[] resultNames = new String[NUM_SIMILAR_BUSINESSES];
		
		// Get search business id
		String searchId = bName_bId.get(businessName);
		
		// Get string array of categories from given business name to search
		searchCategories = bId_category.get(searchId);
		
		// Search using similarBusinesses
		resultIds = similarBusinesses(new ArrayList<String>(Arrays.asList(searchCategories)), bId_category, category_tfidf, searchId);
		
		// For each resulting similar business id
		for(int count = 0; count < resultIds.length; count++) {
			// Convert business id to business name
			resultNames[count] = bId_bName.get(resultIds[count]);	
		}
		// Return string array containing resulting similar business names
		return resultNames;
	}
	private static String[] getSimilarAddresses(String businessName) {
		//Get categories of given search term
		String[] searchCategories = new String[MAX_SEARCH_CATEGORIES];
		String[] resultAddresses = new String[NUM_SIMILAR_BUSINESSES];
		
		// Get search business id
		String searchId = bName_bId.get(businessName);
		
		// Get string array of categories from given business name to search
		searchCategories = bId_category.get(bName_bId.get(businessName));
		
		// Search using similarBusinesses
		resultAddresses = similarBusinesses(new ArrayList<String>(Arrays.asList(searchCategories)), bId_category, category_tfidf, searchId);
		
		// For each resulting similar business id
		for(int count = 0; count < resultAddresses.length; count++) {
			// Convert business id to business name
			resultAddresses[count] = bId_address.get(resultAddresses[count]);	
		}
		// Return string array containing resulting similar business names
		return resultAddresses;
	}
	private static String[] similarBusinesses(ArrayList<String> category, Map<String, String[]> bId_category, Map<String, Double> tfIdfMap, String searchId) {
		String[] bizIds = new String[NUM_SIMILAR_BUSINESSES]; 					// contains business IDs of 3 businesses with highest similarity metrics
		double[] similarityMetric = new double[NUM_SIMILAR_BUSINESSES]; 		// contains similarity metric values of 3 businesses with highest similarity metrics
		
		// For each entry of the bId_category hashmap
		for (Map.Entry<String, String[]> set : bId_category.entrySet()) {
			// If searched business id is not equal to the entry of the bId_category hashmap
			if(searchId != set.getKey()) {
				// Get cosine similarity
				double x = CosineSimilarity.cosineSimilarity(category, new ArrayList<String>(Arrays.asList(set.getValue())), tfIdfMap);
				// If cosine similarity of entry is greater than any of the stored values within the array
				if(x > similarityMetric[0] || x > similarityMetric[1]){
					// Replace the lowest similarity metric value
					int lowest_index = getSmallestValueIndex(similarityMetric);
					similarityMetric[lowest_index] = x;
					bizIds[lowest_index] = set.getKey();
				}
			}
		}
		// Update bizIds
		int[] arr = returnSortArrayIndex(similarityMetric);
		
		// 2nd and 3rd most similar bizIds will be stored as most similar will be the same business itself
		String[] upBizIds = new String[NUM_SIMILAR_BUSINESSES];
		for(int count = 0; count < NUM_SIMILAR_BUSINESSES; count++) {
			upBizIds[count] = bizIds[arr[count]];
		}
		
		// Swap bizIds so that it is in descending order
		int updateCount = 0;
		for(int count = bizIds.length - 1; count >= 0; count--) {
			upBizIds[updateCount] = bizIds[count];
			updateCount++;
		}
		
		// Return businessIds
		return upBizIds;
	}
	private static int[] returnSortArrayIndex(double[] inputArr) {
		int[] arr = new int[inputArr.length];
		for(int i = 0; i<inputArr.length; i++){
			arr[i] = i;
		}
		// Bubble Sort implementation
		for(int i=0; i<inputArr.length; i++) {
			for(int j=0; j<inputArr.length - i - 1; j++) {
				if(inputArr[j] > inputArr[j+1]) {
					// swap array values
					double temp = inputArr[j];
                    inputArr[j] = inputArr[j + 1];
                    inputArr[j + 1] = temp;

					// swap indices values
					int t = arr[j];
					arr[j] = arr[j + 1];
                    arr[j + 1] = t;
				}
			}
		}
		return arr;
	}
	private static int getSmallestValueIndex(double[] arr) {
		int lowest_value = 0;
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] < arr[lowest_value]) {
				lowest_value = i;
			}
		}
		return lowest_value;
	}
	public static Business readBusinessIdFromFile(String bId) {
		Business businessFromFile = new Business();
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
		
        fileName = bId_fileName.get(bId);

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
			businessFromFile.setId(bizId);
			
			// Read bNameByteLength from buffer
			bNameByteLength = buffer.getInt();
			
			// Read bNameByteLength from buffer
			byte[] bNameBytes = new byte[bNameByteLength];
			buffer.get(bNameBytes);
			bName = new String(bNameBytes);
			businessFromFile.setName(bName);
			
			// Read bAddressByteLength from buffer
			bAddressByteLength = buffer.getInt();
			
			// Read bAddress from buffer
			byte[] bAddressBytes = new byte[bAddressByteLength];
			buffer.get(bAddressBytes);
			bAddress = new String(bAddressBytes);
			businessFromFile.address = bAddress;
			
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
			businessFromFile.categories = bCategories;
			
			// Read revIdsCount from buffer
			revIdsCount = buffer.getInt();
			
			// Read revIds from buffer
			for (count = 0; count < revIdsCount; count++) {
				revIdByteLength = buffer.getInt();
				byte[] revIdBytes = new byte[revIdByteLength];
				buffer.get(revIdBytes);
				revId = new String(revIdBytes);
				revIds.add(revId);
			}
			businessFromFile.setReviewIds(revIds);
			
			// Read reviewCount from buffer
			reviewCount = buffer.getInt();
			
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
			businessFromFile.setReviews(reviews);
			
			// Get x-coordinate, y-coordinate, and cluster number from buffer
			x = buffer.getDouble();
			y  = buffer.getDouble();
			cluster = buffer.getInt();
			
			businessFromFile.setCategorySimilarity(x);
			businessFromFile.setReviewSimilarity(y);
			businessFromFile.setCluster(cluster);
			
			return businessFromFile;
			
		} catch (IOException e) {
			System.err.println("Error reading input from file: " + e.getMessage());
            return null;
		}
	}
    private static Point findClosestPointInCluster(Point point, int cluster_id, HashMap<Integer, List<Point>> map){
        List<Point> clusterPoints = map.get(cluster_id);
        Point closestPoint = clusterPoints.get(0);    
        double minDist = Double.MAX_VALUE;
        for(Point p: clusterPoints){
            double dist = point.distance(p);
            if (dist < minDist && dist !=0) {
                minDist = dist;
                closestPoint = p;
            }
        }
        return closestPoint;
    }
    private static String getClosestPointBid(Business business) {
    	double x;
    	double y;
    	int cluster;
    	String closestBizId;
    	
		x = business.categorySimilarity;
		y = business.reviewSimilarity;
		
		Point bIdPoint = new Point(x,y);
		
		cluster = 0;
		for (Point point : points) {
			if (x == point.x && y == point.y) {
				cluster = point.clusterID;
			}
		}
		
		Point closestBizPoint = findClosestPointInCluster(bIdPoint, cluster, cluster_points);
		
		closestBizId = closestBizPoint.businessId;
		return closestBizId;
    }
    private static void populateGraph() {
        // Create graph with businesses.length nodes
        Graph graph = new Graph(businesses.length);

        // Add BIDs to nodes
        graph.addBIDsToNode(index_bId, businesses.length);

        // Add edges with weights
        for (Business business : businesses) {
            List<Pair<Business, Double>> neighbors = neighborMap.getNeighborList(business);
            if (neighbors != null) {
                for (Pair<Business, Double> neighbor : neighbors) {
                    double distance = neighbor.getValue();
                    graph.addEdge(bId_index.get(business.business_id), bId_index.get(neighbor.getKey().business_id), distance);
                }
            }
        }
        System.out.print("Number of disjoint sets: ");
        DisjointSets.printAnswer(10_000, graph.getAdjMatrix());
        //******************************************************************************************************************** CAN'T GET TO WORK WITH OTHER CLOSEST NODES
        // Create Dijkstra object and find shortest path between nodes 0 and itself
        D2 dijkstra = new D2(graph);
        List<Node> path = dijkstra.shortestPath(0, bId_index.get("npyH-DyEMLhDMIqEedmB-g"));
        
        // Create Dijkstra object and find shortest path between node and cluster center
        //
        // TODO
        //

        // Print path
        System.out.print("Shortest path: ");
        for (Node node : path) {
            System.out.print(node.getId() + ":(" + node.getBID() + ") -> ");
        }
        System.out.println();
        //********************************************************************************************************************* GRAPH IS EMPTY
        double[][] adjacencyMatrix = graph.getAdjMatrix();
        /*
        // Display Adjacency Matrix For Testing
        for (int i = 0; i < adjacencyMatrix.length; i++) {
            for (int j = 0; j < adjacencyMatrix[i].length; j++) {
            	if (adjacencyMatrix[i][j] != 0.0)
                System.out.print(adjacencyMatrix[i][j] + " ");
            }
            System.out.println();
        }
        */
        
        Point[] pointArray = points.toArray(new Point[points.size()]);
        
        // Display Point Array For Testing
        for (int i = 0; i < pointArray.length; i++) {
        	System.out.println(pointArray[i]);
        }
        
        // Create and show the graph display window
        GraphDisplay graphDisplay = new GraphDisplay(pointArray, adjacencyMatrix);
    }
}