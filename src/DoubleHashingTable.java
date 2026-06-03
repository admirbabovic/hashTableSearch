public class DoubleHashingTable {

    private CloudInfrastructure[] table;
    private int capacity;
    private int size, insCol, srcCol;

    public DoubleHashingTable(int capacity) {
        this.capacity = capacity;
        this.table = new CloudInfrastructure[capacity];
        this.size = 0;
        this.insCol = 0;
        this.srcCol = 0;
    }

    // Primary hash function
    private int hash1(int key) {
        return (key & 0x7FFFFFFF) % capacity;
    }

    // Secondary hash function - must never return 0
    private int hash2(int key) {
        int step = 67 - (key % 67);

        return (Math.abs(step));
    }

    public boolean insert(CloudInfrastructure server) {
        if ((double) size / capacity > 0.99) return false;

        String ip = server.getIpAddress();
        int h1 = hash1(server.getInstanceID());
        int step = hash2(h1);
        int index = h1;
        int probed = 0;

        while (table[index] != null && probed < capacity) {
            if (table[index].getIpAddress().equals(ip)) {
                table[index] = server;
                return true;
            }
            insCol++;
            probed++;
            index = Math.abs((h1 + probed * step) % capacity);

        }

        table[index] = server;
        size++;
        return true;
    }

    public CloudInfrastructure searchByIp(CloudInfrastructure server) {
        int h1 = hash1(server.getInstanceID());
        int step = hash2(h1);
        int index = h1;
        int probed = 0;

        while (table[index] != null && probed < capacity) {
            if (table[index].getIpAddress().equals(server.getIpAddress())) return table[index];
            probed++;
            srcCol++;
            index = Math.abs((h1 + probed * step) % capacity);
        }
        return null;
    }

    public int getInsCol() {
        return this.insCol;
    }

    public void setInsCol() {
        this.insCol = 0;
    }

    public int getSrcCol() {
        return this.srcCol;
    }

    public void setSrcCol() {
        this.srcCol = 0;
    }
}