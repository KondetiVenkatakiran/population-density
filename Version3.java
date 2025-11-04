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

      if (columnIndex == gridRows && rowIndex == gridColumns)
        tempGrid[columnIndex - 1][rowIndex - 1] += data.data[i].population;
      else if (columnIndex == gridColumns)
        tempGrid[columnIndex - 1][rowIndex] += data.data[i].population;
      else if (rowIndex == gridRows)
        tempGrid[columnIndex][rowIndex - 1] += data.data[i].population;
      else
        tempGrid[columnIndex][rowIndex] += data.data[i].population;
    }
    finalizeGrid(tempGrid);
    return tempGrid;
  }

  // Step two of grid building: finalize the grid for quick population queries
  public static void finalizeGrid(int[][] grid) {
    for (int i = 1; i < grid[0].length; i++)
      grid[0][i] += grid[0][i - 1];
    for (int j = 1; j < grid.length; j++)
      grid[j][0] += grid[j - 1][0];
    for (int k = 1; k < grid[0].length; k++) {
      for (int l = 1; l < grid.length; l++)
        grid[l][k] += grid[l - 1][k] + grid[l][k - 1] - grid[l - 1][k - 1];
    }
  }

  // Method to calculate the population within a specified rectangle
  public int[] calculatePopulation(int west, int south, int east, int north) {
    if (!isGridBuilt)
      buildGrid();
    if (isValidCoordinates(west, south, east, north))
      throw new IllegalArgumentException("Invalid input coordinates");

    int[] populationResult = new int[2];
    int topLeft, bottomRight, lowerLeft;

    if (south - 2 < 0)
      topLeft = 0;
    else
      topLeft = gridPopulationData[east - 1][south - 2];
    if (west - 2 < 0)
      bottomRight = 0;
    else
      bottomRight = gridPopulationData[west - 2][north - 1];
    if (south - 2 < 0 || west - 2 < 0)
      lowerLeft = 0;
    else
      lowerLeft = gridPopulationData[west - 2][south - 2];

    populationResult[0] = gridPopulationData[east - 1][north - 1] - topLeft - bottomRight + lowerLeft;
    populationResult[1] = gridPopulationData[gridColumns - 1][gridRows - 1];
    return populationResult;
  }
}
