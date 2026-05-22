import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DoubleHashingTable {

    private CloudInfrastructure[] table;
    private int capacity;
    private int size;

    public DoubleHashingTable(int capacity) {
        this.capacity = capacity;
        this.table = new CloudInfrastructure[capacity];
        this.size = 0;
    }

    // Primary hash function
    private int hash1(String key) {
        try {
            // Cryptographic hash to slow down the computation
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

    // Secondary hash function - must never return 0
    private int hash2(String key) {
        int hashKey = 0;
        for (char c : key.toCharArray()) {
            hashKey = 67 * hashKey + c;
        }
        return Math.abs(hashKey % (capacity - 1)) + 1;
    }

    public boolean insert(CloudInfrastructure server) {
        if ((double) size / capacity > 0.99) return false;

        String ip = server.getIpAddress();
        int h1 = hash1(ip);
        int step = hash2(ip);
        int index = h1;
        int probed = 0;

        while (table[index] != null && probed < capacity) {
            if (table[index].getIpAddress().equals(ip)) {
                table[index] = server;
                return true;
            }
            probed++;
            index = Math.abs((h1 + probed * step) % capacity);

        }

        table[index] = server;
        size++;
        return true;
    }

    public CloudInfrastructure searchByIp(String ipAddress) {
        int h1 = hash1(ipAddress);
        int step = hash2(ipAddress);
        int index = h1;
        int probed = 0;

        while (table[index] != null && probed < capacity) {
            if (table[index].getIpAddress().equals(ipAddress)) return table[index];
            probed++;
            index = Math.abs((h1 + probed * step) % capacity);
        }
        return null;
    }

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