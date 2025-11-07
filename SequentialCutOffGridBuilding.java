import java.util.concurrent.*;

// You should already have your CensusGroup and ParallelGridBuild classes defined elsewhere

public class SequentialCutOffGridBuilding extends RecursiveAction {
    public int sequentialCutOff;
    public ParallelGridBuild gridBuilder;
    public int startIndex, endIndex;
    public int[][] grid;

    public SequentialCutOffGridBuilding(ParallelGridBuild gridBuilder, int startIndex, int endIndex, int sequentialCutOff) {
        this.gridBuilder = gridBuilder;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.sequentialCutOff = sequentialCutOff;
        this.grid = new int[gridBuilder.gridColumns][gridBuilder.gridRows];
    }

    @Override
    public void compute() {
        if (endIndex - startIndex < sequentialCutOff) {
            float columnInterval = (gridBuilder.easternBoundary - gridBuilder.westernBoundary) / gridBuilder.gridColumns;
            float rowInterval = (gridBuilder.northernBoundary - gridBuilder.southernBoundary) / gridBuilder.gridRows;
            int x, y;
            for (int i = startIndex; i < endIndex; i++) {
                x = (int) Math.floor((gridBuilder.censusGroups[i].longitude - gridBuilder.westernBoundary) / columnInterval);
                y = (int) Math.floor((gridBuilder.censusGroups[i].latitude - gridBuilder.southernBoundary) / rowInterval);
                if (x == gridBuilder.gridColumns && y == gridBuilder.gridRows)
                    grid[x - 1][y - 1] += gridBuilder.censusGroups[i].population;
                else if (x == gridBuilder.gridColumns)
                    grid[x - 1][y] += gridBuilder.censusGroups[i].population;
                else if (y == gridBuilder.gridRows)
                    grid[x][y - 1] += gridBuilder.censusGroups[i].population;
                else
                    grid[x][y] += gridBuilder.censusGroups[i].population;
            }
        } else {
            SequentialCutOffGridBuilding leftTask = new SequentialCutOffGridBuilding(gridBuilder, startIndex, (startIndex + endIndex) / 2, sequentialCutOff);
            SequentialCutOffGridBuilding rightTask = new SequentialCutOffGridBuilding(gridBuilder, (startIndex + endIndex) / 2, endIndex, sequentialCutOff);
            leftTask.fork();
            rightTask.compute();
            leftTask.join();
            for (int i = 0; i < gridBuilder.gridRows; i++) {
                for (int j = 0; j < gridBuilder.gridColumns; j++)
                    grid[j][i] += leftTask.grid[j][i] + rightTask.grid[j][i];
            }
        }
    }

    public static void main(String[] args) {
        CensusGroup[] censusGroups = new CensusGroup[] {
            new CensusGroup(100, 1.5f, 1.5f),
            new CensusGroup(200, 2.5f, 2.5f),
            new CensusGroup(150, 3.5f, 3.5f),
            new CensusGroup(300, 7.5f, 7.5f),
            new CensusGroup(50, 9.5f, 9.5f),
            new CensusGroup(120, 4.9f, 5.0f),
            new CensusGroup(180, 6.0f, 1.0f),
            new CensusGroup(90, 0.5f, 9.0f),
            new CensusGroup(60, 8.0f, 3.0f),
            new CensusGroup(40, 5.5f, 2.5f)
        };

        int gridColumns = 5;
        int gridRows = 5;
        float west = 0f;
        float south = 0f;
        float east = 10f;
        float north = 10f;

        ParallelGridBuild gridBuilder = new ParallelGridBuild(censusGroups, gridColumns, gridRows, west, south, east, north);

        int sequentialCutOff = 3;
        SequentialCutOffGridBuilding task = new SequentialCutOffGridBuilding(gridBuilder, 0, censusGroups.length, sequentialCutOff);

        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(task);

        System.out.println("Population grid:");
        for (int i = gridRows - 1; i >= 0; i--) {
            for (int j = 0; j < gridColumns; j++) {
                System.out.print(task.grid[j][i] + "\t");
            }
            System.out.println();
        }
    }
}