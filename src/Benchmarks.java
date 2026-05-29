import java.util.Random;
import java.util.Locale;

public class Benchmarks {

    private static double[] lpInsert;
    private static double[] lpSearch;
    private static double[] dhInsert;
    private static double[] dhSearch;
    private static double[] bfsInsert;
    private static double[] bfsSearch;
    private static int[] loadPercents;
    private static final int TOTAL_RUNS = 5;
    private static final int DROP_FIRST = 2;

    public static void linearProbing(CloudInfrastructure[] datacenter, Random random, int INITIAL_CAPACITY, int[] INSERT_SIZE, int SERVER_COUNT) {
        System.out.println(">>> [1/3] Starting Linear Probing benchmark...");

        lpInsert = new double[INSERT_SIZE.length];
        lpSearch = new double[INSERT_SIZE.length];
        loadPercents = new int[INSERT_SIZE.length];

        LinearProbingHashTable warmup = new LinearProbingHashTable(INITIAL_CAPACITY);
        for (int i = 0; i < 5000; i++) warmup.insert(datacenter[i]);
        for (int i = 0; i < 5000; i++) warmup.searchByIp(datacenter[random.nextInt(5000)].getIpAddress());

        for (int index = 0; index < INSERT_SIZE.length; index++) {
            int load = INSERT_SIZE[index];
            loadPercents[index] = (int) Math.ceil(((double) load / INITIAL_CAPACITY) * 100);

            LinearProbingHashTable lpTable = new LinearProbingHashTable(INITIAL_CAPACITY);

            long insertStart = System.nanoTime();
            for (int i = 0; i < load; i++) lpTable.insert(datacenter[i]);
            lpInsert[index] = (System.nanoTime() - insertStart) / (double) 1000000;
            
            double[] runResults = new double[TOTAL_RUNS];

            int searchRounds = Math.max(load / 10, 10000);
            int hitRounds = searchRounds / 2;
            int missRounds = searchRounds / 2;

            for (int run = 0; run < TOTAL_RUNS; run++) {
                long totalSearchTime = 0;
                for (int i = 0; i < hitRounds; i++) {
                    String ip = datacenter[random.nextInt(load)].getIpAddress();
                    long s = System.nanoTime(); lpTable.searchByIp(ip);
                    totalSearchTime += System.nanoTime() - s;
                }
                for (int i = 0; i < missRounds; i++) {
                    String ip = datacenter[load + random.nextInt(SERVER_COUNT - load)].getIpAddress();
                    long s = System.nanoTime(); lpTable.searchByIp(ip);
                    totalSearchTime += System.nanoTime() - s;
                }
                runResults[run] = totalSearchTime / (double) searchRounds;
            }

            double sum = 0;
            for (int i = DROP_FIRST; i < TOTAL_RUNS; i++) sum += runResults[i];
            lpSearch[index] = sum / (TOTAL_RUNS - DROP_FIRST);
        }

        System.out.println("    >>> [1/3] Linear Probing benchmark complete.\n");
    }

    public static void doubleHashing(CloudInfrastructure[] datacenter, Random random, int INITIAL_CAPACITY, int[] INSERT_SIZE, int SERVER_COUNT) {
        System.out.println(">>> [2/3] Starting Double Hashing benchmark...");

        dhInsert = new double[INSERT_SIZE.length];
        dhSearch = new double[INSERT_SIZE.length];

        DoubleHashingTable warmup = new DoubleHashingTable(INITIAL_CAPACITY);
        for (int i = 0; i < 5000; i++) warmup.insert(datacenter[i]);
        for (int i = 0; i < 5000; i++) warmup.searchByIp(datacenter[random.nextInt(5000)].getIpAddress());

        for (int index = 0; index < INSERT_SIZE.length; index++) {
            int load = INSERT_SIZE[index];

            DoubleHashingTable dhTable = new DoubleHashingTable(INITIAL_CAPACITY);

            long insertStart = System.nanoTime();
            for (int i = 0; i < load; i++) dhTable.insert(datacenter[i]);
            dhInsert[index] = (System.nanoTime() - insertStart) / (double) 1000000;
            
            double[] runResults = new double[TOTAL_RUNS];

            int searchRounds = Math.max(load / 10, 10000);
            int hitRounds = searchRounds / 2;
            int missRounds = searchRounds / 2;

            for (int run = 0; run < TOTAL_RUNS; run++) {
                long totalSearchTime = 0;
                for (int i = 0; i < hitRounds; i++) {
                    String ip = datacenter[random.nextInt(load)].getIpAddress();
                    long s = System.nanoTime(); dhTable.searchByIp(ip);
                    totalSearchTime += System.nanoTime() - s;
                }
                for (int i = 0; i < missRounds; i++) {
                    String ip = datacenter[load + random.nextInt(SERVER_COUNT - load)].getIpAddress();
                    long s = System.nanoTime(); dhTable.searchByIp(ip);
                    totalSearchTime += System.nanoTime() - s;
                }
                runResults[run] = totalSearchTime / (double) searchRounds;
            }

            double sum = 0;
            for (int i = DROP_FIRST; i < TOTAL_RUNS; i++) sum += runResults[i];
            dhSearch[index] = sum / (TOTAL_RUNS - DROP_FIRST);
        }

        System.out.println("    >>> [2/3] Double Hashing benchmark complete.\n");
    }

