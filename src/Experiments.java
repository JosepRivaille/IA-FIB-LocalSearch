import Utils.InitialStatesEnum;
import Utils.OperatorsEnum;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

class Experiments {

    private static final int REPLICATIONS = 5;

    private static BufferedWriter writerTime;
    private static BufferedWriter writerCost;
    private static BufferedWriter writerInfo;

    static void operators() throws Exception {
        String filePath = "experiments/operators/";
        generateBufferedWriters(filePath);
        printHeader("Switch\tBoth\tSwap\n");

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; i++) {
            Integer seedSensors = random.nextInt();
            Integer seedCenters = random.nextInt();

            for (OperatorsEnum operator : OperatorsEnum.values()) {
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);
                SensorsBoard.SEED_CENTERS = seedCenters;
                SensorsBoard.SEED_SENSORS = seedSensors;

                Problem p = new Problem(board, new SensorsSuccessorsHC(operator), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();

                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (operator != OperatorsEnum.SWITCH) {
                    printData("\t", "\t", "\t");
                }
                printData(time.toString(), SensorsBoard.COST.toString(), SensorsBoard.INFORMATION.toString());
            }
            printData("\n", "\n", "\n");
        }
        writerTime.close();
        writerCost.close();
        writerInfo.close();
    }

    static void initialStates() throws Exception {
        String filePath = "experiments/initialStates/";
        generateBufferedWriters(filePath);
        printHeader("Dummy Sequential\tSimple Greedy\tDistance Greedy\n");

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; i++) {
            Integer seedSensors = random.nextInt();
            Integer seedCenters = random.nextInt();

            for (InitialStatesEnum initialStates : InitialStatesEnum.values()) {
                SensorsBoard board = new SensorsBoard(initialStates);
                SensorsBoard.SEED_CENTERS = seedCenters;
                SensorsBoard.SEED_SENSORS = seedSensors;

                Problem p = new Problem(board, new SensorsSuccessorsHC(OperatorsEnum.SWITCH), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();

                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (initialStates != InitialStatesEnum.DUMMY_SEQUENTIAL) {
                    printData("\t", "\t", "\t");
                }
                printData(time.toString(), SensorsBoard.COST.toString(), SensorsBoard.INFORMATION.toString());
            }
            printData("\n", "\n", "\n");
        }
        writerTime.close();
        writerCost.close();
        writerInfo.close();
    }

    static void increments() throws Exception {
        String filePath = "experiments/increments/";
        generateBufferedWriters(filePath);

        Integer incrementSensor = 50;
        Integer incrementCenters = 2;
        Integer numSensors = 100;
        Integer numCenters = 4;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; ++i) {
            SensorsBoard.NUMBER_CENTERS = numCenters + i * incrementCenters;
            SensorsBoard.NUMBER_SENSORS = numSensors + i * incrementSensor;

            for (int j = 0; j < REPLICATIONS; j++) {
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);
                SensorsBoard.NUMBER_CENTERS = numCenters + i * incrementCenters;
                SensorsBoard.NUMBER_SENSORS = numSensors + i * incrementSensor;
                SensorsBoard.SEED_CENTERS = random.nextInt();
                SensorsBoard.SEED_SENSORS = random.nextInt();

                Problem p = new Problem(board, new SensorsSuccessorsHC(OperatorsEnum.SWITCH), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();
                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (j != 0) {
                    printData("\t", "\t", "\t");
                }
                printData(time.toString(), SensorsBoard.COST.toString(), SensorsBoard.INFORMATION.toString());
            }
            printData("\n", "\n", "\n");
        }
        writerTime.close();
        writerCost.close();
        writerInfo.close();
    }

    static void dataCenters() throws Exception {
        String filePath = "experiments/dataCenters/";
        generateBufferedWriters(filePath);

        Integer incrementCenters = 2;
        Integer numCenters = 4;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; ++i) {
            SensorsBoard.NUMBER_CENTERS = numCenters + i * incrementCenters;

            for (int j = 0; j < REPLICATIONS; j++) {
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);
                SensorsBoard.NUMBER_CENTERS = numCenters + i * incrementCenters;
                SensorsBoard.SEED_CENTERS = random.nextInt();
                SensorsBoard.SEED_SENSORS = random.nextInt();

                Problem p = new Problem(board, new SensorsSuccessorsHC(OperatorsEnum.SWITCH), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();
                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (j != 0) {
                    printData("\t", "\t", "\t");
                }
                printData(time.toString(), SensorsBoard.COST.toString(), SensorsBoard.INFORMATION.toString());
            }
            printData("\n", "\n", "\n");
        }
        closeWritters();
    }

    /*----------*/

    private static void generateBufferedWriters(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path);
        writerTime = new BufferedWriter(new FileWriter(filePath + "fileTime.txt"));
        writerCost = new BufferedWriter(new FileWriter(filePath + "fileCost.txt"));
        writerInfo = new BufferedWriter(new FileWriter(filePath + "fileInfo.txt"));
    }

    private static void closeWritters() throws IOException {
        writerTime.close();
        writerCost.close();
        writerInfo.close();
    }

    private static void printHeader(String header) throws IOException {
        writerTime.append(header);
        writerCost.append(header);
        writerInfo.append(header);
    }

    private static void printData(String timeData, String costData, String infoData) throws IOException {
        writerTime.append(timeData);
        writerCost.append(costData);
        writerInfo.append(infoData);
    }

}
