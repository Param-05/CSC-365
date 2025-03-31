package common;

import java.io.*;
import java.util.*;

public class NeighborMap implements Serializable {
    private static String filename = "neighbors.ser";
    private HashMap<String, List<Pair<Business, Double>>> neighborMap;

    public NeighborMap() {
        neighborMap = new HashMap<>();
    }

    public void addNeighbor(String bId, Pair<Business, Double> pair) {
        if (!neighborMap.containsKey(bId)) {
            neighborMap.put(bId, new ArrayList<>());
        }
        neighborMap.get(bId).add(pair);
    }

    public List<Pair<Business, Double>> getNeighborList(Business business) {
        return neighborMap.get(business.business_id);
    }

    public void saveMap() throws IOException {
        FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(neighborMap);
        oos.close();
        fos.close();
    }

    public void loadMap() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        neighborMap = (HashMap<String, List<Pair<Business, Double>>>) ois.readObject();
        ois.close();
        fis.close();
    }
}