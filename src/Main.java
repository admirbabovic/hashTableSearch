import java.util.Random;

public class Main {

    private static final int SERVER_COUNT = 1000003;
    private static final int INITIAL_CAPACITY = 1000003;

    public static void main(String[] args) {
        DataGenerator generator = new DataGenerator();
        CloudInfrastructure[] datacenter = generator.generateRandomData(SERVER_COUNT);
        Random random = new Random();

        double[] percentages = {0.10, 0.20, 0.40, 0.70, 0.90, 0.95, 0.99};

        int[] INSERT_SIZE = new int[percentages.length];

        for (int i = 0; i < percentages.length; i++) {
            INSERT_SIZE[i] = (int) (INITIAL_CAPACITY * percentages[i]);
        }

        System.out.println("===== LINEAR PROBING =====");
        Benchmarks.linearProbing(datacenter, random, INITIAL_CAPACITY, INSERT_SIZE, SERVER_COUNT);

        System.out.println("===== DOUBLE HASHING =====");
        Benchmarks.doubleHashing(datacenter, random, INITIAL_CAPACITY, INSERT_SIZE, SERVER_COUNT);

        System.out.println("===== LINKED BFS =====");
        Benchmarks.benchmarkBFS(datacenter, random, INITIAL_CAPACITY, INSERT_SIZE, SERVER_COUNT);
    }

}