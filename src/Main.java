public class Main {

    public static void main (String[] args) {
        DataGenerator smallCluster = new DataGenerator();

        int serverCount = 10000;
        int tableCapacity = 13007;

        CloudInfrastructure[] smallDatacenter = smallCluster.generateRandomData(serverCount);

        LinearProbingHashTable lpHashTable = new LinearProbingHashTable(tableCapacity);

        for (CloudInfrastructure server : smallDatacenter){
            lpHashTable.insert(server);
        }

        SearchResult linearProbingResults = new SearchResult();
        linearProbingResults.getLPSearchResults(smallDatacenter, lpHashTable);
        System.out.println("Linear Probing: Found with " + linearProbingResults.getProbes() + " probes in " + linearProbingResults.getTestDuration() + "s.");

    }
}
