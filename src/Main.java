import java.util.Scanner;
import java.util.Random;

public class Main {

    private static final int SERVER_COUNT = 130000;
    private static final int INITIAL_CAPACITY = 130007;
    private static int[] INSERT_SIZE;

    public static void main(String[] args) {
        DataGenerator generator = new DataGenerator();
        CloudInfrastructure[] datacenter = generator.generateRandomData(SERVER_COUNT);
        Random random = new Random();

        double[] percentages = {0.10, 0.20, 0.40, 0.70, 0.90};
        INSERT_SIZE = new int[percentages.length];
        for (int i = 0; i < percentages.length; i++) {
            INSERT_SIZE[i] = (int) (INITIAL_CAPACITY * percentages[i]);
        }

        System.out.println("===== LINEAR PROBING =====");
        benchmarkLinear(datacenter, random);

        System.out.println("===== DOUBLE HASHING =====");
        benchmarkDoubleHashing(datacenter, random);

        System.out.println("===== LINKED BFS =====");
        benchmarkBFS(datacenter, random);
    }

    private static void benchmarkLinear(CloudInfrastructure[] datacenter, Random random) {
        LinearProbingHashTable warmup = new LinearProbingHashTable(INITIAL_CAPACITY);
        for (int i = 0; i < 5000; i++) warmup.insert(datacenter[i]);
        for (int i = 0; i < 5000; i++) warmup.searchByIp(datacenter[random.nextInt(5000)].getIpAddress());

        for (int load : INSERT_SIZE) {
            System.out.println("Benchmark: " + (int) Math.ceil(((double) load / INITIAL_CAPACITY) * 100) + "% table load");
            System.out.println("---");

            LinearProbingHashTable lpTable = new LinearProbingHashTable(INITIAL_CAPACITY);

            long insertStart = System.nanoTime();
            for (int i = 0; i < load; i++) lpTable.insert(datacenter[i]);
            double insertTime = (System.nanoTime() - insertStart) / 1_000_000.0;
            System.out.println("Insertion time: " + insertTime + " ms");
            System.out.println("Table size after insert: " + lpTable.getSize());

            int totalRuns = 5;
            int dropFirst = 2;
            double[] runResults = new double[totalRuns];

            int searchRounds = Math.max(load / 10, 10000);
            int hitRounds = searchRounds / 2;
            int missRounds = searchRounds / 2;

            for (int run = 0; run < totalRuns; run++) {
                long totalSearchTime = 0;

                // Hit searches - IP exists in table
                for (int i = 0; i < hitRounds; i++) {
                    String targetIp = datacenter[random.nextInt(load)].getIpAddress();
                    long searchStart = System.nanoTime();
                    lpTable.searchByIp(targetIp);
                    totalSearchTime += System.nanoTime() - searchStart;
                }

                // Miss searches - IP was never inserted
                for (int i = 0; i < missRounds; i++) {
                    String targetIp = datacenter[load + random.nextInt(SERVER_COUNT - load)].getIpAddress();
                    long searchStart = System.nanoTime();
                    lpTable.searchByIp(targetIp);
                    totalSearchTime += System.nanoTime() - searchStart;
                }

                runResults[run] = totalSearchTime / (double) searchRounds;
            }

            double sum = 0;
            for (int i = dropFirst; i < totalRuns; i++) sum += runResults[i];
            double avgSearchTime = sum / (totalRuns - dropFirst);

            System.out.println("Avg search time (last " + (totalRuns - dropFirst) + " of " + totalRuns + " runs): " + String.format("%.2f", avgSearchTime) + " ns");
            System.out.println("==============================");
        }
    }

    private static void benchmarkDoubleHashing(CloudInfrastructure[] datacenter, Random random) {
        DoubleHashingTable warmup = new DoubleHashingTable(INITIAL_CAPACITY);
        for (int i = 0; i < 5000; i++) warmup.insert(datacenter[i]);
        for (int i = 0; i < 5000; i++) warmup.searchByIp(datacenter[random.nextInt(5000)].getIpAddress());

        for (int load : INSERT_SIZE) {
            System.out.println("Benchmark: " + (int) Math.ceil(((double) load / INITIAL_CAPACITY) * 100) + "% table load");
            System.out.println("---");

            DoubleHashingTable dhTable = new DoubleHashingTable(INITIAL_CAPACITY);

            long insertStart = System.nanoTime();
            for (int i = 0; i < load; i++) dhTable.insert(datacenter[i]);
            double insertTime = (System.nanoTime() - insertStart) / 1_000_000.0;
            System.out.println("Insertion time: " + insertTime + " ms");
            System.out.println("Table size after insert: " + dhTable.getSize());

            int totalRuns = 5;
            int dropFirst = 2;
            double[] runResults = new double[totalRuns];

            int searchRounds = Math.max(load / 10, 10000);
            int hitRounds = searchRounds / 2;
            int missRounds = searchRounds / 2;

            for (int run = 0; run < totalRuns; run++) {
                long totalSearchTime = 0;

                // Hit searches - IP exists in table
                for (int i = 0; i < hitRounds; i++) {
                    String targetIp = datacenter[random.nextInt(load)].getIpAddress();
                    long searchStart = System.nanoTime();
                    dhTable.searchByIp(targetIp);
                    totalSearchTime += System.nanoTime() - searchStart;
                }

                // Miss searches - IP was never inserted
                for (int i = 0; i < missRounds; i++) {
                    String targetIp = datacenter[load + random.nextInt(SERVER_COUNT - load)].getIpAddress();
                    long searchStart = System.nanoTime();
                    dhTable.searchByIp(targetIp);
                    totalSearchTime += System.nanoTime() - searchStart;
                }

                runResults[run] = totalSearchTime / (double) searchRounds;
            }

            double sum = 0;
            for (int i = dropFirst; i < totalRuns; i++) sum += runResults[i];
            double avgSearchTime = sum / (totalRuns - dropFirst);

            System.out.println("Avg search time (last " + (totalRuns - dropFirst) + " of " + totalRuns + " runs): " + String.format("%.2f", avgSearchTime) + " ns");
            System.out.println("==============================");
        }
    }

    private static void benchmarkBFS(CloudInfrastructure[] datacenter, Random random) {
        // same structure, swap in LinkedBFSHashTable
    }
}