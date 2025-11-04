public class SequentialCutOffCornersTime {
    public static final CensusData censusData = PopulationQuery.parse("CenPop2010.txt");

    public static void main(String[] args) {
        double startTime, endTime;
        float[] corners;

        // Warm-up phase
        for (int i = 0; i < 5; i++) {
            startTime = System.currentTimeMillis();
            corners = SequentialCutoffCorners.findCornersWithSequentialCutOff(censusData, 50);
            endTime = System.currentTimeMillis();
            System.out.println("Parallel find corners with sequential cut-off = 50: " + (endTime - startTime));
        }

        // Test with increasing cut-off values
        for (int cutOff = 2; cutOff < 200000; cutOff *= 2) {
            startTime = System.currentTimeMillis();
            corners = SequentialCutoffCorners.findCornersWithSequentialCutOff(censusData, cutOff);
            endTime = System.currentTimeMillis();
            System.out.println("Parallel find corners with sequential cut-off = " + cutOff + ": " + (endTime - startTime));
            System.out.println(corners[0] + "," + corners[1] + "," + corners[2] + "," + corners[3]);
        }

        // Final test with a large cut-off
        startTime = System.currentTimeMillis();
        corners = SequentialCutoffCorners.findCornersWithSequentialCutOff(censusData, 220000);
        endTime = System.currentTimeMillis();
        System.out.println("Parallel find corners with sequential cut-off = 220000: " + (endTime - startTime));
        System.out.println(corners[0] + "," + corners[1] + "," + corners[2] + "," + corners[3]);
    }
}
