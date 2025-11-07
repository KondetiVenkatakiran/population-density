public class Version1 implements TotalPopulation {
    // Instance variables
    CensusData data; // Holds the census data
    int gridColumns; // Number of columns in the grid
    int gridRows; // Number of rows in the grid
    float[] bounds; // Array to store the geographical bounds (west, south, east, north)

    // Constructor to initialize the class with census data and grid dimensions
    public Version1(CensusData data, int gridColumns, int gridRows) {
        this.data = data;
        this.gridColumns = gridColumns;
        this.gridRows = gridRows;
        this.bounds = findBounds(data); // Calculate the geographical bounds based on the data
    }

    // Method to find the geographical bounds of the census data
    private float[] findBounds(CensusData data) {
        float west = 190, south = 80, east = -190, north = -80; // Initialize bounds with extreme values

        // Iterate over the data to find the actual bounds
        for (int i = 0; i < data.data_size; i++) {
            float latitude = data.data[i].latitude;
            float longitude = data.data[i].longitude;
            if (latitude < south) {
                south = latitude; // Update south bound
            }
            if (latitude > north) {
                north = latitude; // Update north bound
            }
            if (longitude < west) {
                west = longitude; // Update west bound
            }
            if (longitude > east) {
                east = longitude; // Update east bound
            }
        }
        return new float[]{west, south, east, north}; // Return the calculated bounds
    }

    // Corrected method to check if the given coordinates are valid within the grid
    public boolean isValidCoordinates(int west, int south, int east, int north) {
        return (west > 0 && south > 0 &&
                east >= west && east <= gridColumns &&
                north >= south && north <= gridRows);
    }

    // Method to check if a point (x, y) is inside the specified rectangle
    public boolean inside(float x, float y, int west, int south, int east, int north) {
        return ((x >= west && x <= east) && (y >= south && y <= north));
    }

    // Method to calculate the population within a specified rectangle
    public int[] calculatePopulation(int west, int south, int east, int north) {
        if (!isValidCoordinates(west, south, east, north))
            throw new IllegalArgumentException("Invalid input coordinates"); // Validate input coordinates

        int[] result = new int[2]; // Array to store the population count and total population
        float columnDistance = (bounds[2] - bounds[0]) / gridColumns; // Calculate the width of each grid cell
        float rowDistance = (bounds[3] - bounds[1]) / gridRows; // Calculate the height of each grid cell

        // Iterate over the data to calculate the population within the rectangle
        for (int i = 0; i < data.data_size; i++) {
            float x = (data.data[i].longitude - bounds[0]) / columnDistance + 1; // Calculate x position in the grid
            float y = (data.data[i].latitude - bounds[1]) / rowDistance + 1; // Calculate y position in the grid
            if (inside(x, y, west, south, east, north))
                result[0] += data.data[i].population; // Add to the population count if inside the rectangle
            result[1] += data.data[i].population; // Add to the total population
        }
        return result; // Return the population count and total population
    }

    // ---------------------------------------------------------
    // Added main() method for independent testing
    // ---------------------------------------------------------
    public static void main(String[] args) {
        // Create some dummy data for testing
        CensusData data = new CensusData();
        data.add(1000, 40.0f, -90.0f);
        data.add(2000, 35.0f, -100.0f);
        data.add(1500, 45.0f, -95.0f);

        int gridColumns = 10;
        int gridRows = 10;

        Version1 version1 = new Version1(data, gridColumns, gridRows);

        // Example rectangle query
        int west = 1, south = 1, east = 5, north = 5;
        int[] result = version1.calculatePopulation(west, south, east, north);

        System.out.println("Population in region: " + result[0]);
        System.out.println("Total population: " + result[1]);
    }
}