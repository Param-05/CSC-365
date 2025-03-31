package common;

public class WordFrequencyTable implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
	//*********************************************
	// Node Class
	static final class Node {
		String key;
		Node next;
		int count;
		Node(String k, int c, Node n) { key = k; count = c; next = n;}
	}
	
	//*********************************************
	// Instantiate Node Array
	static Node[] table = new Node[8]; // Always a power of 2
	static int size = 0;
	
	//*********************************************
	// GetCount Function
	public int getCount(String key) {
		int h = key.hashCode();
		int i = h & (table.length - 1);						// i = bitwise AND on h using table.length-1 (like a bit-mask, to return only the low-order bits of h. Basically a super-fast variant of h % table.length)
		for (Node e = table[i]; e != null; e = e.next) {
			if (key.equals(e.key)) {
				return e.count;
			}
		}
	return 0;
	}
	
	//*********************************************
	// Add Function
	public void add(String key) {
		int h = key.hashCode();
		int i = h & (table.length - 1);
		for (Node e = table[i]; e != null; e = e.next) {
			if (key.equals(e.key)) {
				++e.count;
				return;
			}
		}
		table[i] = new Node(key, 1, table[i]);
		++size;
		if ((float)size/table.length >= 0.75f) {
			resize();
			//System.out.println("Table resized to size : " + table.length);
		}
	}
	
	//*********************************************
	// Resize Function
	public void resize() {
		Node[] oldTable = table;
		int oldCapacity = oldTable.length;
		int newCapacity = oldCapacity << 1;
		Node[] newTable = new Node[newCapacity];
		for (int i = 0; i < oldCapacity; ++i) {
			for (Node e = oldTable[i]; e != null; e = e.next) {
				int h = e.key.hashCode();
				int j = h & (newTable.length - 1);
				newTable[j] = new Node(e.key, e.count, newTable[j]);
			}
		}
		table = newTable;			
	}
	
	//*********************************************
	// Remove Function
	public void remove(String key) {
		int h = key.hashCode();
		int i = h & (table.length - 1);
		Node e = table[i], p = null;
		while (e != null) {
			if (key.equals(e.key)) {
				if (p == null) {
					table[i] = e.next;
				}
				else {
					p.next = e.next;
				}
				break;
			}
			p = e;
			e = e.next;
		}
	}
	
	//*********************************************
	// PrintAll Function
	public void PrintAll() {
		for (int i = 0; i < table.length; ++i)
			for (Node e = table[i]; e != null; e = e.next) {
				System.out.println("Key : " + e.key + " --- Count : " + e.count);
			}
	}
}