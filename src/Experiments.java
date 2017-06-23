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
    private static BufferedWriter[] bufferedWriters;

    /* Experiment 1 */
    static void operators() throws Exception {
        String filePath = "experiments/operators/";
        generateBufferedWriters(filePath, "Cost", "Information", "Expansions", "Time");
        for (BufferedWriter bufferedWriter : bufferedWriters) {
            bufferedWriter.append("Switch\tSwap\tBoth\n");
        }

        SensorsBoard.NUMBER_CENTERS = 4;
        SensorsBoard.NUMBER_SENSORS = 100;
        SensorsBoard.INFORMATION_WEIGHT = 2.5;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; i++) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            for (OperatorsEnum operator : OperatorsEnum.values()) {
                Long time = System.currentTimeMillis();
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

                SensorsSuccessorsHC.CHOSEN_OPERATOR = operator;
                Problem p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();

                SearchAgent searchAgent = new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (operator != OperatorsEnum.SWITCH) {
                    printData("\t", "\t", "\t", "\t");
                }
                printData(
                        SensorsBoard.TOTAL_COST.toString(),
                        SensorsBoard.TOTAL_INFORMATION.toString(),
                        searchAgent.getInstrumentation().getProperty("nodesExpanded"),
                        time.toString()
                );
            }
            printData("\n", "\n", "\n", "\n");
        }
        closeWriters();
    }

    /* Experiment 2 */
    static void initialStates() throws Exception {
        String filePath = "experiments/initialStates/";
        generateBufferedWriters(filePath, "Cost", "Information", "Expansions", "Time");
        for (BufferedWriter bufferedWriter : bufferedWriters) {
            bufferedWriter.append("Dummy Sequential\tSimple Greedy\tDistance Greedy\n");
        }

        SensorsBoard.NUMBER_CENTERS = 4;
        SensorsBoard.NUMBER_SENSORS = 100;
        SensorsBoard.INFORMATION_WEIGHT = 2.5;
        SensorsSuccessorsHC.CHOSEN_OPERATOR = OperatorsEnum.SWITCH;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; i++) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            for (InitialStatesEnum initialStates : InitialStatesEnum.values()) {
                Long time = System.currentTimeMillis();
                SensorsBoard board = new SensorsBoard(initialStates);

                Problem p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();

                SearchAgent searchAgent = new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (initialStates != InitialStatesEnum.DUMMY_SEQUENTIAL) {
                    printData("\t", "\t", "\t", "\t");
                }
                printData(
                        SensorsBoard.TOTAL_COST.toString(),
                        SensorsBoard.TOTAL_INFORMATION.toString(),
                        searchAgent.getInstrumentation().getProperty("nodesExpanded"),
                        time.toString()
                );
            }
            printData("\n", "\n", "\n", "\n");
        }
        closeWriters();
    }

    /* Experiment 3 */
    static void parameters() throws Exception {
        String filePath = "experiments/parameters/";
        generateBufferedWriters(filePath, "Cost", "Information", "Time");
        bufferedWriters[0].append("Initial Cost\tCost\tTotal iterations\tPartial iterations\tk\tlambda\n");
        bufferedWriters[1].append("Information\tTotal iterations\tPartial iterations\tk\tlambda\n");
        bufferedWriters[2].append("Time\tTotal iterations\tPartial iterations\tk\tlambda\n");

        SensorsBoard.NUMBER_CENTERS = 4;
        SensorsBoard.NUMBER_SENSORS = 100;
        SensorsBoard.INFORMATION_WEIGHT = 2.5;
        SensorsSuccessorsHC.CHOSEN_OPERATOR = OperatorsEnum.SWITCH;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; i++) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            for (int it = 0; it < REPLICATIONS; it++) {
                for (int itRep = 0; itRep < REPLICATIONS; itRep++) {
                    for (int k = 0; k < 4; k++) {
                        for (int lambda = 0; lambda < 4; lambda++) {
                            SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);
                            String initialCost = String.valueOf(SensorsBoard.TOTAL_COST);

                            Problem p = new Problem(board, new SensorsSuccessorsSA(), new SensorsGoal(), new SensorsHeuristic());

                            Search alg = new SimulatedAnnealingSearch(1000 + 1000 * it, 100 + 100 * itRep, (int) (Math.pow(5, k)), 0.001 * Math.pow(10, lambda));

                            Long time = System.currentTimeMillis();
                            new SearchAgent(p, alg);
                            time = System.currentTimeMillis() - time;

                            String totalIterations = String.valueOf(1000 + 1000 * it);
                            String partialIterations = String.valueOf(100 + 100 * itRep);
                            String kVal = String.valueOf(Math.pow(5, k));
                            String lVal = String.valueOf(0.001 * Math.pow(10, lambda));
                            printData(
                                    initialCost + "\t" + SensorsBoard.TOTAL_COST + "\t" + totalIterations + "\t" + partialIterations + "\t" + kVal + "\t" + lVal + "\n",
                                    SensorsBoard.TOTAL_INFORMATION + "\t" + totalIterations + "\t" + partialIterations + "\t" + kVal + "\t" + lVal + "\n",
                                    time.toString() + "\t" + totalIterations + "\t" + partialIterations + "\t" + kVal + "\t" + lVal + "\n"
                            );
                        }
                    }
                }
            }
        }
        closeWriters();
    }

    /* Experiment 4 */
    static void increments() throws Exception {
        String filePath = "experiments/increments/";
        generateBufferedWriters(filePath, "DataHC", "DataSA");
        bufferedWriters[0].append("Sensors\tCenters\tCost\tInformation\tTime\tUC\n");
        bufferedWriters[1].append("Sensors\tCenters\tTime\n");

        SensorsBoard.INFORMATION_WEIGHT = 2.5;
        SensorsSuccessorsHC.CHOSEN_OPERATOR = OperatorsEnum.SWITCH;

        Integer incrementSensors = 50;
        Integer incrementCenters = 2;
        Integer numSensors = 100;
        Integer numCenters = 4;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; ++i) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            for (int j = 0; j < REPLICATIONS; j++) {
                Long time = System.currentTimeMillis();
                SensorsBoard.NUMBER_CENTERS = numCenters + j * incrementCenters;
                SensorsBoard.NUMBER_SENSORS = numSensors + j * incrementSensors;

                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

                Problem p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;
                String dataHC = String.valueOf(numSensors + j * incrementSensors) + "\t" + String.valueOf(numCenters + j * incrementCenters)
                        + "\t" + SensorsBoard.TOTAL_COST.toString() + "\t" + SensorsBoard.TOTAL_INFORMATION.toString() + '\t'
                        + time.toString() + "\t" + SensorsBoard.USED_CENTERS + "\n";

                time = System.currentTimeMillis();
                board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

                p = new Problem(board, new SensorsSuccessorsSA(), new SensorsGoal(), new SensorsHeuristic());
                alg = new SimulatedAnnealingSearch();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                printData(dataHC, String.valueOf(numSensors + j * incrementSensors) + "\t" +
                        String.valueOf(numCenters + j * incrementCenters) + "\t" + time.toString() + "\n");
            }
        }
        closeWriters();
    }

    /* Experiment 6 */
    static void dataCenters() throws Exception {
        String filePath = "experiments/dataCenters/";
        generateBufferedWriters(filePath, "HillClimbing", "SimulatedAnnealing");
        for (BufferedWriter bufferedWriter : bufferedWriters) {
            bufferedWriter.append("Cost1\tTime1\tUC1\tCost2\tTime2\tUC2\tCost3\tTime3\tUC3\tCost4\tTime4\tUC4\n");
        }

        SensorsBoard.NUMBER_SENSORS = 100;
        SensorsBoard.INFORMATION_WEIGHT = 2.5;
        SensorsSuccessorsHC.CHOSEN_OPERATOR = OperatorsEnum.SWITCH;

        Integer incrementCenters = 2;
        Integer numCenters = 4;

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

                String hillClimbingData = SensorsBoard.TOTAL_COST + "\t" + time.toString() + "\t" + SensorsBoard.USED_CENTERS;

                time = System.currentTimeMillis();
                board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

                p = new Problem(board, new SensorsSuccessorsSA(), new SensorsGoal(), new SensorsHeuristic());
                alg = new SimulatedAnnealingSearch();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                if (j != 0) {
                    printData("\t", "\t");
                }
                printData(hillClimbingData, SensorsBoard.TOTAL_COST + "\t" + time.toString() + "\t" + SensorsBoard.USED_CENTERS);
            }
            printData("\n", "\n", "\n");
        }
        closeWriters();
    }

    /* Experiment 7 */
    static void heuristic() throws Exception {
        String filePath = "experiments/heuristic/";
        generateBufferedWriters(filePath, "Data");
        bufferedWriters[0].append("Weight\tCost\tInformation\tTime\n");

        SensorsBoard.NUMBER_SENSORS = 100;
        SensorsBoard.NUMBER_CENTERS = 2;
        SensorsSuccessorsHC.CHOSEN_OPERATOR = OperatorsEnum.SWITCH;

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; ++i) {
            SensorsBoard.SEED_CENTERS = random.nextInt();
            SensorsBoard.SEED_SENSORS = random.nextInt();

            for (int j = 0; j < REPLICATIONS; j++) {
                SensorsBoard.INFORMATION_WEIGHT = 1 + j * 0.2;
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

                Problem p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());

                Search alg = new HillClimbingSearch();
                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                printData(SensorsBoard.INFORMATION_WEIGHT + "\t" + SensorsBoard.TOTAL_COST + "\t"
                        + SensorsBoard.TOTAL_INFORMATION + "\t" + time.toString() + "\n");
            }

            SensorsBoard.INFORMATION_WEIGHT = 2.5;
            SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);

            Problem p = new Problem(board, new SensorsSuccessorsHC(), new SensorsGoal(), new SensorsHeuristic());

            Search alg = new HillClimbingSearch();
            Long time = System.currentTimeMillis();
            new SearchAgent(p, alg);
            time = System.currentTimeMillis() - time;

            printData(SensorsBoard.INFORMATION_WEIGHT + "\t" + SensorsBoard.TOTAL_COST + "\t"
                    + SensorsBoard.TOTAL_INFORMATION + "\t" + time.toString() + "\n");
        }
        closeWriters();
    }

    /*----------*/

    private static void generateBufferedWriters(String filePath, String... fileNames) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path);
        bufferedWriters = new BufferedWriter[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            bufferedWriters[i] = new BufferedWriter(new FileWriter(filePath + fileNames[i] + ".txt"));
        }
    }

    private static void closeWriters() throws IOException {
        for (BufferedWriter bufferedWriter : bufferedWriters) {
            bufferedWriter.close();
        }
        bufferedWriters = null;
    }

    private static void printData(String... data) throws IOException {
        assert data.length == bufferedWriters.length;
        for (int i = 0; i < bufferedWriters.length; i++) {
            bufferedWriters[i].append(data[i]);
        }
    }

}
