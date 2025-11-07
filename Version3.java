public class Version3 extends Version1 {
  public int[][] gridPopulationData;
  private boolean isGridBuilt;

  public Version3(CensusData data, int gridColumns, int gridRows) {
    super(data, gridColumns, gridRows);
    isGridBuilt = false;
    gridPopulationData = null;
  }

  // Method to build the grid with census data
  public void buildGrid() {
    gridPopulationData = initializeGrid(data);
    isGridBuilt = true;
  }

  // Step one of grid building: populate the grid with census data
  private int[][] initializeGrid(CensusData data) {
    int[][] tempGrid = new int[gridColumns][gridRows];
    float columnWidth = (bounds[2] - bounds[0]) / gridColumns;
    float rowHeight = (bounds[3] - bounds[1]) / gridRows;
    int columnIndex, rowIndex;

    for (int i = 0; i < data.data_size; i++) {
      columnIndex = (int) Math.floor((data.data[i].longitude - bounds[0]) / columnWidth);
      rowIndex = (int) Math.floor((data.data[i].latitude - bounds[1]) / rowHeight);

      // Clamp indices if they fall exactly on the boundary
      if (columnIndex >= gridColumns) columnIndex = gridColumns - 1;
      if (rowIndex >= gridRows) rowIndex = gridRows - 1;

      tempGrid[columnIndex][rowIndex] += data.data[i].population;
    }

    finalizeGrid(tempGrid);
    return tempGrid;
  }

  // Step two of grid building: finalize the grid for quick population queries
  public static void finalizeGrid(int[][] grid) {
    // Prefix sums by rows
    for (int i = 0; i < grid.length; i++) {
      for (int j = 1; j < grid[0].length; j++) {
        grid[i][j] += grid[i][j - 1];
      }
    }
    // Prefix sums by columns
    for (int j = 0; j < grid[0].length; j++) {
      for (int i = 1; i < grid.length; i++) {
        grid[i][j] += grid[i - 1][j];
      }
    }
  }

  // Method to calculate the population within a specified rectangle
  public int[] calculatePopulation(int west, int south, int east, int north) {
    if (!isGridBuilt)
      buildGrid();

    if (!isValidCoordinates(west, south, east, north))
      throw new IllegalArgumentException("Invalid input coordinates");

    int[] populationResult = new int[2];

    // Calculate population using prefix sums
    int totalPopulation = gridPopulationData[gridColumns - 1][gridRows - 1];

    int populationInRect = gridPopulationData[east - 1][north - 1];
    if (west > 1)
      populationInRect -= gridPopulationData[west - 2][north - 1];
    if (south > 1)
      populationInRect -= gridPopulationData[east - 1][south - 2];
    if (west > 1 && south > 1)
      populationInRect += gridPopulationData[west - 2][south - 2];

    populationResult[0] = populationInRect;
    populationResult[1] = totalPopulation;

    return populationResult;
  }
}