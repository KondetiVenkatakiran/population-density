public class V4V5Time {
    public static final CensusData censusData = PopulationQuery.parse("CenPop2010.txt");

    public static void main(String[] args) {
        Version4 version4Instance;
        Version5 version5Instance;
        long startTime, endTime;

        for (int iteration = 0; iteration < 5; iteration++) {
            startTime = System.currentTimeMillis();
            version4Instance = new Version4(censusData, 100, 100);
            version4Instance.buildGrid();
            endTime = System.currentTimeMillis();
            System.out.println("Version 4 with grid size 100 x 100: " + (endTime - startTime));

            startTime = System.currentTimeMillis();
            version5Instance = new Version5(censusData, 100, 100);
            version5Instance.buildGrid();
            endTime = System.currentTimeMillis();
            System.out.println("Version 5 with grid size 100 x 100: " + (endTime - startTime));
        }

        for (int gridSize = 1; gridSize <= 2048; gridSize *= 2) {
            startTime = System.currentTimeMillis();
            version4Instance = new Version4(censusData, gridSize, gridSize);
            version4Instance.buildGrid();
            endTime = System.currentTimeMillis();
            System.out.println("Version 4 with grid size " + gridSize + " x " + gridSize + ": " + (endTime - startTime));

            startTime = System.currentTimeMillis();
            version5Instance = new Version5(censusData, gridSize, gridSize);
            version5Instance.buildGrid();
            endTime = System.currentTimeMillis();
            System.out.println("Version 5 with grid size " + gridSize + " x " + gridSize + ": " + (endTime - startTime));
        }
    }
}
