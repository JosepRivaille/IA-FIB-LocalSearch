import Utils.InitialStatesEnum;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

class Experiments {

    private static final int REPLICATIONS = 10;

    private static BufferedWriter writerTime;
    private static BufferedWriter writerCost;
    private static BufferedWriter writerInfo;

    static void operators() throws Exception {
        String filePath = "experiments/operators/";
        generateBufferedWriters(filePath);

        writerTime.write("Switch\tBoth\tSwap\t\n");
        writerCost.write("Switch\tBoth\tSwap\t\n");
        writerInfo.write("Switch\tBoth\tSwap\t\n");

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; i++) {
            Integer seedSensors = random.nextInt();
            Integer seedCenters = random.nextInt();

            for (int j = -1; j < 2; j++) {
                SensorsBoard board = new SensorsBoard(InitialStatesEnum.DISTANCE_GREEDY);
                SensorsBoard.SEED_CENTERS = seedCenters;
                SensorsBoard.SEED_SENSORS = seedSensors;

                Problem p = new Problem(board, new SensorsSuccessorsHC(j), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();

                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                writerTime.append(time.toString()).append('\t');
                writerCost.append(SensorsBoard.COST.toString()).append('\t');
                writerInfo.append(SensorsBoard.INFORMATION.toString()).append('\t');
            }
            writerTime.append('\n');
            writerCost.append('\n');
            writerInfo.append('\n');
        }
        writerTime.close();
        writerCost.close();
        writerInfo.close();
    }

    static void initialStates() throws Exception {
        String filePath = "experiments/initialStates/";
        generateBufferedWriters(filePath);

        writerTime.write("Dummy Sequential\tSimple Greedy\tDistance Greedy\t\n");
        writerCost.write("Dummy Sequential\tSimple Greedy\tDistance Greedy\t\n");
        writerInfo.write("Dummy Sequential\tSimple Greedy\tDistance Greedy\t\n");

        Random random = new Random();
        for (int i = 0; i < REPLICATIONS; i++) {
            Integer seedSensors = random.nextInt();
            Integer seedCenters = random.nextInt();

            for (InitialStatesEnum initialStates : InitialStatesEnum.values()) {
                SensorsBoard board = new SensorsBoard(initialStates);
                SensorsBoard.SEED_CENTERS = seedCenters;
                SensorsBoard.SEED_SENSORS = seedSensors;

                Problem p = new Problem(board, new SensorsSuccessorsHC(-1), new SensorsGoal(), new SensorsHeuristic());
                Search alg = new HillClimbingSearch();

                Long time = System.currentTimeMillis();
                new SearchAgent(p, alg);
                time = System.currentTimeMillis() - time;

                writerTime.append(time.toString()).append('\t');
                writerCost.append(SensorsBoard.COST.toString()).append('\t');
                writerInfo.append(SensorsBoard.INFORMATION.toString()).append('\t');
            }

            writerTime.append('\n');
            writerCost.append('\n');
            writerInfo.append('\n');
        }
        writerTime.close();
        writerCost.close();
        writerInfo.close();
    }

    private static void generateBufferedWriters(String filePath) throws IOException {
        writerTime = new BufferedWriter(new FileWriter(filePath + "fileTime.txt"));
        writerCost = new BufferedWriter(new FileWriter(filePath + "fileCost.txt"));
        writerInfo = new BufferedWriter(new FileWriter(filePath + "fileInfo.txt"));
    }

}
