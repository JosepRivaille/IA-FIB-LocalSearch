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
                "Switch\tSwap\tBoth\n",
                "Switch\tSwap\tBoth\n",
                "Switch\tSwap\tBoth\n"
        );

        SensorsBoard.NUMBER_CENTERS = 4;
        SensorsBoard.NUMBER_SENSORS = 100;
        SensorsBoard.INFORMATION_WEIGHT = 2.5;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; i++) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            for (OperatorsEnum operator : OperatorsEnum.values()) {
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

                Problem p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());
                SensorsSuccessorsHC.CHOSEN_OPERATOR = operator;
                Search alg = new HillClimbingSearch();

                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (operator != OperatorsEnum.SWITCH) {
                    printData("\t", "\t", "\t");
                }
                printData(time.toString(), SensorsBoard.TOTAL_COST.toString(), SensorsBoard.TOTAL_INFORMATION.toString());
            }
            printData("\n", "\n", "\n");
        }
        closeWriters();
    }

    static void initialStates() throws Exception {
        String filePath = "experiments/initialStates/";
        generateBufferedWriters(filePath);
        printHeader(
                "Dummy Sequential\tSimple Greedy\tDistance Greedy\n",
                "Dummy Sequential\tSimple Greedy\tDistance Greedy\n",
                "Dummy Sequential\tSimple Greedy\tDistance Greedy\n"
        );

        SensorsBoard.NUMBER_CENTERS = 4;
        SensorsBoard.NUMBER_SENSORS = 100;
        SensorsSuccessorsHC.CHOSEN_OPERATOR = OperatorsEnum.SWITCH;
        SensorsBoard.INFORMATION_WEIGHT = 2.5;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; i++) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            for (InitialStatesEnum initialStates : InitialStatesEnum.values()) {
                Long time = System.currentTimeMillis();

                SensorsBoard board = new SensorsBoard(initialStates);

                Problem p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();

                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (initialStates != InitialStatesEnum.DUMMY_SEQUENTIAL) {
                    printData("\t", "\t", "\t");
                }
                printData(time.toString(), SensorsBoard.TOTAL_COST.toString(), SensorsBoard.TOTAL_INFORMATION.toString());
            }
            printData("\n", "\n", "\n");
        }
        closeWriters();
    }

    static void parameters() throws Exception {
        String filePath = "experiments/parameters/";
        generateBufferedWriters(filePath);
        printHeader(
                "Time\tTotal iterations\tPartial iterations\tk\tlambda\n",
                "Initial Cost\tCost\tTotal iterations\tPartial iterations\tk\tlambda\n",
                "Information\tTotal iterations\tPartial iterations\tk\tlambda\n"
        );

        SensorsBoard.NUMBER_CENTERS = 4;
        SensorsBoard.NUMBER_SENSORS = 100;
        SensorsBoard.INFORMATION_WEIGHT = 2.5;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; i++) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            for (int it = 0; it < REPLICATIONS; it++) {
                for (int itRep = 0; itRep < REPLICATIONS; itRep++) {
                    for (int k = 0; k < 4; k++) {
                        for (int lambda = 0; lambda < 4; lambda++) {
                            SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);
                            writerCost.append(String.valueOf(SensorsBoard.TOTAL_COST)).append("\t");

                            Problem p = new Problem(board, new SensorsSuccessorsSA(), new SensorsGoal(), new SensorsHeuristic());

                            Search alg = new SimulatedAnnealingSearch(1000 + 1000 * lambda, 100 + 100 * k, (int) (Math.pow(5, k)), 0.001 * Math.pow(10, lambda));

                            Long time = System.currentTimeMillis();
                            new SearchAgent(p, alg);
                            time = System.currentTimeMillis() - time;

                            printData(
                                    time.toString() + "\t" + String.valueOf(1000 + 1000 * lambda) + "\t" + String.valueOf(100 + 100 * k) + "\t" + String.valueOf(Math.pow(5, k)) + "\t" + String.valueOf(0.001 * Math.pow(10, lambda)),
                                    SensorsBoard.TOTAL_COST + "\t" + String.valueOf(1000 + 1000 * lambda) + "\t" + String.valueOf(100 + 100 * k) + "\t" + String.valueOf(Math.pow(5, k)) + "\t" + String.valueOf(0.001 * Math.pow(10, lambda)),
                                    SensorsBoard.TOTAL_INFORMATION + "\t" + String.valueOf(1000 + 1000 * lambda) + "\t" + String.valueOf(100 + 100 * k) + "\t" + String.valueOf(Math.pow(5, k)) + "\t" + String.valueOf(0.001 * Math.pow(10, lambda))
                            );
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
        printHeader(
                "T1\tT2\tT3\tT4\n",
                "C1\tC2\tC3\tC4\n",
                "I1\tI2\tI3\tI4\n"
        );

        Integer incrementSensor = 50;
        Integer incrementCenters = 2;
        Integer numSensors = 100;
        Integer numCenters = 4;

        SensorsSuccessorsHC.CHOSEN_OPERATOR = OperatorsEnum.SWITCH;
        SensorsBoard.INFORMATION_WEIGHT = 2.5;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; ++i) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            for (int j = 0; j < REPLICATIONS; j++) {
                SensorsBoard.NUMBER_CENTERS = numCenters + j * incrementCenters;
                SensorsBoard.NUMBER_SENSORS = numSensors + j * incrementSensor;
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

                Problem p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();

                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (j != 0) {
                    printData("\t", "\t", "\t");
                }
                printData(time.toString(), SensorsBoard.TOTAL_COST.toString(), SensorsBoard.TOTAL_INFORMATION.toString());
            }
            printData("\n", "\n", "\n");
        }
        closeWriters();
    }

    static void proportion() throws Exception {
        String filePath = "experiments/proportion/";
        generateBufferedWriters(filePath);
        printHeader(
                "Time\n",
                "Proportion\n",
                "Information\n"
        );

        SensorsBoard.NUMBER_CENTERS = 4;
        SensorsBoard.NUMBER_SENSORS = 100;
        SensorsSuccessorsHC.CHOSEN_OPERATOR = OperatorsEnum.SWITCH;
        SensorsBoard.INFORMATION_WEIGHT = 2.5;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; ++i) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();
            SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

            Problem p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());
            Search alg = new HillClimbingSearch();

            Long time = System.currentTimeMillis();
            new SearchAgent(p, alg);
            time = System.currentTimeMillis() - time;

            printData(time.toString(), String.valueOf((double) SensorsBoard.USED_CENTERS / SensorsBoard.NUMBER_SENSORS), SensorsBoard.TOTAL_INFORMATION.toString());
            printData("\n", "\n", "\n");
        }
        closeWriters();
    }

    static void dataCenters() throws Exception {
        String filePath = "experiments/dataCenters/";
        generateBufferedWriters(filePath);
        printHeader(
                "THC1\tTSA1\tTHC2\tTSA2\tTHC3\tTSA3\tTHC4\tTSA4\n",
                "CHC1\tDCHC1\tCSA1\tDCSA1\tCHC2\tDCHC2\tCSA2\tDCSA2\tCHC3\tDCHC3\tCSA3\tDCSA3\tCHC4\tDCHC4\tCSA4\tDCSA4\n",
                "IHC1\tISA1\tIHC2\tISA2\tIHC3\tISA3\tIHC4\tISA4\n"
        );

        Integer incrementCenters = 2;
        Integer numCenters = 4;

        SensorsBoard.NUMBER_SENSORS = 100;
        SensorsSuccessorsHC.CHOSEN_OPERATOR = OperatorsEnum.SWITCH;
        SensorsBoard.INFORMATION_WEIGHT = 2.5;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; ++i) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            for (int j = 0; j < 4; j++) {
                SensorsBoard.NUMBER_CENTERS = numCenters + j * incrementCenters;

                Long time = System.currentTimeMillis();
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

                Problem p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (j != 0) {
                    printData("\t", "\t", "\t");
                }
                printData(time.toString(), SensorsBoard.TOTAL_COST.toString() + "\t" + board.getUsedCenters(), SensorsBoard.TOTAL_INFORMATION.toString());

                time = System.currentTimeMillis();
                board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);
                p = new Problem(board, new SensorsSuccessorsSA(), new SensorsGoal(), new SensorsHeuristic());
                alg = new SimulatedAnnealingSearch();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                printData("\t", "\t", "\t");
                printData(time.toString(), SensorsBoard.TOTAL_COST.toString() + "\t" + board.getUsedCenters(), SensorsBoard.TOTAL_INFORMATION.toString());
            }
            printData("\n", "\n", "\n");
        }
        closeWriters();
    }

    static void heuristic() throws Exception {
        String filePath = "experiments/heuristic/";
        generateBufferedWriters(filePath);
        printHeader(
                "T1\tT2\tT3\tT4\tT5\tT6\tT7\tT8\tT9\tT10\n",
                "C1\tC2\tC3\tC4\tC5\tC6\tC7\tC8\tC9\tC10\n",
                "I1\tI2\tI3\tI4\tI5\tI6\tI7\tI8\tI9\tI10\n"
        );

        SensorsBoard.NUMBER_SENSORS = 100;
        SensorsBoard.NUMBER_CENTERS = 2;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; ++i) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            Integer nWeight = 1;
            for (int j = 0; j < REPLICATIONS; j++) {
                SensorsBoard.INFORMATION_WEIGHT = nWeight + j * 0.2;
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

                Problem p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());
                SensorsSuccessorsHC.CHOSEN_OPERATOR = OperatorsEnum.SWITCH;
                Search alg = new HillClimbingSearch();
                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (j != 0) {
                    printData("\t", "\t", "\t");
                }
                printData(time.toString(), SensorsBoard.TOTAL_COST.toString(), SensorsBoard.TOTAL_INFORMATION.toString());
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
