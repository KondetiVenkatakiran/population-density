public class ParallelGridBuild {
    // Array to hold census data groups, each representing a geographical area with population data
    public CensusGroup[] censusGroups;

    // Number of columns and rows in the grid, representing the grid's dimensions
    public int gridColumns, gridRows;

    // Geographical boundaries of the grid, defining the area covered by the grid
    public float westernBoundary, southernBoundary, easternBoundary, northernBoundary; // Renamed from west, south, east, north

    // Constructor to initialize the ParallelGridBuild object with census data and grid parameters
    public ParallelGridBuild(CensusGroup[] censusGroups, int gridColumns, int gridRows,
                             float westernBoundary, float southernBoundary,
                             float easternBoundary, float northernBoundary) {
        // Assign the provided census data groups to the class variable
        this.censusGroups = censusGroups;

        // Set the number of columns and rows for the grid
        this.gridColumns = gridColumns;
        this.gridRows = gridRows;

        // Define the geographical boundaries for the grid
        this.westernBoundary = westernBoundary;
        this.southernBoundary = southernBoundary;
        this.easternBoundary = easternBoundary;
        this.northernBoundary = northernBoundary;
    }
}