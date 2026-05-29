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
        int hashVal = 0;
        for (int i = 0; i < key.length(); i++) {
            hashVal = (hashVal * 7) + key.charAt(i);
        }

        return (hashVal & 0x7FFFFFFF) % capacity;
    }

    // Secondary hash function - must never return 0
    private int hash2(String key) {
        int step = 67 - (key.hashCode() % 67);

        return (Math.abs(step));
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
}