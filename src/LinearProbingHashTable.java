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
    private int hash(int instanceID) {
        return instanceID % capacity;
    }

    // Insert with linear probing
    public boolean insert(CloudInfrastructure server) {
        if (size >= capacity) {
            System.out.println("Hash table is full!");
            return false;
        }

        int instanceID = server.getInstanceID();
        int index = hash(instanceID);

        while (table[index] != null) {
            if (table[index].getInstanceID() == server.getInstanceID()) {
                table[index] = server; // update duplicate
                return true;
            }
            index = (index + 1) % capacity; // probe next slot
        }

        table[index] = server;
        size++;
        return true;
    }

    // Search by instanceID
    public int search(int instanceID) {
        int index = hash(instanceID);
        int probes = 0;

        for (probes = 0; probes < capacity; probes++) {
            if (table[index] == null) return probes; // not found, return how many probes it took
            if (table[index].getInstanceID() == instanceID) return probes + 1; // found, return probe count
            index = (index + 1) % capacity;
        }

        return probes; // full loop
    }

    public int getSize()     { return size; }
    public int getCapacity() { return capacity; }
}