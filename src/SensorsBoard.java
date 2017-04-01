import IA.Red.Centro;
import IA.Red.CentrosDatos;
import IA.Red.Sensor;
import IA.Red.Sensores;
import Utils.InitialStatesEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the board with sensors and data centers information.
 */
class SensorsBoard {

    static Integer NUMBER_SENSORS;
    static Integer NUMBER_CENTERS;
    static Integer SEED_SENSORS;
    static Integer SEED_CENTERS;
    static Double INFORMATION_WEIGHT;

    static Double TOTAL_COST;
    static Double TOTAL_INFORMATION;
    static Integer USED_CENTERS;

    private static final Integer MAX_SENSOR_CONNECTIONS = 3;
    private static final Integer MAX_DATA_CENTER_CONNECTIONS = 25;

    private static List<Sensor> sensorList;
    private static List<Centro> centerList;

    private Double totalInformation;
    private Double totalCost;

    private List<SensorNode> sensorConnections;

    /**
     * Default constructor
     */
    SensorsBoard(InitialStatesEnum initialState) {
        sensorList = new ArrayList<>();
        centerList = new ArrayList<>();

        generateBoard();
        sensorConnections = new ArrayList<>(sensorList.size());

        switch (initialState) {
            case DUMMY_SEQUENTIAL:
                generateDummySequentialInitialState();
                break;
            case SIMPLE_GREEDY:
                generateSimpleGreedyInitialState();
                break;
            case DISTANCE_GREEDY:
                generateDistanceGreedyInitialState();
                break;
            default:
                System.out.println("Use args DS, SG or DG to choose the initial state generation method");
                System.exit(2);
        }
    }

    /**
     * Child state constructor
     *
     * @param board Parent state board.
     */
    SensorsBoard(SensorsBoard board) {
        sensorConnections = new ArrayList<>();
        for (SensorNode sensorNode : board.sensorConnections) {
            sensorConnections.add(new SensorNode(sensorNode));
        }

        totalCost = board.totalCost;
        totalInformation = board.totalInformation;
    }

    /**
     * Generates random board with N sensors and N data centers given a seed.
     */
    private void generateBoard() {
        sensorList.addAll(new Sensores(NUMBER_SENSORS, SEED_SENSORS));
        centerList.addAll(new CentrosDatos(NUMBER_CENTERS, SEED_CENTERS));
    }

    /*---- INITIAL STATES ----*/

    /**
     * Initial state generation with random sequential connections.
     */
    private void generateDummySequentialInitialState() {
        for (int i = 0; i < sensorList.size() - 1; i++) {
            List<Integer> inputs = new ArrayList<>();
            if (i > 0) {
                inputs.add(i - 1);
            }
            sensorConnections.add(new SensorNode(i + 1, inputs, 0D, 0D));
        }
        List<Integer> inputs = new ArrayList<>();
        inputs.add(sensorList.size() - 2);
        sensorConnections.add(new SensorNode(sensorList.size(), inputs, 0D, 0D));

        for (int i = 0; i < centerList.size(); i++) {
            inputs = new ArrayList<>();
            if (i == 0) {
                inputs.add(sensorList.size() - 1);
            }
            sensorConnections.add(new SensorNode(null, inputs, 0D, 0D));
        }

        recalculateBoardData();
    }

    /**
     * Initial state generation with greedy capacity sorting strategy.
     */
    private void generateSimpleGreedyInitialState() {
        sensorList.sort((sensor1, sensor2) -> ((Double) (sensor2.getCapacidad() - sensor1.getCapacidad())).intValue());

        for (int i = 0; i < getProblemSize(); i++) {
            List<Integer> inputs = new ArrayList<>();
            sensorConnections.add(new SensorNode(null, inputs, 0D, 0D));
        }

        Integer currentCenter = sensorList.size();
        Integer sensorID = 0;
        for (; sensorID < sensorList.size(); sensorID++) {
            if (sensorConnections.get(currentCenter).getInputsCount() >= MAX_DATA_CENTER_CONNECTIONS) {
                currentCenter++;
            }
            if (currentCenter < getProblemSize()) {
                sensorConnections.get(sensorID).setOutputSensor(currentCenter);
                sensorConnections.get(currentCenter).addInput(sensorID);
            } else {
                break;
            }
        }

        Integer currentSensor = 0;
        for (; sensorID < sensorList.size(); sensorID++) {
            if (sensorConnections.get(currentSensor).getInputsCount() >= MAX_SENSOR_CONNECTIONS) {
                currentSensor++;
            } else {
                sensorConnections.get(sensorID).setOutputSensor(currentSensor);
                sensorConnections.get(currentSensor).addInput(sensorID);
            }
        }

        recalculateBoardData();
    }

