import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Compare the performance of Version1 and Version3 as the number of queries changes.
 * 
 * Updated to use Version1 and Version3 implementations.
 * 
 * @author Chun-Wei Chen (original)
 * @author Your Name (updated)
 * @version Updated 2025
 */
public class V1V3Time {
    public static final CensusData cData = PopulationQuery.parse("CenPop2010.txt");

    public static void main(String[] args) {
        Version1 v1 = new Version1(cData, 100, 500);
        Version3 v3 = new Version3(cData, 100, 500);

        // Warm up JVM with Version1 queries
        for (int i = 0; i < 5; i++) {
            v1.calculatePopulation(1, 1, 100, 500);
        }

        int[] coordinates = new int[4];
        int[] result;
        int count = 1;

        try (Scanner scanner = new Scanner(new File("input.txt"))) {
            while (scanner.hasNextLine()) {
                System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
                String input = scanner.nextLine().trim();
                String[] tokens = input.split("\\s+");
                if (tokens.length != 4) {
                    System.out.println("Invalid input format. Exiting.");
                    break;
                }
                try {
                    for (int i = 0; i < 4; i++) {
                        coordinates[i] = Integer.parseInt(tokens[i]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid coordinate input. Exiting.");
                    break;
                }

                // Time Version1
                long startTime = System.currentTimeMillis();
                try {
                    result = v1.calculatePopulation(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid query coordinates for Version1. Exiting.");
                    break;
                }
                long endTime = System.currentTimeMillis();
                System.out.println("Version1, query " + count + " time: " + (endTime - startTime) + " ms");
                System.out.println("Population of rectangle: " + result[0]);
                System.out.format("Percent of total population: %.2f%%\n", 100.0 * result[0] / result[1]);

                // Time Version3
                startTime = System.currentTimeMillis();
                try {
                    result = v3.calculatePopulation(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid query coordinates for Version3. Exiting.");
                    break;
                }
                endTime = System.currentTimeMillis();
                System.out.println("Version3, query " + count + " time: " + (endTime - startTime) + " ms");
                System.out.println("Population of rectangle: " + result[0]);
                System.out.format("Percent of total population: %.2f%%\n", 100.0 * result[0] / result[1]);

                count++;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Input file not found. Please provide 'input.txt' in the working directory.");
        }
    }
}