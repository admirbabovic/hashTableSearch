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
        int hashVal = 0;
        for (int i = 0; i < key.length(); i++) {
            hashVal = (hashVal * 7) + key.charAt(i);
        }

        return (hashVal & 0x7FFFFFFF) % capacity;
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
}