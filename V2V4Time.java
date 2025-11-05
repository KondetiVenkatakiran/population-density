import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class V2V4Time {
    public static final CensusData cData = PopulationQuery.parse("CenPop2010.txt");

    public static void main(String[] args) {
        long startTime, endTime;
        Version2 v2 = new Version2(cData, 100, 500);
        Version4 v4 = new Version4(cData, 100, 500);

        // warm up Version 2
        for (int i = 0; i < 5; i++) {
            v2.calculatePopulation(1, 1, 100, 500);
        }
        // warm up Version 4
        for (int i = 0; i < 5; i++) {
            v4.calculatePopulation(1, 1, 100, 500);
        }

        int[] coordinates = new int[4];
        int[] result = new int[2];
        int count = 1;
        Scanner scanner = null;

        try {
            scanner = new Scanner(new File("input.txt"));
        } catch (FileNotFoundException e) {
            System.err.println("Input file not found.");
            System.exit(1);
        }

        while (scanner.hasNextLine()) {
            System.out.println("Please give west, south, east, north coordinates of your query rectangle:");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) break;

            String[] tokens = input.split("\\s+");
            if (tokens.length != 4) {
                System.err.println("Invalid number of coordinates. Exiting.");
                scanner.close();
                System.exit(1);
            }
            try {
                for (int i = 0; i < 4; i++) {
                    coordinates[i] = Integer.parseInt(tokens[i]);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid coordinate format. Exiting.");
                scanner.close();
                System.exit(1);
            }

            try {
                startTime = System.currentTimeMillis();
                result = v2.calculatePopulation(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
                endTime = System.currentTimeMillis();
                System.out.println("Version 2, query " + count + " time: " + (endTime - startTime) + " ms");
                System.out.println("Population of rectangle: " + result[0]);
                System.out.format("Percent of total population: %.2f%%\n", 100.0 * result[0] / result[1]);
            } catch (IllegalArgumentException e) {
                System.err.println("Version 2: " + e.getMessage());
                scanner.close();
                System.exit(1);
            }

            try {
                startTime = System.currentTimeMillis();
                result = v4.calculatePopulation(coordinates[0], coordinates[1], coordinates[2], coordinates[3]);
                endTime = System.currentTimeMillis();
                System.out.println("Version 4, query " + count + " time: " + (endTime - startTime) + " ms");
                System.out.println("Population of rectangle: " + result[0]);
                System.out.format("Percent of total population: %.2f%%\n", 100.0 * result[0] / result[1]);
            } catch (IllegalArgumentException e) {
                System.err.println("Version 4: " + e.getMessage());
                scanner.close();
                System.exit(1);
            }

            count++;
            System.out.println("-------------------------------------------");
        }
        scanner.close();
    }
}