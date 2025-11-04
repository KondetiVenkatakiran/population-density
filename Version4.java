import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class Version4 extends Version2 {
  // 2D array to store the population data for each grid cell
  public int[][] gridPopulationData;
  // Flag to check if the grid has been built
  protected boolean isGridBuilt;
  private static final ForkJoinPool forkJoinPool = new ForkJoinPool();
  private static final int SEQUENTIAL_CUTOFF_BUILD = 57500;
  private static final int SEQUENTIAL_CUTOFF_JOIN = 25;

  // Constructor to initialize the class with census data and grid dimensions
  public Version4(CensusData data, int gridColumns, int gridRows) {
    super(data, gridColumns, gridRows); // Call the constructor of the superclass Version2
    isGridBuilt = false; // Initially, the grid is not built
    gridPopulationData = null; // Initialize the grid population data to null
  }

  // Method to build the grid with census data
  public void buildGrid() {
    // Create a new GridBuilding task for parallel grid building
    GridBuildingTask gridBuilder = new GridBuildingTask(
            new ParallelGridBuild(data.data, gridColumns, gridRows, bounds[0], bounds[1], bounds[2], bounds[3]), 0, data.data_size
    );
    forkJoinPool.invoke(gridBuilder); // Use ForkJoinPool to execute the grid building task
    int[][] tempGrid = gridBuilder.gridPopulationData; // Retrieve the built grid
    Version3.finalizeGrid(tempGrid); // Finalize the grid for quick population queries
    gridPopulationData = tempGrid; // Assign the finalized grid to the class variable
    isGridBuilt = true; // Mark the grid as built
  }

  // Inner class for grid building logic
  private static class GridBuildingTask extends RecursiveAction {
    ParallelGridBuild gridValues;
    int startIndex, endIndex;
    int[][] gridPopulationData;

    public GridBuildingTask(ParallelGridBuild gridValues, int startIndex, int endIndex) {
      this.gridValues = gridValues;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
      this.gridPopulationData = new int[gridValues.gridColumns][gridValues.gridRows];
    }

    @Override
    protected void compute() {
      if (endIndex - startIndex < SEQUENTIAL_CUTOFF_BUILD) {
        float columnInterval = (gridValues.easternBoundary - gridValues.westernBoundary) / gridValues.gridColumns;
        float rowInterval = (gridValues.northernBoundary - gridValues.southernBoundary) / gridValues.gridRows;
        int columnIndex, rowIndex;

        for (int i = startIndex; i < endIndex; i++) {
          columnIndex = (int) Math.floor((gridValues.censusGroups[i].longitude - gridValues.westernBoundary) / columnInterval);
          rowIndex = (int) Math.floor((gridValues.censusGroups[i].latitude - gridValues.southernBoundary) / rowInterval);

          if (columnIndex == gridValues.gridColumns && rowIndex == gridValues.gridRows) {
            gridPopulationData[columnIndex - 1][rowIndex - 1] += gridValues.censusGroups[i].population;
          } else if (columnIndex == gridValues.gridColumns) {
            gridPopulationData[columnIndex - 1][rowIndex] += gridValues.censusGroups[i].population;
          } else if (rowIndex == gridValues.gridRows) {
            gridPopulationData[columnIndex][rowIndex - 1] += gridValues.censusGroups[i].population;
          } else {
            gridPopulationData[columnIndex][rowIndex] += gridValues.censusGroups[i].population;
          }
        }
      } else {
        int mid = (startIndex + endIndex) / 2;
        GridBuildingTask leftTask = new GridBuildingTask(gridValues, startIndex, mid);
        GridBuildingTask rightTask = new GridBuildingTask(gridValues, mid, endIndex);

        leftTask.fork();
        rightTask.compute();
        leftTask.join();
        gridPopulationData = combine(leftTask.gridPopulationData, rightTask.gridPopulationData);
      }
    }
  }

  // Method to combine grids
  public static int[][] combine(int[][] leftGrid, int[][] rightGrid) {
    int[][] resultGrid = new int[leftGrid.length][leftGrid[0].length];
    GridJoiningTask task = new GridJoiningTask(leftGrid, rightGrid, 0, leftGrid[0].length, 0, leftGrid.length, resultGrid);
    forkJoinPool.invoke(task);
    return task.resultGrid;
  }

  // Inner class for grid joining logic
  private static class GridJoiningTask extends RecursiveAction {
    int[][] leftGrid, rightGrid, resultGrid;
    int xlow, xhigh, ylow, yhigh;

    public GridJoiningTask(int[][] leftGrid, int[][] rightGrid, int xlow, int xhigh, int ylow, int yhigh, int[][] resultGrid) {
      this.leftGrid = leftGrid;
      this.rightGrid = rightGrid;
      this.xlow = xlow;
      this.xhigh = xhigh;
      this.ylow = ylow;
      this.yhigh = yhigh;
      this.resultGrid = resultGrid;
    }

    @Override
    protected void compute() {
      if (xhigh - xlow < SEQUENTIAL_CUTOFF_JOIN && yhigh - ylow < SEQUENTIAL_CUTOFF_JOIN) {
        for (int i = ylow; i < yhigh; i++) {
          for (int j = xlow; j < xhigh; j++) {
            resultGrid[i][j] += leftGrid[i][j] + rightGrid[i][j];
          }
        }
      } else {
        int xmid = (xlow + xhigh) / 2;
        int ymid = (ylow + yhigh) / 2;

        GridJoiningTask lowerLeft = new GridJoiningTask(leftGrid, rightGrid, xlow, xmid, ylow, ymid, resultGrid);
        GridJoiningTask lowerRight = new GridJoiningTask(leftGrid, rightGrid, xmid, xhigh, ylow, ymid, resultGrid);
        GridJoiningTask upperLeft = new GridJoiningTask(leftGrid, rightGrid, xlow, xmid, ymid, yhigh, resultGrid);
        GridJoiningTask upperRight = new GridJoiningTask(leftGrid, rightGrid, xmid, xhigh, ymid, yhigh, resultGrid);

        lowerLeft.fork();
        lowerRight.compute();
        upperLeft.compute();
        upperRight.compute();
        lowerLeft.join();
      }
    }
  }

  // Method to calculate the population within a specified rectangle
  public int[] calculatePopulation(int west, int south, int east, int north) {
    if (!isGridBuilt) // Build the grid if not built
      buildGrid();

    if (!isValidCoordinates(west, south, east, north))
      throw new IllegalArgumentException("Invalid input coordinates");

    int[] populationResult = new int[2]; // result: population in region, total population

    int topLeft = (south - 2 < 0) ? 0 : gridPopulationData[east - 1][south - 2];
    int bottomRight = (west - 2 < 0) ? 0 : gridPopulationData[west - 2][north - 1];
    int lowerLeft = (south - 2 < 0 || west - 2 < 0) ? 0 : gridPopulationData[west - 2][south - 2];

    populationResult[0] = gridPopulationData[east - 1][north - 1] - topLeft - bottomRight + lowerLeft;
    populationResult[1] = gridPopulationData[gridColumns - 1][gridRows - 1]; // total population

    return populationResult;
  }

  @Override
  public boolean isValidCoordinates(int west, int south, int east, int north) {
    // Coordinates should be within grid bounds and west < east, south < north
    if (west < 0 || south < 0 || east > gridColumns || north > gridRows)
      return false;
    if (west >= east || south >= north)
      return false;
    return true;
  }
}