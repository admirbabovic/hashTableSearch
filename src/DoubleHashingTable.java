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
    private int h1(String key) {
        int hashKey = 0;
        for (char c : key.toCharArray()) {
            hashKey = 31 * hashKey + c;
        }
        return Math.abs(hashKey % capacity);
    }

    // Secondary hash function - must never return 0
    private int h2(String key) {
        int hashKey = 0;
        for (char c : key.toCharArray()) {
            hashKey = 67 * hashKey + c;
        }
        return Math.abs(hashKey % (capacity - 1)) + 1;
    }

    public boolean insert(CloudInfrastructure server) {
        if ((double) size / capacity > 0.95) return false;

        String ip = server.getIpAddress();
        int index = h1(ip);
        int step = h2(ip);
        int probed = 0;

        while (table[index] != null && probed < capacity) {
            if (table[index].getIpAddress().equals(ip)) {
                table[index] = server;
                return true;
            }
            probed++;
            index = Math.abs((h1(ip) + probed * step) % capacity);

        }

        table[index] = server;
        size++;
        return true;
    }

    public CloudInfrastructure searchByIp(String ipAddress) {
        int index = h1(ipAddress);
        int step = h2(ipAddress);
        int probed = 0;

        while (table[index] != null && probed < capacity) {
            if (table[index].getIpAddress().equals(ipAddress)) return table[index];
            probed++;
            index = Math.abs((h1(ipAddress) + probed * step) % capacity);
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

    public int getSize()     { return size; }
    public int getCapacity() { return capacity; }
}