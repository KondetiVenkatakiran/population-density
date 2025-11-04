public class Version5 extends Version4 {

  // Constructor to initialize Version5 with census data and grid dimensions
  public Version5(CensusData data, int gridColumns, int gridRows) {
    super(data, gridColumns, gridRows);
  }

  // Method to build the grid using parallel threads
  public void buildGrid() {
    // Initialize grid values with census data and geographical bounds
    ParallelGridBuild gridValues = new ParallelGridBuild(
        data.data,
        gridColumns,
        gridRows,
        bounds[0],
        bounds[1],
        bounds[2],
        bounds[3]
    );

    // Create a grid to store population data
    int[][] populationGrid = new int[gridColumns][gridRows];

    // ✅ Create a lock grid using Object instead of Integer
    Object[][] lockGrid = new Object[gridColumns][gridRows];

    // ✅ Initialize each lock object individually
    for (int i = 0; i < gridColumns; i++) {
      for (int j = 0; j < gridRows; j++) {
        lockGrid[i][j] = new Object();
      }
    }

    // Define the number of threads to be used for parallel processing
    final int numberOfThreads = 4;

    // Start threads for building the grid in parallel
    Thread[] threads = new Thread[numberOfThreads];
    for (int i = 0; i < numberOfThreads; i++) {
      final int startIndex = i * (data.data_size / numberOfThreads);
      final int endIndex = (i == numberOfThreads - 1)
          ? data.data_size
          : (i + 1) * (data.data_size / numberOfThreads);

      threads[i] = new Thread(() -> {
        float columnWidth = (gridValues.easternBoundary - gridValues.westernBoundary) / gridValues.gridColumns;
        float rowHeight = (gridValues.northernBoundary - gridValues.southernBoundary) / gridValues.gridRows;
        int columnIndex, rowIndex;

        for (int j = startIndex; j < endIndex; j++) {
          columnIndex = (int) Math.floor((gridValues.censusGroups[j].longitude - gridValues.westernBoundary) / columnWidth);
          rowIndex = (int) Math.floor((gridValues.censusGroups[j].latitude - gridValues.southernBoundary) / rowHeight);

          // Adjust indices to stay within bounds
          if (columnIndex >= gridValues.gridColumns) columnIndex = gridValues.gridColumns - 1;
          if (rowIndex >= gridValues.gridRows) rowIndex = gridValues.gridRows - 1;

          // ✅ Synchronize safely on custom lock objects
          synchronized (lockGrid[columnIndex][rowIndex]) {
            populationGrid[columnIndex][rowIndex] += gridValues.censusGroups[j].population;
          }
        }
      });
      threads[i].start();
    }

    // Wait for all threads to complete execution
    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    // Finalize the grid for quick population queries
    Version3.finalizeGrid(populationGrid);

    // Assign the finalized grid to the class variable
    gridPopulationData = populationGrid;

    // Mark the grid as built
    isGridBuilt = true;
  }
}