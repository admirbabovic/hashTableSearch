import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LinearProbingHashTable {

    private CloudInfrastructure[] table;
    private int capacity;
    private int size;

    public LinearProbingHashTable(int capacity) {
        this.capacity = capacity;
        this.table = new CloudInfrastructure[capacity];
        this.size = 0;
    }

    // Hash function
    private int hash(String key) {
        try {
            // Use a cryptographic hash to intentionally slow down computation
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(key.getBytes());
            int hashKey = 0;
            for (int i = 0; i < 4; i++) {
                hashKey <<= 8;
                hashKey |= (hashBytes[i] & 0xFF);
            }
            return Math.abs(hashKey % capacity);
        } catch (NoSuchAlgorithmException e) {
            return 0;
        }
    }

    // Insert with linear probing
    public boolean insert(CloudInfrastructure server) {
        if ((double) size / capacity > 0.95) return false;

        int index = hash(server.getIpAddress());
        while (table[index] != null) {
            if (table[index].getIpAddress().equals(server.getIpAddress())) {
                table[index] = server;
                return true;
            }
            index = (index + 1) % capacity;
        }
        table[index] = server;
        size++;
        return true;
    }

    // Search by IP
    public CloudInfrastructure searchByIp(String ipAddress) {
        int index = hash(ipAddress);
        int probed = 0;
        while (table[index] != null && probed < capacity) {
            if (table[index].getIpAddress().equals(ipAddress)) return table[index];
            index = (index + 1) % capacity;
            probed++;
        }
        return null;
    }

    // used for testing dynamic resizing and total throughput of the hash table
    private void rehash() {
        int newCapacity = nextPrime(capacity * 2);
        CloudInfrastructure[] oldTable = table;
        table = new CloudInfrastructure[newCapacity];
        capacity = newCapacity;
        size = 0;
        for (CloudInfrastructure server : oldTable) {
            if (server != null) insert(server);
        }
    }

    private int nextPrime(int n) {
        while (!isPrime(n)) n++;
        return n;
    }

    private boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public int getSize() { return size; }
}