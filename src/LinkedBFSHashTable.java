public class LinkedBFSHashTable {

    private static class Node {
        CloudInfrastructure server;
        Node nextInChain;
        Node nextBucket;

        Node(CloudInfrastructure server) {
            this.server = server;
        }
    }

    private Node[] table;
    private int capacity;
    private int size;

    public LinkedBFSHashTable(int capacity) {
        this.capacity = capacity;
        this.table = new Node[capacity];
        this.size = 0;
    }

    private int hash(String key) {
        int hashVal = 0;
        for (int i = 0; i < key.length(); i++) {
            hashVal = (hashVal * 7) + key.charAt(i);
        }

        return (hashVal & 0x7FFFFFFF) % capacity;
    }

    public void insert(CloudInfrastructure server) {
        int index = hash(server.getIpAddress());

        Node current = table[index];
        while (current != null) {
            if (current.server.getIpAddress().equals(server.getIpAddress())) {
                current.server = server;
                return;
            }
            current = current.nextInChain;
        }

        Node newNode = new Node(server);
        newNode.nextInChain = table[index];
        table[index] = newNode;
        size++;
    }

    public void buildBridges() {
        int[] active = new int[capacity];
        int count = 0;
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null) active[count++] = i;
        }

        if (count == 0) return;

        for (int b = 0; b < count; b++) {
            int bucketIdx = active[b];
            int nextIdx = active[(b + 1) % count];  // ring wrap

            Node tail = table[bucketIdx];
            while (tail.nextInChain != null) tail = tail.nextInChain;

            tail.nextBucket = table[nextIdx];
        }
    }

    public CloudInfrastructure searchByIp(String ipAddress) {
        int index = hash(ipAddress);
        Node current = table[index];

        while (current != null) {
            if (current.server.getIpAddress().equals(ipAddress)) {
                return current.server;
            }
            current = current.nextInChain;
        }

        return null;
    }
}