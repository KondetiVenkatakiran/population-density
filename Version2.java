import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class Version2 implements TotalPopulation {
  // ForkJoinPool for parallel processing
  public static final ForkJoinPool forkJoin = new ForkJoinPool();
  CensusData data; // Holds the census data
  int gridColumns; // Number of columns in the grid
  int gridRows; // Number of rows in the grid
  float[] bounds; // Array to store the geographical bounds (west, south, east, north)

  // Constructor to initialize the class with census data and grid dimensions
  public Version2(CensusData data, int gridColumns, int gridRows) {
    this.data = data;
    this.gridColumns = gridColumns;
    this.gridRows = gridRows;
    this.bounds = calculateCorners(data); // Calculate the geographical bounds
  }

  // Corrected method to check if the given coordinates are valid within the grid
  public boolean isValidCoordinates(int west, int south, int east, int north) {
    return (west > 0 && south > 0 &&
            east >= west && east <= gridColumns &&
            north >= south && north <= gridRows);
  }

  // Method to calculate the population within a specified rectangle
  public int[] calculatePopulation(int west, int south, int east, int north) {
    if (!isValidCoordinates(west, south, east, north))
      throw new IllegalArgumentException("Invalid input coordinates"); // Validate input coordinates

    int[] coordinates = new int[] { west, south, east, north }; // Store the rectangle coordinates
    ParallelGridPop parallelGridPopulationTask = new ParallelGridPop(data, bounds, coordinates, gridColumns, gridRows, 0, data.data_size);
    forkJoin.invoke(parallelGridPopulationTask);

    return parallelGridPopulationTask.populationData;
  }

  // Inner class for parallel corner calculation
  private static class ParallelCorners extends RecursiveAction {
    public CensusData censusData;
    public int startIndex, endIndex;
    public float[] cornerCoordinates;

    // Constructor that initializes the variables
    public ParallelCorners(CensusData censusData, int startIndex, int endIndex) {
      this.censusData = censusData;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
      this.cornerCoordinates = new float[4];
    }

    public void compute() {
      if (endIndex - startIndex == 1) {
        float latitude = censusData.data[startIndex].latitude;
        float longitude = censusData.data[startIndex].longitude;
        cornerCoordinates = new float[]{longitude, latitude, longitude, latitude};
      } else {
        ParallelCorners leftTask = new ParallelCorners(censusData, startIndex, (startIndex + endIndex) / 2);
        ParallelCorners rightTask = new ParallelCorners(censusData, (startIndex + endIndex) / 2, endIndex);
        leftTask.fork();
        rightTask.compute();
        leftTask.join();
        for (int i = 0; i < 4; i++) {
          if (i < 2)
            cornerCoordinates[i] = Math.min(leftTask.cornerCoordinates[i], rightTask.cornerCoordinates[i]);
          else
            cornerCoordinates[i] = Math.max(leftTask.cornerCoordinates[i], rightTask.cornerCoordinates[i]);
        }
      }
    }
  }

  // Method to calculate corners using the inner ParallelCorners class
  public static float[] calculateCorners(CensusData censusData) {
    ParallelCorners parallelCornersTask = new ParallelCorners(censusData, 0, censusData.data_size);
    forkJoin.invoke(parallelCornersTask);
    return parallelCornersTask.cornerCoordinates;
  }

  // Inner class for parallel grid population calculation
  private static class ParallelGridPop extends RecursiveAction {
    public CensusData data; // The census data to be processed.
    public float[] bounds; // The geographical bounds (west, south, east, north) of the grid.
    public int gridColumns, gridRows; // The number of columns and rows in the grid.
    public int startIndex, endIndex; // The range of data indices this task will process.
    public int[] coordinates; // The coordinates defining the query rectangle.
    public int[] populationData; // Array to store the population count for the specified grid area.

    // Constructor to initialize the task with necessary parameters.
    public ParallelGridPop(CensusData data, float[] bounds, int[] coordinates, int gridColumns, int gridRows, int startIndex, int endIndex) {
      this.data = data;
      this.bounds = bounds;
      this.coordinates = coordinates;
      this.gridColumns = gridColumns;
      this.gridRows = gridRows;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
      this.populationData = new int[2]; // Initialize the population data array.
    }

    // Method to check if a given point (longitudeIndex, latitudeIndex) is within the specified grid area.
    public boolean grid(float longitudeIndex, float latitudeIndex) {
      return ((longitudeIndex >= coordinates[0] && longitudeIndex <= coordinates[2]) &&
              (latitudeIndex >= coordinates[1] && latitudeIndex <= coordinates[3]));
    }

    // The compute method is where the parallel computation logic is implemented.
    public void compute() {
      // Base case: if the task is responsible for a single data point.
      if (endIndex - startIndex == 1) {
        // Calculate the distance between grid columns and rows.
        float columnDistance = (bounds[2] - bounds[0]) / gridColumns;
        float rowDistance = (bounds[3] - bounds[1]) / gridRows;

        // Determine the grid indices for the current data point.
        float longitudeIndex = (data.data[startIndex].longitude - bounds[0]) / columnDistance + 1;
        float latitudeIndex = (data.data[startIndex].latitude - bounds[1]) / rowDistance + 1;

        // Check if the data point is within the specified grid area and update population data.
        if (grid(longitudeIndex, latitudeIndex)) {
          populationData[0] += data.data[startIndex].population;
        }
        populationData[1] += data.data[startIndex].population;
      } else {
        // Recursive case: split the task into two subtasks.
        ParallelGridPop leftTask = new ParallelGridPop(data, bounds, coordinates, gridColumns, gridRows, startIndex, (startIndex + endIndex) / 2);
        ParallelGridPop rightTask = new ParallelGridPop(data, bounds, coordinates, gridColumns, gridRows, (startIndex + endIndex) / 2, endIndex);
        leftTask.fork(); // Fork the left task to run asynchronously.
        rightTask.compute(); // Compute the right task.
        leftTask.join(); // Wait for the left task to complete.

        // Combine the results from the subtasks.
        populationData[0] = leftTask.populationData[0] + rightTask.populationData[0];
        populationData[1] = leftTask.populationData[1] + rightTask.populationData[1];
      }
    }
  }

  // ----------------------------------------
  // Added main() method for independent testing
  // ----------------------------------------
  public static void main(String[] args) {
    // Create some dummy census data for testing
    CensusData data = new CensusData();
    data.add(1000, 40.0f, -90.0f);
    data.add(2000, 35.0f, -100.0f);
    data.add(1500, 45.0f, -95.0f);

    int gridColumns = 10;
    int gridRows = 10;

    Version2 version2 = new Version2(data, gridColumns, gridRows);

    // Example rectangle query
    int west = 1, south = 1, east = 5, north = 5;
    int[] result = version2.calculatePopulation(west, south, east, north);

    System.out.println("Population in region: " + result[0]);
    System.out.println("Total population: " + result[1]);
  }
}