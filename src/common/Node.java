package common;

public class Node {
    private int id;
    private String bID;

    public Node(int id) {
        this.id = id;
    }

    public void addBID(String bID) {
        this.bID = bID;
    }

    public int getId() {
        return id;
    }

    public String getBID() {
        return bID;
    }

    @Override
    public String toString() {
        return bID;
    }
}