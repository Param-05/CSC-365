package common;

public class Point {
	public double x;
    public double y;
    public int clusterID;
    public String businessId;
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double distance(Point other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    public void addToCluster(int clusterID){
        this.clusterID = clusterID;
    }
    public void addBusinessId(String businessId){
        this.businessId = businessId;
    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}