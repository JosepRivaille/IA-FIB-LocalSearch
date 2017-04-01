import Utils.InitialStatesEnum;
import Utils.OperatorsEnum;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

class Experiments {

    private static final int REPLICATIONS = 10;

    private static BufferedWriter writerTime;
    private static BufferedWriter writerCost;
    private static BufferedWriter writerInfo;

    static void operators() throws Exception {
        String filePath = "experiments/operators/";
        generateBufferedWriters(filePath);
        printHeader(
                "Switch\tBoth\tSwap\n",
                "Switch\tBoth\tSwap\n",
                "Switch\tBoth\tSwap\n"
        );

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; i++) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            for (OperatorsEnum operator : OperatorsEnum.values()) {
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

                Problem p = new Problem(board, new SensorsSuccessorsHC(operator), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();

                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (operator != OperatorsEnum.SWITCH) {
                    printData("\t", "\t", "\t");
                }
                printData(time.toString(), board.costHeuristic().toString(), board.informationHeuristic().toString());
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
        printHeader(
                "Dummy Sequential\tSimple Greedy\tDistance Greedy\n",
                "Dummy Sequential\tSimple Greedy\tDistance Greedy\n",
                "Dummy Sequential\tSimple Greedy\tDistance Greedy\n"
        );

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; i++) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            for (InitialStatesEnum initialStates : InitialStatesEnum.values()) {
                Long time = System.currentTimeMillis();

                SensorsBoard board = new SensorsBoard(initialStates);

                Problem p = new Problem(board, new SensorsSuccessorsHC(OperatorsEnum.SWITCH), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();

                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (initialStates != InitialStatesEnum.DUMMY_SEQUENTIAL) {
                    printData("\t", "\t", "\t");
                }
                printData(time.toString(), board.costHeuristic().toString(), board.informationHeuristic().toString());
            }
            printData("\n", "\n", "\n");
        }
        writerTime.close();
        writerCost.close();
        writerInfo.close();
    }

    static void parameters() throws Exception {
        String filePath = "experiments/parameters/";
        generateBufferedWriters(filePath);
        printHeader(
                "Time\tTotal iterations\tPartial iterations\tk\tlambda\n",
                "Initial Cost\tCost\tTotal iterations\tPartial iterations\tk\tlambda\n",
                "Information\tTotal iterations\tPartial iterations\tk\tlambda\n"
        );

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; i++) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            for (int it = 0; it < REPLICATIONS; it++) {
                for (int itRep = 0; itRep < REPLICATIONS; itRep++) {
                    for (int k = 0; k < 4; k++) {
                        for (int lambda = 0; lambda < 4; lambda++) {
                            SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);
                            writerCost.append(board.costHeuristic().toString()).append("\t");

                            Problem p = new Problem(board, new SensorsSuccessorsSA(), new SensorsGoal(), new SensorsHeuristic());

                            Search alg = new SimulatedAnnealingSearch(1000 + 1000 * lambda, 100 + 100 * k, (int) (Math.pow(5, k)), 0.001 * Math.pow(10, lambda));

                            Long time = System.currentTimeMillis();
                            new SearchAgent(p, alg);
                            time = System.currentTimeMillis() - time;

                            writerTime.append(time.toString()).append("\t").append(String.valueOf(1000 + 1000 * lambda)).append("\t").append(String.valueOf(100 + 100 * k)).append("\t").append(String.valueOf((int) (Math.pow(5, k)))).append("\t").append(String.valueOf(0.001 * Math.pow(10, lambda)));
                            writerCost.append(SensorsBoard.COST.toString()).append("\t").append(String.valueOf(1000 + 1000 * lambda)).append("\t").append(String.valueOf(100 + 100 * k)).append("\t").append(String.valueOf((int) (Math.pow(5, k)))).append("\t").append(String.valueOf(0.001 * Math.pow(10, lambda)));
                            writerInfo.append(SensorsBoard.INFORMATION.toString()).append("\t").append(String.valueOf(1000 + 1000 * lambda)).append("\t").append(String.valueOf(100 + 100 * k)).append("\t").append(String.valueOf((int) (Math.pow(5, k)))).append("\t").append(String.valueOf(0.001 * Math.pow(10, lambda)));
                            printData("\n", "\n", "\n");
                        }
                    }
                }
            }
        }
        closeWriters();
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
                SensorsBoard.SEED_CENTERS = random.nextInt();
                SensorsBoard.SEED_SENSORS = random.nextInt();
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

                Problem p = new Problem(board, new SensorsSuccessorsHC(OperatorsEnum.SWITCH), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();
                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (j != 0) {
                    printData("\t", "\t", "\t");
                }
                printData(time.toString(), board.costHeuristic().toString(), board.informationHeuristic().toString());
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
                SensorsBoard.SEED_CENTERS = random.nextInt();
                SensorsBoard.SEED_SENSORS = random.nextInt();
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

                Problem p = new Problem(board, new SensorsSuccessorsHC(OperatorsEnum.SWITCH), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();
                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (j != 0) {
                    printData("\t", "\t", "\t");
                }
                printData(time.toString(), board.costHeuristic().toString(), board.informationHeuristic().toString());
            }
            printData("\n", "\n", "\n");
        }
        closeWriters();
    }

    /*----------*/

    private static void generateBufferedWriters(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path);
        writerTime = new BufferedWriter(new FileWriter(filePath + "fileTime.txt"));
        writerCost = new BufferedWriter(new FileWriter(filePath + "fileCost.txt"));
        writerInfo = new BufferedWriter(new FileWriter(filePath + "fileInfo.txt"));
    }

    private static void closeWriters() throws IOException {
        writerTime.close();
        writerCost.close();
        writerInfo.close();
    }

    private static void printHeader(String headerTime, String headerCost, String headerInfo) throws IOException {
        writerTime.append(headerTime);
        writerCost.append(headerCost);
        writerInfo.append(headerInfo);
    }

    private static void printData(String timeData, String costData, String infoData) throws IOException {
        writerTime.append(timeData);
        writerCost.append(costData);
        writerInfo.append(infoData);
    }

}
