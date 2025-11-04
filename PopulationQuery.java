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
            System.err.println("Usage: java PopulationQuery <filename> <x> <y> <version>");
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
            throw new IllegalArgumentException("Grid size must be an integer");
        }

        if (gridColumns < 1 || gridRows < 1)
            throw new IllegalArgumentException("Grid dimensions must be positive");

        CensusData data = parse(filename);
        TotalPopulation totalPopulation = null;

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
            default -> throw new UnsupportedOperationException("Unsupported version: " + version);
        }

        Scanner scanner = new Scanner(System.in);
        int[] result = new int[2];

        while (true) {
            System.out.println("Please give west, south, east, north coordinates of your rectangle:");
            String input = scanner.nextLine();
            start = System.currentTimeMillis();

            String[] parts = input.split(" ");
            if (parts.length != 4) {
                System.out.println("Invalid input format. Exiting.");
                break;
            }

            try {
                int west = Integer.parseInt(parts[0]);
                int south = Integer.parseInt(parts[1]);
                int east = Integer.parseInt(parts[2]);
                int north = Integer.parseInt(parts[3]);

                if (totalPopulation.isValidCoordinates(west, south, east, north)) {
                    System.out.println("Invalid coordinates. Exiting.");
                    break;
                }

                result = totalPopulation.calculatePopulation(west, south, east, north);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input. Exiting.");
                break;
            }

            end = System.currentTimeMillis();
            System.out.println("population of rectangle: " + result[0]);
            System.out.format("percent of total population: %.2f%n",
                    Math.round(10000.0 * result[0] / result[1]) / 100.0);
            System.out.println("Time Elapsed: " + (end - start) + " ms");
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