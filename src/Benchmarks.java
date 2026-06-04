import java.util.Random;
import java.util.Locale;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Benchmarks {

    private static double[] lpInsert;
    private static double[] lpSearch;
    private static double[] dhInsert;
    private static double[] dhSearch;
    private static double[] bfsInsert;
    private static double[] bfsSearch;
    private static int[] loadPercents, lpInsertCollisions, lpSearchCollisions, dhInsertCollisions, dhSearchCollisions, bfsInsertCollisions, bfsSearchCollisions;
    private static final int TOTAL_RUNS = 5;
    private static final int DROP_FIRST = 2;

    public static void linearProbing(CloudInfrastructure[] datacenter, Random random, int INITIAL_CAPACITY, int[] INSERT_SIZE, int SERVER_COUNT) {
        System.out.println(">>> [1/3] Starting Linear Probing benchmark...");

        lpInsert = new double[INSERT_SIZE.length];
        lpSearch = new double[INSERT_SIZE.length];
        lpInsertCollisions = new int[INSERT_SIZE.length];
        lpSearchCollisions = new int[INSERT_SIZE.length];
        loadPercents = new int[INSERT_SIZE.length];

        LinearProbingHashTable warmup = new LinearProbingHashTable(INITIAL_CAPACITY);
        for (int i = 0; i < 5000; i++) warmup.insert(datacenter[i]);
        for (int i = 0; i < 5000; i++) warmup.searchByIp(datacenter[random.nextInt(5000)].getIpAddress());

        for (int index = 0; index < INSERT_SIZE.length; index++) {
            int load = INSERT_SIZE[index];
            loadPercents[index] = (int) Math.ceil(((double) load / INITIAL_CAPACITY) * 100);

            LinearProbingHashTable lpTable = new LinearProbingHashTable(INITIAL_CAPACITY);

            long insertStart = System.nanoTime();
            for (int i = 0; i < load; i++) {
                lpTable.insert(datacenter[i]);
            }
            lpInsert[index] = (System.nanoTime() - insertStart) / (double) 1000000;
            lpInsertCollisions[index] = lpTable.getInsCol();
            lpTable.setInsCol();
            
            double[] runResults = new double[TOTAL_RUNS];
            int[] runCollisions = new int[TOTAL_RUNS];

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
                runCollisions[run] = lpTable.getSrcCol();
                lpTable.setSrcCol();
            }

            int sumCollisions = 0;
            for (int i = DROP_FIRST; i < TOTAL_RUNS; i++) sumCollisions += runCollisions[i];
            lpSearchCollisions[index] = sumCollisions / (TOTAL_RUNS - DROP_FIRST);
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
        dhInsertCollisions = new int[INSERT_SIZE.length];
        dhSearchCollisions = new int[INSERT_SIZE.length];

        DoubleHashingTable warmup = new DoubleHashingTable(INITIAL_CAPACITY);
        for (int i = 0; i < 5000; i++) warmup.insert(datacenter[i]);
        for (int i = 0; i < 5000; i++) warmup.searchByIp(datacenter[random.nextInt(5000)].getIpAddress());

        for (int index = 0; index < INSERT_SIZE.length; index++) {
            int load = INSERT_SIZE[index];

            DoubleHashingTable dhTable = new DoubleHashingTable(INITIAL_CAPACITY);

            long insertStart = System.nanoTime();
            for (int i = 0; i < load; i++) {
                dhTable.insert(datacenter[i]);
            }
            dhInsert[index] = (System.nanoTime() - insertStart) / (double) 1000000;
            dhInsertCollisions[index] = dhTable.getInsCol();
            dhTable.setInsCol();

            double[] runResults = new double[TOTAL_RUNS];
            int[] runCollisions = new int[TOTAL_RUNS];

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
                runCollisions[run] = dhTable.getSrcCol();
                dhTable.setSrcCol();
            }

            int sumCollisions = 0;
            for (int i = DROP_FIRST; i < TOTAL_RUNS; i++) sumCollisions += runCollisions[i];
            dhSearchCollisions[index] = sumCollisions / (TOTAL_RUNS - DROP_FIRST);
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
        bfsInsertCollisions = new int[INSERT_SIZE.length];
        bfsSearchCollisions = new int[INSERT_SIZE.length];

        LinkedBFSHashTable warmup = new LinkedBFSHashTable(INITIAL_CAPACITY);
        for (int i = 0; i < 5000; i++) warmup.insert(datacenter[i]);
        warmup.buildBridges();
        for (int i = 0; i < 5000; i++) warmup.searchByIp(datacenter[random.nextInt(5000)].getIpAddress());

        for (int index = 0; index < INSERT_SIZE.length; index++) {
            int load = INSERT_SIZE[index];

            LinkedBFSHashTable bfsTable = new LinkedBFSHashTable(INITIAL_CAPACITY);

            long insertStart = System.nanoTime();
            for (int i = 0; i < load; i++) {
                bfsTable.insert(datacenter[i]);
            }
            bfsTable.buildBridges();
            bfsInsert[index] = (System.nanoTime() - insertStart) / (double) 1000000;
            bfsInsertCollisions[index] = bfsTable.getInsCol();
            bfsTable.setInsCol();
            
            double[] runResults = new double[TOTAL_RUNS];
            int[] runCollisions = new int[TOTAL_RUNS];

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
                runCollisions[run] = bfsTable.getSrcCol();
                bfsTable.setSrcCol();
            }

            int sumCollisions = 0;
            for (int i = DROP_FIRST; i < TOTAL_RUNS; i++) sumCollisions += runCollisions[i];
            bfsSearchCollisions[index] = sumCollisions / (TOTAL_RUNS - DROP_FIRST);
            double sum = 0;
            for (int i = DROP_FIRST; i < TOTAL_RUNS; i++) sum += runResults[i];
            bfsSearch[index] = sum / (TOTAL_RUNS - DROP_FIRST);
        }

        System.out.println("    >>> [3/3] Linked BFS benchmark complete.\n");
    }

    public static void printResultsTable() {
        final int WL = 6;
        final int WT = 13;
        final int WC = 19;

        // New width per algorithm: Time + " │ " + Cols + " │ " + Time + " │ " + Cols
        int algoW  = WT + 3 + WC + 3 + WT + 3 + WC;

        int totalW = WL + (3 + algoW) * 3;
        String sep = "─".repeat(totalW);

        // algorithm names
        System.out.printf("%-" + WL + "s ││ %-" + algoW + "s ││ %-" + algoW + "s ││ %-" + algoW + "s%n", "Load",
                center("Linear Probing", algoW),
                center("Double Hashing", algoW),
                center("Linked BFS", algoW));

        // Subcolumn names row (algoSub handles internal single pipes, printf handles the ││ before them)
        String algoSub = String.format("%-" + WT + "s │ %-" + WC + "s │ %-" + WT + "s │ %-" + WC + "s",
                "Insert (ms)", "Insert (collisions)", "Search (ns)", "Search (collisions)");

        System.out.printf("%-" + WL + "s ││ %s ││ %s ││ %s%n", "", algoSub, algoSub, algoSub);

        System.out.println(sep);

        // data rows
        for (int i = 0; i < loadPercents.length; i++) {
            // Time variables
            double liIns = lpInsert[i], liSrch = lpSearch[i];
            double dhIns = dhInsert[i], dhSrch = dhSearch[i];
            double bIns = bfsInsert[i], bSrch  = bfsSearch[i];

            // Collision variables (ints)
            int lpInsCol = lpInsertCollisions[i], lpSrchCols = lpSearchCollisions[i];
            int dhInsCol = dhInsertCollisions[i], dhSrchCols = dhSearchCollisions[i];
            int bfsInsCol = bfsInsertCollisions[i], bfsSrchCol = bfsSearchCollisions[i];

            System.out.printf(
                    "%-" + WL + "s ││ " +
                            "%-" + WT + "s │ %-" + WC + "s │ %-" + WT + "s │ %-" + WC + "s ││ " + // Linear Probing
                            "%-" + WT + "s │ %-" + WC + "s │ %-" + WT + "s │ %-" + WC + "s ││ " + // Double Hashing
                            "%-" + WT + "s │ %-" + WC + "s │ %-" + WT + "s │ %-" + WC + "s%n",   // Linked BFS
                    loadPercents[i] + "%",
                    fmt(liIns), fmtInt(lpInsCol), fmt(liSrch), fmtInt(lpSrchCols),
                    fmt(dhIns), fmtInt(dhInsCol), fmt(dhSrch), fmtInt(dhSrchCols),
                    fmt(bIns),  fmtInt(bfsInsCol),  fmt(bSrch),  fmtInt(bfsSrchCol)
            );
        }

        System.out.println(sep);
    }

    private static String fmt(double v) {
        return String.format(Locale.US, "%,.4f", v);
    }

    private static String fmtInt(int v) {
        return String.format(Locale.US, "%,d", v);
    }

    private static String center(String s, int width) {
        if (s.length() >= width) return s;
        int pad = width - s.length();
        int left = pad / 2;
        int right = pad - left;
        return " ".repeat(left) + s + " ".repeat(right);
    }

    public static void dataExport () {
        String csvFileName = "data.csv";

        try (PrintWriter writer = new PrintWriter(csvFileName)) {

            writer.println("Load_percent,LP_insert_ms,LP_insert_cols,LP_search_ns,LP_search_col," +
                    "DH_insert_ms,DH_insert_cols,DH_search_ns,DH_search_col," +
                    "BFS_insert_ms,BFS_insert_cols,BFS_search_ns,BFS_search_col");

            for (int i = 0; i < loadPercents.length; i++) {
                String row = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s",
                        loadPercents[i],
                        lpInsert[i],
                        lpInsertCollisions[i],
                        lpSearch[i],
                        lpSearchCollisions[i],
                        dhInsert[i],
                        dhInsertCollisions[i],
                        dhSearch[i],
                        dhSearchCollisions[i],
                        bfsInsert[i],
                        bfsInsertCollisions[i],
                        bfsSearch[i],
                        bfsSearchCollisions[i]
                        );

                writer.println(row);
            }

            System.out.println("CSV file created successfully at: " + new File(csvFileName).getAbsolutePath());

        } catch (FileNotFoundException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}