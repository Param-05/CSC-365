package common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PersistentHashtable implements Serializable {
    private static final long serialVersionUID = 1L;
    private static String hashTableFilePath = "hashtable.ser";
    public Map<String, String> hashtable;
    private Map<String, String> cache;
    private List<Long> blockOffsets;

    public PersistentHashtable() {
        hashtable = new HashMap<String, String>();
        cache = new LinkedHashMap<>(16, 0.75f, true);
        blockOffsets = new ArrayList<>();
        }

    public void put(String businessName, String fileName) {
        hashtable.put(businessName, fileName);
        cache.put(businessName, fileName);
    }

    public String get(String businessName) {
        // check if the key is in the cache
        if (cache.containsKey(businessName)) {
            // update the position of the key as the most recently used
            String fileName = cache.remove(businessName);
            cache.put(businessName, fileName);
            return fileName;
        }

        // check if the key is in the hashtable
        if (hashtable.containsKey(businessName)) {
            // add the key-value pair to the cache and return the value
            String fileName = hashtable.get(businessName);
            cache.put(businessName, fileName);
            // check if the cache has exceeded its capacity and remove the least recently used key
            if (cache.size() > 16) {
                Iterator<Map.Entry<String, String>> it = cache.entrySet().iterator();
                it.next();
                it.remove();
            }
            return fileName;
        }

        // key not found in hashtable
        return null;
    }
    public void save() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(hashTableFilePath, "rw")) {
            blockOffsets.clear();
            cache.forEach((key, value) -> {
                try {
                    byte[] keyBytes = key.getBytes();
                    byte[] valueBytes = value.getBytes();
                    int keyLength = keyBytes.length;
                    int valueLength = valueBytes.length;
                    int totalLength = keyLength + valueLength + 8;
                    long blockOffset = file.getFilePointer();
                    blockOffsets.add(blockOffset);
                    file.writeInt(totalLength);
                    file.writeInt(keyLength);
                    file.write(keyBytes);
                    file.writeInt(valueLength);
                    file.write(valueBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(hashTableFilePath + ".index"))) {
                out.writeObject(blockOffsets);
            }
        }
    }
    public void load() throws IOException, ClassNotFoundException {
        try (RandomAccessFile file = new RandomAccessFile(hashTableFilePath, "r")) {
            cache.clear();
            blockOffsets.clear();
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(hashTableFilePath + ".index"))) {
                blockOffsets = (List<Long>) in.readObject();
            }
            for (Long offset : blockOffsets) {
	            file.seek(offset);
	            int totalLength = file.readInt();
	            int keyLength = file.readInt();
	            byte[] keyBytes = new byte[keyLength];
	            file.read(keyBytes);
	            int valueLength = file.readInt();
	            byte[] valueBytes = new byte[valueLength];
	            file.read(valueBytes);
	            String key = new String(keyBytes);
	            String value = new String(valueBytes);
	            cache.put(key, value);
	            hashtable.put(key, value);
            }
        }
    }
}