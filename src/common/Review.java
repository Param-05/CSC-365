package common;

public final class Review {		
	// Object Variables
	public String review_id;
	public String business_id;
	public String text;
	
	//*********************************************
	// Getters and setters
	public String getReviewId() {
		return review_id;
	}
	public void setReviewId(String review_id) {
		this.review_id = review_id;
	}
	public String getBusinessId() {
		return business_id;
	}
	public void setBusinessId(String business_id) {
		this.business_id = business_id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}