package common;

public class Haversine {
    // Haversine formula to calculate distance between two coordinates in kilometers
    public static double haversine(Business business1, Business business2) {
        final int EARTH_RADIUS_KM = 6371;

        double lat1 = Math.toRadians(business1.getLatitude());
        double lat2 = Math.toRadians(business2.getLatitude());
        double lon1 = Math.toRadians(business1.getLongitude());
        double lon2 = Math.toRadians(business2.getLongitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.pow(Math.sin(dLat/2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dLon/2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return EARTH_RADIUS_KM * c;
    }
}