    /**
     * Initial state generation with distance priority greedy capacity sorting strategy.
     */
    private void generateDistanceGreedyInitialState() {
        sensorList.sort((sensor1, sensor2) -> ((Double) (sensor2.getCapacidad() - sensor1.getCapacidad())).intValue());

        for (int i = 0; i < getProblemSize(); i++) {
            List<Integer> inputs = new ArrayList<>();
            sensorConnections.add(new SensorNode(null, inputs, 0D, 0D));
        }

        List<Integer> availableNodes = new ArrayList<>();
        for (Integer currentCenter = sensorList.size(); currentCenter < getProblemSize(); currentCenter++) {
            availableNodes.add(currentCenter);
        }

        Integer sensorID = 0;
        while (!availableNodes.isEmpty() && sensorID < sensorList.size()) {
            Integer bestNode = availableNodes.get(0);
            Double bestDistance = Double.MAX_VALUE;
            for (int i = 0; i < availableNodes.size(); i++) {
                Integer availableNode = availableNodes.get(i);
                if (sensorConnections.get(availableNode).getInputsCount() >= MAX_DATA_CENTER_CONNECTIONS) {
                    availableNodes.remove(i);
                } else {
                    Double distance = calculateDistance(sensorID, availableNode);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestNode = availableNode;
                    }
                }
            }

            sensorConnections.get(sensorID).setOutputSensor(bestNode);
            sensorConnections.get(bestNode).addInput(sensorID);
            sensorID++;
        }

        availableNodes = new ArrayList<>();
        for (Integer i = 0; i < getProblemSize() && sensorList.get(i).getCapacidad() == 5; i++) {
            availableNodes.add(i);
        }

        Integer sensorCapacityAux = 2;
        for (; sensorID < sensorList.size(); sensorID++) {
            if (availableNodes.isEmpty()) {
                for (Integer i = 0; i < getProblemSize() && sensorList.get(i).getCapacidad() == sensorCapacityAux; i++) {
                    availableNodes.add(i);
                }
                sensorCapacityAux--;
            }

            Integer bestNode = availableNodes.get(0);
            Double bestDistance = Double.MAX_VALUE;
            for (int i = 0; i < availableNodes.size(); i++) {
                Integer availableNode = availableNodes.get(i);
                if (sensorConnections.get(availableNode).getInputsCount() < MAX_SENSOR_CONNECTIONS) {
                    Double distance = calculateDistance(sensorID, availableNode);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestNode = availableNode;
                    }
                } else {
                    availableNodes.remove(i);
                }
            }

