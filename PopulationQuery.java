import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class PopulationQuery {
    public static final int TOKENS_PER_LINE = 7;
    public static final int POPULATION_INDEX = 4;
    public static final int LATITUDE_INDEX = 5;
    public static final int LONGITUDE_INDEX = 6;
    static long start;
    static long end;

    private static CensusData cachedData;
    private static TotalPopulation totalPopulation;

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: java PopulationQuery <filename> <gridColumns> <gridRows> <version>");
            System.exit(1);
        }

        String filename = args[0];
        int gridColumns = 0;
        int gridRows = 0;
        String version = args[3];

        try {
            gridColumns = Integer.parseInt(args[1]);
            gridRows = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Grid size must be an integer");
            System.exit(1);
        }

        if (gridColumns < 1 || gridRows < 1) {
            System.err.println("Grid dimensions must be positive");
            System.exit(1);
        }

        CensusData data = parse(filename);

        switch (version) {
            case "-v1" -> {
                System.out.println("You are using Version1...");
                totalPopulation = new Version1(data, gridColumns, gridRows);
            }
            case "-v2" -> {
                System.out.println("You are using Version2...");
                totalPopulation = new Version2(data, gridColumns, gridRows);
            }
            case "-v3" -> {
                System.out.println("You are using Version3...");
                Version3 v3 = new Version3(data, gridColumns, gridRows);
                v3.buildGrid();
                totalPopulation = v3;
            }
            case "-v4" -> {
                System.out.println("You are using Version4...");
                Version4 v4 = new Version4(data, gridColumns, gridRows);
                v4.buildGrid();
                totalPopulation = v4;
            }
            case "-v5" -> {
                System.out.println("You are using Version5...");
                Version5 v5 = new Version5(data, gridColumns, gridRows);
                v5.buildGrid();
                totalPopulation = v5;
            }
            default -> {
                System.err.println("Unsupported version: " + version);
                System.exit(1);
            }
        }

        Scanner scanner = new Scanner(System.in);
        int[] result = new int[2];

        while (true) {
            System.out.println("Please give west, south, east, north coordinates of your rectangle (or type 'exit' to quit):");
            String input = scanner.nextLine().trim();

            // Check if user wants to exit
            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Exiting program. Goodbye!");
                break;
            }

            String[] parts = input.split("\\s+");
            if (parts.length != 4) {
                System.out.println("Invalid input format. Please enter exactly 4 integers separated by spaces.");
                continue; // continue loop so user can retry
            }

            try {
                int west = Integer.parseInt(parts[0]);
                int south = Integer.parseInt(parts[1]);
                int east = Integer.parseInt(parts[2]);
                int north = Integer.parseInt(parts[3]);

                if (!totalPopulation.isValidCoordinates(west, south, east, north)) {
                    System.out.println("Invalid coordinates. Please enter valid coordinates within grid bounds.");
                    continue; // allow retry instead of exiting
                }

                start = System.currentTimeMillis();
                result = totalPopulation.calculatePopulation(west, south, east, north);
                end = System.currentTimeMillis();

                System.out.println("Population of rectangle: " + result[0]);
                System.out.format("Percent of total population: %.2f%%%n",
                        Math.round(10000.0 * result[0] / result[1]) / 100.0);
                System.out.println("Time elapsed: " + (end - start) + " ms");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: coordinates must be integers. Please try again.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error calculating population: " + e.getMessage());
            }
        }

        scanner.close();
    }

    public static CensusData parse(String filename) {
        CensusData result = new CensusData();

        try (BufferedReader fileIn = new BufferedReader(new FileReader(filename))) {
            String oneLine = fileIn.readLine(); // skip header

            while ((oneLine = fileIn.readLine()) != null) {
                String[] tokens = oneLine.split(",");
                if (tokens.length != TOKENS_PER_LINE)
                    throw new NumberFormatException("Invalid line format");
                int population = Integer.parseInt(tokens[POPULATION_INDEX]);
                if (population != 0)
                    result.add(population,
                            Float.parseFloat(tokens[LATITUDE_INDEX]),
                            Float.parseFloat(tokens[LONGITUDE_INDEX]));
            }
        } catch (IOException ioe) {
            System.err.println("Error reading input file.");
            System.exit(1);
        } catch (NumberFormatException nfe) {
            System.err.println("Error in file format: " + nfe.getMessage());
            System.exit(1);
        }

        return result;
    }

    public static void preprocess(String filename, int gridColumns, int gridRows, int version) {
        cachedData = parse(filename);

        switch (version) {
            case 1 -> totalPopulation = new Version1(cachedData, gridColumns, gridRows);
            case 2 -> totalPopulation = new Version2(cachedData, gridColumns, gridRows);
            case 3 -> {
                Version3 v3 = new Version3(cachedData, gridColumns, gridRows);
                v3.buildGrid();
                totalPopulation = v3;
            }
            case 4 -> {
                Version4 v4 = new Version4(cachedData, gridColumns, gridRows);
                v4.buildGrid();
                totalPopulation = v4;
            }
            case 5 -> {
                Version5 v5 = new Version5(cachedData, gridColumns, gridRows);
                v5.buildGrid();
                totalPopulation = v5;
            }
            default -> throw new IllegalArgumentException("Invalid version number: " + version);
        }

        System.out.println("Preprocessing complete for Version " + version);
    }

    public static Pair<Integer, Float> singleInteraction(int w, int s, int e, int n) {
        if (totalPopulation == null) {
            System.err.println("Error: Data not preprocessed. Run preprocess() first.");
            return new Pair<>(0, 0.0f);
        }

        int[] result = totalPopulation.calculatePopulation(w, s, e, n);
        float percent = (float) result[0] / result[1];
        return new Pair<>(result[0], percent);
    }
}