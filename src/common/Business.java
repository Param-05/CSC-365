package common;

import java.io.Serializable;
import java.util.ArrayList;

public final class Business implements Serializable {
	private static final long serialVersionUID = 1L;
		// Object Variables
		public String business_id;
		public String name;
		public String address;
		public String city;
		public String state;
		public int postalCode;
		public double latitude;
		public double longitude;
		public double stars;
		public int reviewCount;
		public int isOpen;
		public String categories;
		public ArrayList<String> reviewIds;
		public ArrayList<ArrayList<String>> reviews;
		public double categorySimilarity;
		public double reviewSimilarity;
		public int cluster;
		
		public String getId() {
			return business_id;
		}
		public void setId(String business_id) {
			this.business_id = business_id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public int getPostalCode() {
			return postalCode;
		}
		public void setPostalCode(int postalCode) {
			this.postalCode = postalCode;
		}
		public double getLatitude() {
			return latitude;
		}
		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}
		public double getLongitude() {
			return longitude;
		}
		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}
		public double getStars() {
			return stars;
		}
		public void setStars(double stars) {
			this.stars = stars;
		}
		public int getReviewCount() {
			return reviewCount;
		}
		public void setReviewCount(int reviewCount) {
			this.reviewCount = reviewCount;
		}
		public int getIsOpen() {
			return isOpen;
		}
		public void setIsOpen(int isOpen) {
			this.isOpen = isOpen;
		}
		public String getCategories() {
			return categories;
		}
		public void setCategories(String categories) {
			this.categories = categories;
		}
		public ArrayList<String> getReviewIds() {
			return reviewIds;
		}
		public void setReviewIds(ArrayList<String> reviewIds) {
			this.reviewIds = reviewIds;
		}
		public ArrayList<ArrayList<String>> getReviews() {
			return reviews;
		}
		public void setReviews(ArrayList<ArrayList<String>> reviews) {
			this.reviews = reviews;
		}
		public double getCategorySimilarity() {
			return categorySimilarity;
		}
		public void setCategorySimilarity(double categorySimilarity) {
			this.categorySimilarity = categorySimilarity;
		}
		public double getReviewSimilarity() {
			return reviewSimilarity;
		}
		public void setReviewSimilarity(double reviewSimilarity) {
			this.reviewSimilarity = reviewSimilarity;
		}
		public int getCluster() {
			return cluster;
		}
		public void setCluster(int cluster) {
			this.cluster = cluster;
		}
	}