            sensorConnections.get(sensorID).setOutputSensor(bestNode);
            sensorConnections.get(bestNode).addInput(sensorID);
        }

        recalculateBoardData();
    }

    /**
     * Recalculates board data resetting all nodes.
     */
    private void recalculateBoardData() {
        Integer currentCenter;
        for (currentCenter = sensorList.size(); currentCenter < getProblemSize(); currentCenter++) {
            Pair<Double, Double> data = calculateData(currentCenter, sensorConnections.get(currentCenter).getInputs());
            sensorConnections.get(currentCenter).setInformation(data.getInformation());
            sensorConnections.get(currentCenter).setCost(data.getCost());
        }
        recalculateTotalValues();
    }

    /*---- OPERATORS ----*/

    /**
     * Swaps output connection from first sensor to a second one.
     *
     * @param first  Sensor from that we will swap its output connection.
     * @param second Sensor that will receive the new input connection.
     * @return If connection has been switched.
     */
    Boolean switchConnection(int first, int second) {
        Integer oldEndConnection = sensorConnections.get(first).getOutputSensor();
        if (second == oldEndConnection || !isAllowedConnection(first, second, 0)) {
            return false;
        }

        for (int i = 0; i < sensorConnections.get(oldEndConnection).getInputsCount(); i++) {
            if (sensorConnections.get(oldEndConnection).getInput(i) == first) {

                sensorConnections.get(oldEndConnection).removeInput(i);
                sensorConnections.get(first).setOutputSensor(second);
                sensorConnections.get(second).addInput(first);

                recalculateDataUpstreamFromNode(oldEndConnection);
                recalculateDataUpstreamFromNode(second);
                recalculateTotalValues();

                return true;
            }
        }
        return false;
    }

    /**
     * Switch output connections between two sensors.
     *
     * @param first  First sensor to connect the second one output.
     * @param second Second sensor to connect the first one output.
     * @return If connection has been swapped.
     */
    Boolean swapConnection(int first, int second) {
        int oldEndFirstConnection = sensorConnections.get(first).getOutputSensor();
        int oldEndSecondConnection = sensorConnections.get(second).getOutputSensor();
        if (first == oldEndSecondConnection || second == oldEndFirstConnection ||
                oldEndFirstConnection == oldEndSecondConnection ||
                !isAllowedConnection(first, oldEndSecondConnection, 1) ||
                !isAllowedConnection(second, oldEndFirstConnection, 1)) {
            return false;
        }

        int secondIndex = 0;
        for (Integer secondInputID : sensorConnections.get(oldEndSecondConnection).getInputSensors()) {
            if (secondInputID == second) {
                int firstIndex = 0;
                for (Integer firstInputID : sensorConnections.get(oldEndFirstConnection).getInputSensors()) {
                    if (firstInputID == first) {
                        sensorConnections.get(first).setOutputSensor(oldEndSecondConnection);
                        sensorConnections.get(second).setOutputSensor(oldEndFirstConnection);

                        sensorConnections.get(oldEndSecondConnection).removeInput(secondIndex);
                        sensorConnections.get(oldEndSecondConnection).addInput(first);

                        sensorConnections.get(oldEndFirstConnection).removeInput(firstIndex);
                        sensorConnections.get(oldEndFirstConnection).addInput(second);

                        recalculateDataUpstreamFromNode(oldEndFirstConnection);
                        recalculateDataUpstreamFromNode(oldEndSecondConnection);
                        recalculateTotalValues();

                        return true;
                    }
                    firstIndex++;
                }
            }
            secondIndex++;
        }

        return false;
    }

    /*-------------- HEURISTICS --------------*/

    /**
     * Heuristic based on cost and information.
     *
     * @return Heuristic value.
     */
    Double superHeuristic() {
        return totalCost - Math.pow(totalInformation, INFORMATION_WEIGHT);
    }

    /*-------------- Goal functions --------------*/

    /**
     * Checks if it's a goal solution state.
     *
     * @return Is a goal state.
     */
    boolean isGoal() {
        return false;
    }

    /*-------------- Auxiliary classes and functions --------------*/

    /**
     * Recursive calculation of information and cost
     *
     * @param inputs List of input sensors to iterate.
     */
    private Pair<Double, Double> calculateData(Integer nodeID, List<Integer> inputs) {
        if (inputs.isEmpty()) {
            // Leaf has no cost and its own information
            sensorConnections.get(nodeID).setCost(0D);
            Double information = nodeID < sensorList.size() ? sensorList.get(nodeID).getCapacidad() : 0D;
            sensorConnections.get(nodeID).setInformation(information);

            // Return information
            Double distance = nodeID < sensorList.size() ? calculateOutputDistance(nodeID) : 0D;
            return new Pair<>(information, distance * information);
        }

        // Reset node information
        Double information = nodeID < sensorList.size() ? sensorList.get(nodeID).getCapacidad() : 0D;
        sensorConnections.get(nodeID).setInformation(information);
        sensorConnections.get(nodeID).setCost(0D);

        // Refresh children data
        for (Integer input : inputs) {
            Pair<Double, Double> childData = calculateData(input, sensorConnections.get(input).getInputs());
            sensorConnections.get(nodeID).addInformation(childData.getInformation());
            sensorConnections.get(nodeID).addCost(childData.getCost());
        }

        // Return modified data
        SensorNode currentNode = sensorConnections.get(nodeID);
        Double cost = nodeID < sensorList.size() ? calculateOutputDistance(nodeID) : 0D;
        if (nodeID < sensorList.size()) {
            information = Math.min(3 * sensorList.get(nodeID).getCapacidad(), currentNode.getInformation());
            cost = (cost * information) + currentNode.getCost();
            return new Pair<>(information, cost);
        } else {
            return new Pair<>(currentNode.getInformation(), currentNode.getCost());
        }
    }

    /**
     * Upstream calculation of information and cost from an specific node.
     *
     * @param nodeID Node from where to start.
     */
    private void recalculateDataUpstreamFromNode(Integer nodeID) {
        SensorNode currentNode = sensorConnections.get(nodeID);
        currentNode.setInformation(nodeID < sensorList.size() ? sensorList.get(nodeID).getCapacidad() : 0D);
        currentNode.setCost(0D);
        for (Integer inputID : currentNode.getInputSensors()) {
            Double informationTransmitted = Math.min(3 * sensorList.get(inputID).getCapacidad(), sensorConnections.get(inputID).getInformation());
            currentNode.addInformation(informationTransmitted);
            currentNode.addCost(sensorConnections.get(inputID).getCost() + (informationTransmitted * calculateOutputDistance(inputID)));
        }
        Integer nextNode = sensorConnections.get(nodeID).getOutputSensor();
        if (nextNode != null) {
            recalculateDataUpstreamFromNode(nextNode);
        }
    }

    private void recalculateTotalValues() {
        Integer currentCenter;
        totalInformation = 0D;
        totalCost = 0D;
        USED_CENTERS = 0;
        for (currentCenter = sensorList.size(); currentCenter < getProblemSize(); currentCenter++) {
            totalInformation += sensorConnections.get(currentCenter).getInformation();
            totalCost += sensorConnections.get(currentCenter).getCost();
            if (sensorConnections.get(currentCenter).getInputsCount() != 0) {
                USED_CENTERS++;
            }
        }
        TOTAL_INFORMATION = totalInformation;
        TOTAL_COST = totalCost;
    }

    /**
     * Calculate distance from a sensor to a output node.
     *
     * @param sensorID Sensor unique identifier.
     * @return Output cost.
     */
    private Double calculateOutputDistance(Integer sensorID) {
        Integer outputID = sensorConnections.get(sensorID).getOutputSensor();
        Integer xOutput = outputID < sensorList.size()
                ? sensorList.get(outputID).getCoordX()
                : centerList.get(outputID % sensorList.size()).getCoordX();
        Integer yOutput = outputID < sensorList.size()
                ? sensorList.get(outputID).getCoordY()
                : centerList.get(outputID % sensorList.size()).getCoordY();
        return (Math.pow(sensorList.get(sensorID).getCoordX() - xOutput, 2)
                + Math.pow(sensorList.get(sensorID).getCoordY() - yOutput, 2));
    }

    /**
     * Calculate distance between two nodes
     *
     * @param firstSensor Sensor unique identifier.
     * @param secondNode  Node unique identifier.
     */
    private Double calculateDistance(Integer firstSensor, Integer secondNode) {
        Integer xOutput = secondNode < sensorList.size()
                ? sensorList.get(secondNode).getCoordX()
                : centerList.get(secondNode % sensorList.size()).getCoordX();
        Integer yOutput = secondNode < sensorList.size()
                ? sensorList.get(secondNode).getCoordY()
                : centerList.get(secondNode % sensorList.size()).getCoordY();
        return Math.sqrt(Math.pow(sensorList.get(firstSensor).getCoordX() - xOutput, 2)
                + Math.pow(sensorList.get(firstSensor).getCoordY() - yOutput, 2));
    }

    /**
     * Checks if a a connection between two sensors can be established.
     *
     * @param first  Sensor from that will be an output connection.
     * @param second Sensor that will receive the input connection.
     * @return If the connection can be performed.
     */
    private Boolean isAllowedConnection(int first, int second, int margin) {
        Integer maxConnections = second < sensorList.size() ? MAX_SENSOR_CONNECTIONS : MAX_DATA_CENTER_CONNECTIONS;
        if (sensorConnections.get(second).getInputsCount() >= maxConnections + margin) {
            return false;
        }

        // Already connected second -> first (cycle)
        Integer nextSensor = sensorConnections.get(second).getOutputSensor();
        while (nextSensor != null) {
            if (nextSensor == first) {
                return false;
            }
            nextSensor = sensorConnections.get(nextSensor).getOutputSensor();
        }

        return true;
    }

    /**
     * Get used centers.
     *
     * @return Number of centers used.
     */
    Integer getUsedCenters() {
        Integer count = 0;
        for (int i = sensorList.size(); i < getProblemSize(); i++) {
            if (sensorConnections.get(i).getInputsCount() != 0) {
                ++count;
            }
        }
        return count;
    }

    /**
     * Gets the number of sensors.
     *
     * @return Number of sensors.
     */
    Integer getSensorsSize() {
        return sensorList.size();
    }

    /**
     * Gets problem size.
     *
     * @return Number of sensors and centers.
     */
    Integer getProblemSize() {
        return sensorList.size() + centerList.size();
    }

    /**
     * Auxiliary class to represent data transmission among nodes.
     *
     * @param <I> Information unit.
     * @param <C> Cost unit.
     */
    private class Pair<I, C> {

        private I information;
        private C cost;

        Pair(I information, C cost) {
            this.information = information;
            this.cost = cost;
        }

        I getInformation() {
            return information;
        }

        C getCost() {
            return cost;
        }

    }

}
