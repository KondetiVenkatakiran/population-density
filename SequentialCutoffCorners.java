import java.util.concurrent.*;

public class SequentialCutoffCorners extends RecursiveAction {
    public static final ForkJoinPool forkJoinPool = new ForkJoinPool();
    public int sequentialCutOff;
    public CensusData censusData;
    public int startIndex, endIndex;
    public float[] boundaries;

    public SequentialCutoffCorners(CensusData censusData, int startIndex, int endIndex, int sequentialCutOff) {
        this.censusData = censusData;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.sequentialCutOff = sequentialCutOff;
        this.boundaries = new float[4];
    }

    public void compute() {
        if (endIndex - startIndex < sequentialCutOff) {
            float westBoundary = censusData.data[startIndex].longitude;
            float southBoundary = censusData.data[startIndex].latitude;
            float eastBoundary = westBoundary;
            float northBoundary = southBoundary;
            for (int i = startIndex + 1; i < endIndex; i++) {
                if (censusData.data[i].longitude < westBoundary)
                    westBoundary = censusData.data[i].longitude;
                if (censusData.data[i].longitude > eastBoundary)
                    eastBoundary = censusData.data[i].longitude;
                if (censusData.data[i].latitude < southBoundary)
                    southBoundary = censusData.data[i].latitude;
                if (censusData.data[i].latitude > northBoundary)
                    northBoundary = censusData.data[i].latitude;
            }
            boundaries = new float[]{westBoundary, southBoundary, eastBoundary, northBoundary};
        } else {
            SequentialCutoffCorners leftTask = new SequentialCutoffCorners(censusData, startIndex, (startIndex + endIndex) / 2, sequentialCutOff);
            SequentialCutoffCorners rightTask = new SequentialCutoffCorners(censusData, (startIndex + endIndex) / 2, endIndex, sequentialCutOff);
            leftTask.fork();
            rightTask.compute();
            leftTask.join();
            for (int i = 0; i < 4; i++) {
                if (i < 2)
                    boundaries[i] = Math.min(leftTask.boundaries[i], rightTask.boundaries[i]);
                else
                    boundaries[i] = Math.max(leftTask.boundaries[i], rightTask.boundaries[i]);
            }
        }
    }

    public static float[] findCornersWithSequentialCutOff(CensusData censusData, int sequentialCutOff) {
        SequentialCutoffCorners task = new SequentialCutoffCorners(censusData, 0, censusData.data_size, sequentialCutOff);
        forkJoinPool.invoke(task);
        return task.boundaries;
    }

    public static void main(String[] args) {
        CensusData censusData = new CensusData();

        // Add some sample data points
        censusData.add(1000, 40.7128f, -74.0060f);  // New York
        censusData.add(500, 34.0522f, -118.2437f);  // Los Angeles
        censusData.add(700, 41.8781f, -87.6298f);   // Chicago
        censusData.add(300, 29.7604f, -95.3698f);   // Houston
        censusData.add(900, 33.4484f, -112.0740f);  // Phoenix

        int sequentialCutOff = 2;

        float[] corners = findCornersWithSequentialCutOff(censusData, sequentialCutOff);

        System.out.println("West boundary: " + corners[0]);
        System.out.println("South boundary: " + corners[1]);
        System.out.println("East boundary: " + corners[2]);
        System.out.println("North boundary: " + corners[3]);
    }
}