    public static void benchmarkBFS(CloudInfrastructure[] datacenter, Random random, int INITIAL_CAPACITY, int[] INSERT_SIZE, int SERVER_COUNT) {
        System.out.println(">>> [3/3] Starting Linked BFS benchmark...");

        bfsInsert = new double[INSERT_SIZE.length];
        bfsSearch = new double[INSERT_SIZE.length];

        LinkedBFSHashTable warmup = new LinkedBFSHashTable(INITIAL_CAPACITY);
        for (int i = 0; i < 5000; i++) warmup.insert(datacenter[i]);
        warmup.buildBridges();
        for (int i = 0; i < 5000; i++) warmup.searchByIp(datacenter[random.nextInt(5000)].getIpAddress());

        for (int index = 0; index < INSERT_SIZE.length; index++) {
            int load = INSERT_SIZE[index];

            LinkedBFSHashTable bfsTable = new LinkedBFSHashTable(INITIAL_CAPACITY);

            long insertStart = System.nanoTime();
            for (int i = 0; i < load; i++) bfsTable.insert(datacenter[i]);
            bfsTable.buildBridges();
            bfsInsert[index] = (System.nanoTime() - insertStart) / (double) 1000000;
            
            double[] runResults = new double[TOTAL_RUNS];

            int searchRounds = Math.max(load / 10, 10000);
            int hitRounds = searchRounds / 2;
            int missRounds = searchRounds / 2;

            for (int run = 0; run < TOTAL_RUNS; run++) {
                long totalSearchTime = 0;
                for (int i = 0; i < hitRounds; i++) {
                    String ip = datacenter[random.nextInt(load)].getIpAddress();
                    long s = System.nanoTime(); bfsTable.searchByIp(ip);
                    totalSearchTime += System.nanoTime() - s;
                }
                for (int i = 0; i < missRounds; i++) {
                    String ip = datacenter[load + random.nextInt(SERVER_COUNT - load)].getIpAddress();
                    long s = System.nanoTime(); bfsTable.searchByIp(ip);
                    totalSearchTime += System.nanoTime() - s;
                }
                runResults[run] = totalSearchTime / (double) searchRounds;
            }

            double sum = 0;
            for (int i = DROP_FIRST; i < TOTAL_RUNS; i++) sum += runResults[i];
            bfsSearch[index] = sum / (TOTAL_RUNS - DROP_FIRST);
        }

        System.out.println("    >>> [3/3] Linked BFS benchmark complete.\n");
    }

    public static void printResultsTable() {
        final int WL = 6;
        final int WV = 13;

        int algoW  = WV + 3 + WV;

        int totalW = WL + (3 + algoW) * 3;
        String sep = "─".repeat(totalW);

        // algorithm names
        System.out.printf("%-" + WL + "s │ %-" + algoW + "s │ %-" + algoW + "s │ %-" + algoW + "s%n", "Load",
                center("Linear Probing", algoW),
                center("Double Hashing", algoW),
                center("Linked BFS", algoW));

        // subcolumn names
        String algoSub = String.format("%-" + WV + "s │ %-" + WV + "s", "Insert (ms)", "Search (ns)");
        System.out.printf("%-" + WL + "s │ %s │ %s │ %s%n", "", algoSub, algoSub, algoSub);

        System.out.println(sep);

        // data rows
        for (int i = 0; i < loadPercents.length; i++) {
            double liIns = lpInsert[i], liSrch = lpSearch[i];
            double dhIns = dhInsert[i], dhSrch = dhSearch[i];
            double bIns = bfsInsert[i], bSrch  = bfsSearch[i];

            System.out.printf(
                    "%-" + WL + "s │ %-" + WV + "s │ %-" + WV + "s │ %-" + WV + "s │ %-" + WV + "s │ %-" + WV + "s │ %-" + WV + "s%n", loadPercents[i] + "%",
                    fmt(liIns), fmt(liSrch),
                    fmt(dhIns), fmt(dhSrch),
                    fmt(bIns),  fmt(bSrch));
        }

        System.out.println(sep);
    }

    private static String fmt(double v) {
        return String.format(Locale.US, "%,.4f", v);
    }

    private static String center(String s, int width) {
        if (s.length() >= width) return s;
        int pad = width - s.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }
}