import java.util.Random;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class DataGenerator {

    public CloudInfrastructure[] generateRandomData (int serverCount) {
        CloudInfrastructure[] generatedServers = new CloudInfrastructure[serverCount];
        Random random = new Random();

        CloudInfrastructure helper = new CloudInfrastructure();

        String[] regions = helper.getAvailableRegions();
        int regionsIndexed = regions.length;

        String[] types = helper.getAvailableInstanceTypes();
        int typesIndexed = types.length;

        // Creating Arraylist of shuffled unique IDs
        List<Integer> uniqueID = new ArrayList<>();
        for (int i = 1; i <= serverCount * 10; i++) {
            uniqueID.add(i);
        }
        Collections.shuffle(uniqueID);

        for (int i = 0; i < serverCount; i++) {
            int instanceID = uniqueID.get(i);
            String instanceType = types[random.nextInt(typesIndexed)];
            String region = regions[random.nextInt(regionsIndexed)];
            String ipAddress = random.nextInt(256) + "." + random.nextInt(256) + "."
                    + random.nextInt(256) + "." + random.nextInt(256);
            double cpuUtilization = random.nextDouble(100);

            double ramUsageGB;
            if (instanceType.endsWith(".small")) {
                ramUsageGB = random.nextDouble(2);
            }
            else if (instanceType.endsWith(".medium")) {
                ramUsageGB = random.nextDouble(4);
            }
            else if (instanceType.endsWith(".large")) {
                ramUsageGB = random.nextDouble(8);
            }
            else if (instanceType.endsWith(".xlarge")) {
                ramUsageGB = random.nextDouble(16);
            }
            else {
                ramUsageGB = random.nextDouble(32);
            }

            long uptimeSeconds = random.nextInt(31536000);
            long currentTime = System.currentTimeMillis();
            long timeOffset = random.nextInt(31536000);
            long creationTimestamp = (currentTime / 1000) - timeOffset;;

            generatedServers[i] = new CloudInfrastructure(instanceID, instanceType, region, ipAddress, cpuUtilization,
                                                            ramUsageGB, uptimeSeconds, creationTimestamp);
        }

        return generatedServers;
    }

}
