import java.util.Random;

public class Main {
    // Prime numbers for testing: 10009, 100019, 1000003, 2000003, 3000017, 5000011
    private static final int SERVER_COUNT = 1000003;
    private static final int INITIAL_CAPACITY = SERVER_COUNT;

    public static void main(String[] args) {
        DataGenerator generator = new DataGenerator();
        CloudInfrastructure[] datacenter = generator.generateRandomData(SERVER_COUNT);
        Random random = new Random();

        double[] percentages = {0.05, 0.10, 0.15, 0.20, 0.25, 0.30, 0.35, 0.40, 0.45,
                                0.50, 0.55, 0.60, 0.65, 0.70, 0.75, 0.80, 0.85, 0.90,
                                0.91, 0.92, 0.93, 0.94, 0.95, 0.96, 0.97, 0.98, 0.99};

        int[] INSERT_SIZE = new int[percentages.length];

        for (int i = 0; i < percentages.length; i++) {
            INSERT_SIZE[i] = (int) (INITIAL_CAPACITY * percentages[i]);
        }

        Benchmarks.linearProbing(datacenter, random, INITIAL_CAPACITY, INSERT_SIZE, SERVER_COUNT);
        Benchmarks.doubleHashing(datacenter, random, INITIAL_CAPACITY, INSERT_SIZE, SERVER_COUNT);
        Benchmarks.benchmarkBFS(datacenter, random, INITIAL_CAPACITY, INSERT_SIZE, SERVER_COUNT);
        Benchmarks.printResultsTable();
    }

}