import java.util.concurrent.*;

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
}
