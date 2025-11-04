import java.util.concurrent.ForkJoinPool;

public class SequentialCutOffGridBuildingTime {
    public static final ForkJoinPool forkJoinPool = new ForkJoinPool();
    public static final CensusData censusData = PopulationQuery.parse("CenPop2010.txt");
    public static final int gridColumns = 100;
    public static final int gridRows = 500;

    private static void buildGridWithSequentialCutOff(Version4 version, int cutOff) {
        ParallelGridBuild gridBuilder = new ParallelGridBuild(version.data.data, version.gridColumns, version.gridRows,
                version.bounds[0], version.bounds[1], version.bounds[2], version.bounds[3]);
        SequentialCutOffGridBuilding task = new SequentialCutOffGridBuilding(gridBuilder, 0, version.data.data_size, cutOff);
        forkJoinPool.invoke(task);
        version.gridPopulationData = task.grid;
        version.isGridBuilt = true;
    }

    public static void main(String[] args) {
        long startTime, endTime;

        // Warm-up phase
        for (int i = 0; i < 5; i++) {
            Version4 version = new Version4(censusData, 100, 500);
            startTime = System.currentTimeMillis();
            buildGridWithSequentialCutOff(version, 50);
            endTime = System.currentTimeMillis();
            System.out.println("Parallel build grid with sequential cut-off = 50: " + (endTime - startTime));
        }

        // Test with increasing cut-off values
        for (int cutOff = 2; cutOff < 220000; cutOff *= 2) {
            Version4 version = new Version4(censusData, 100, 500);
            startTime = System.currentTimeMillis();
            buildGridWithSequentialCutOff(version, cutOff);
            endTime = System.currentTimeMillis();
            System.out.println("Parallel build grid with sequential cut-off = " + cutOff + ": " + (endTime - startTime));
        }

        // Final test with a large cut-off
        Version4 version = new Version4(censusData, 100, 500);
        startTime = System.currentTimeMillis();
        buildGridWithSequentialCutOff(version, 220000);
        endTime = System.currentTimeMillis();
        System.out.println("Parallel build grid with sequential cut-off = 220000: " + (endTime - startTime));
    }
}
