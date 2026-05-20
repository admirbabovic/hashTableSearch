import java.util.Scanner;
import java.util.Random;

public class Main {

    private static final int SERVER_COUNT = 130000;
    private static final int INITIAL_CAPACITY = 130007;
    private static final int[] INSERT_COUNTS = {13000, 26000, 52000, 91000, 117000};

    public static void main(String[] args) {
        DataGenerator generator = new DataGenerator();
        CloudInfrastructure[] datacenter = generator.generateRandomData(SERVER_COUNT);
        Random random = new Random();

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

        for (int count : INSERT_COUNTS) {
            System.out.println("Benchmark: " + (int) Math.ceil(((double) count / INITIAL_CAPACITY) * 100) + "% table load");
            System.out.println("---");

            LinearProbingHashTable lpTable = new LinearProbingHashTable(INITIAL_CAPACITY);

            long insertStart = System.nanoTime();
            for (int i = 0; i < count; i++) lpTable.insert(datacenter[i]);
            double insertTime = (System.nanoTime() - insertStart) / 1_000_000.0;
            System.out.println("Insertion time: " + insertTime + " ms");
            System.out.println("Table size after insert: " + lpTable.getSize());

            int totalRuns = 5;
            int dropFirst = 2;
            double[] runResults = new double[totalRuns];

            int searchRounds = Math.max(count / 10, 10000);
            int hitRounds = searchRounds / 2;
            int missRounds = searchRounds / 2;

            for (int run = 0; run < totalRuns; run++) {
                long totalSearchTime = 0;

                // Hit searches - IP exists in table
                for (int i = 0; i < hitRounds; i++) {
                    String targetIp = datacenter[random.nextInt(count)].getIpAddress();
                    long searchStart = System.nanoTime();
                    lpTable.searchByIp(targetIp);
                    totalSearchTime += System.nanoTime() - searchStart;
                }

                // Miss searches - IP was never inserted
                for (int i = 0; i < missRounds; i++) {
                    String targetIp = datacenter[count + random.nextInt(SERVER_COUNT - count)].getIpAddress();
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

        for (int count : INSERT_COUNTS) {
            System.out.println("Benchmark: " + (int) Math.ceil(((double) count / INITIAL_CAPACITY) * 100) + "% table load");
            System.out.println("---");

            DoubleHashingTable dhTable = new DoubleHashingTable(INITIAL_CAPACITY);

            long insertStart = System.nanoTime();
            for (int i = 0; i < count; i++) dhTable.insert(datacenter[i]);
            double insertTime = (System.nanoTime() - insertStart) / 1_000_000.0;
            System.out.println("Insertion time: " + insertTime + " ms");
            System.out.println("Table size after insert: " + dhTable.getSize());

            int totalRuns = 5;
            int dropFirst = 2;
            double[] runResults = new double[totalRuns];

            int searchRounds = Math.max(count / 10, 10000);
            int hitRounds = searchRounds / 2;
            int missRounds = searchRounds / 2;

            for (int run = 0; run < totalRuns; run++) {
                long totalSearchTime = 0;

                // Hit searches - IP exists in table
                for (int i = 0; i < hitRounds; i++) {
                    String targetIp = datacenter[random.nextInt(count)].getIpAddress();
                    long searchStart = System.nanoTime();
                    dhTable.searchByIp(targetIp);
                    totalSearchTime += System.nanoTime() - searchStart;
                }

                // Miss searches - IP was never inserted
                for (int i = 0; i < missRounds; i++) {
                    String targetIp = datacenter[count + random.nextInt(SERVER_COUNT - count)].getIpAddress();
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