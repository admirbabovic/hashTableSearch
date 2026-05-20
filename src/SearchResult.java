public class SearchResult {
    private int probes, testDuration;
    private long testDurationMs;

    public SearchResult () {
        this.probes = 0;
    }
    public SearchResult getLPSearchResults (CloudInfrastructure[] servers, LinearProbingHashTable table) {
        long starTime = System.currentTimeMillis();

        for (CloudInfrastructure server : servers){
            this.probes =+ table.search(server.getInstanceID());
        }

        this.testDurationMs = (System.currentTimeMillis() - starTime) * 1000;
        this.testDuration = (int) testDurationMs;

        return this;
    }

    public int getProbes () {
        return this.probes;
    }

    public int getTestDuration () {
        return this.testDuration;
    }
}