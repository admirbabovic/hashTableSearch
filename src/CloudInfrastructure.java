public class CloudInfrastructure {

    private int instanceID;
    private String instanceType, region, ipAddress;
    double cpuUtilization, ramUsageGB;
    long uptimeSeconds, creationTimestamp;
    private final String[] availableRegions = {
        "na-east", "na-west", "na-northeast", "na-northwest",
        "eu-east", "eu-west", "eu-central", "eu-south", "eu-north",
        "ap-east", "ap-south", "ap-southeast", "ap-northeast",
        "af-south", "sa-east"};

    private final String[] availableInstanceTypes = {
        "g1i.small", "g1i.medium", "g1i.large", "g1i.xlarge",
        "g2i.small", "g2i.medium", "g2i.large", "g2i.xlarge",
        "g3i.small", "g3i.medium", "g3i.large", "g3i.xlarge",
        "g1a.small", "g1a.medium", "g1a.large", "g1a.xlarge",
        "g2a.small", "g2a.medium", "g2a.large", "g2a.xlarge",
        "g3a.small", "g3a.medium", "g3a.large", "g3a.xlarge",
        "g1m.small", "g1m.medium", "g1m.large", "g1m.xlarge",
        "g2m.small", "g2m.medium", "g2m.large", "g2m.xlarge",
        "g3m.small", "g3m.medium", "g3m.large", "g3m.xlarge"
    };

    public CloudInfrastructure() {
        this(1, "N/A", "N/A", "N/A", 0, 0, 0, 0);
    }

    public CloudInfrastructure(int instanceID, String instanceType, String region, String ipAddress,
                               double cpuUtilization, double ramUsageGB, long uptimeSeconds, long creationTimestamp) {
        this.instanceID = instanceID;
        this.instanceType = instanceType;
        this.region = region;
        this.ipAddress = ipAddress;
        this.cpuUtilization = cpuUtilization;
        this.ramUsageGB = ramUsageGB;
        this.uptimeSeconds = uptimeSeconds;
        this.creationTimestamp = creationTimestamp;
    }

    public String[] getAvailableRegions () {
        return availableRegions;
    }

    public String[] getAvailableInstanceTypes () {
        return availableInstanceTypes;
    }

    public int getInstanceID () {
        return instanceID;
    }

}
