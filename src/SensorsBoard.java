import IA.Red.Centro;
import IA.Red.CentrosDatos;
import IA.Red.Sensor;
import IA.Red.Sensores;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the board with sensors and data centers information.
 */
class SensorsBoard {

    private Double totalInformation;
    private Double totalCost;

    private static final Integer NUMBER_SENSORS = 100;
    private static final Integer NUMBER_CENTERS = 4;
    private static final Integer GENERATOR_SEED = 1234;

    private static final Integer MAX_SENSOR_CONNECTIONS = 3;
    private static final Integer MAX_DATA_CENTER_CONNECTIONS = 25;

    private static List<Sensor> sensorList;
    private static List<Centro> centerList;

    private List<SensorNode> sensorConnections;

    static Double COST;
    static Double INFORMATION;

    /**
     * Default constructor
     */
    SensorsBoard() {
        sensorList = new ArrayList<>();
        centerList = new ArrayList<>();

        generateBoard();
        sensorConnections = new ArrayList<>(sensorList.size());

        //generateEmptyInitialState();
        //generateDummyInitialState();
        generateGreedyInitialState();
    }

    /**
     * Child state constructor
     *
     * @param board Parent state board.
     */
    SensorsBoard(SensorsBoard board) {

        sensorConnections = new ArrayList<>();
        for (int i = 0; i < getProblemSize(); i++) {
            List<Integer> inputs = new ArrayList<>();
            for (int j = 0; j < board.sensorConnections.get(i).getInputsCount(); j++) {
                inputs.add(board.sensorConnections.get(i).getInput(j));
            }
            sensorConnections.add(new SensorNode(board.sensorConnections.get(i).getOutputSensor(), inputs));
        }

        totalCost = board.getTotalCost();
        totalInformation = board.getTotalInformation();
    }

    /**
     * Generates random board with N sensors and N data centers given a seed.
     */
    private void generateBoard() {
        sensorList.addAll(new Sensores(NUMBER_SENSORS, GENERATOR_SEED));
        centerList.addAll(new CentrosDatos(NUMBER_CENTERS, GENERATOR_SEED));
    }

    /*---- INITIAL STATES ----*/

    /**
     * Initial state generation with random sequential connections.
     */
    @SuppressWarnings("unused")
    private void generateDummyInitialState() {
        for (int i = 0; i < sensorList.size() - 1; i++) {
            List<Integer> inputs = new ArrayList<>();
            if (i > 0) {
                inputs.add(i - 1);
            }
            sensorConnections.add(new SensorNode(i + 1, inputs));
        }
        List<Integer> inputs = new ArrayList<>();
        inputs.add(sensorList.size() - 2);
        sensorConnections.add(new SensorNode(sensorList.size(), inputs));

        for (int i = 0; i < centerList.size(); i++) {
            inputs = new ArrayList<>();
            if (i == 0) {
                inputs.add(sensorList.size() - 1);
            }
            sensorConnections.add(new SensorNode(null, inputs));
        }
    }

    /**
     * Initial state generation with greedy capacity sorting strategy.
     */
    private void generateGreedyInitialState() {

        sensorList.sort((sensor1, sensor2) -> ((Double) (sensor2.getCapacidad() - sensor1.getCapacidad())).intValue());

        for (int i = 0; i < getProblemSize(); i++) {
            List<Integer> inputs = new ArrayList<>();
            sensorConnections.add(new SensorNode(null, inputs));
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

        totalCost = 0D;
        totalInformation = 0D;
        for (currentCenter = sensorList.size(); currentCenter < getProblemSize(); currentCenter++) {
            Pair<Double, Double> data = calculateData(currentCenter, sensorConnections.get(currentCenter).getInputs());
            sensorConnections.get(currentCenter).setInformation(data.getInformation());
            sensorConnections.get(currentCenter).setCost(data.getCost());

            totalInformation += data.getInformation();
            totalCost += data.getCost();
        }
    }

    /*---- OPERATORS ----*/

    /**
     * Swaps output connection from first sensor to a second one.
     *
     * @param first  Sensor from that we will swap its output connection.
     * @param second Sensor that will receive the new input connection.
     * @return If connection has been swapped.
     */
    Boolean swapConnection(int first, int second) {
        Integer oldEndConnection = sensorConnections.get(first).getOutputSensor();
        if (second == oldEndConnection || !isAllowedConnection(first, second)) {
            return false;
        }

        for (int i = 0; i < sensorConnections.get(oldEndConnection).getInputsCount(); i++) {
            if (sensorConnections.get(oldEndConnection).getInput(i) == first) {

                // Subtract old upstream connections cost and information.
                Double oldCost = sensorConnections.get(first).getCost();
                Double oldInformation = sensorConnections.get(first).getInformation();
                Double oldDistance = calculateOutputDistance(first);

                Integer parent = sensorConnections.get(first).getOutputSensor();
                while (parent != null) {
                    sensorConnections.get(parent).addInformation(-oldCost);
                    sensorConnections.get(parent).addInformation(-oldInformation);
                    //TODO: Sensor supera limit info -> no restar tot
                    //TODO: Al totalitzar, guardar total i pujar min(maxVal, total)

                    parent = sensorConnections.get(parent).getOutputSensor();
                }

                sensorConnections.get(oldEndConnection).removeInput(i);
                sensorConnections.get(first).setOutputSensor(second);
                sensorConnections.get(second).addInput(first);

                // Add new upstream connections cost and information.
                Double information = sensorConnections.get(first).getInformation();
                Double distanceDifference = calculateOutputDistance(first) - oldDistance;
                sensorConnections.get(first).addCost(distanceDifference);

                parent = sensorConnections.get(first).getOutputSensor();
                while (parent != null) {
                    sensorConnections.get(parent).addCost(distanceDifference);

                    Double infoAux = parent < sensorList.size()
                            ? Math.min(2 * sensorList.get(parent).getCapacidad(), information + sensorConnections.get(parent).getInformation())
                            : information + sensorConnections.get(parent).getInformation();
                    sensorConnections.get(parent).addInformation(infoAux);
                    sensorConnections.get(parent).addCost(sensorConnections.get(first).getCost());
                    //TODO: No tira

                    parent = sensorConnections.get(parent).getOutputSensor();
                }


                return true;
            }
        }
        return false;
    }

    /*-------------- HEURISTICS --------------*/

    /**
     * Heuristic based on cost.
     *
     * @return Heuristic value.
     */
    Double costHeuristic() {
        return totalCost;
    }

    /**
     * Heuristic based on information.
     *
     * @return Heuristic value.
     */
    Double informationHeuristic() {
        return totalInformation;
    }

    /**
     * Heuristic based on cost and information.
     *
     * @return Heuristic value.
     */
    Double superHeuristic() {
        return totalCost - Math.pow(totalInformation, 3);
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
            // Leaf has no cost.
            sensorConnections.get(nodeID).setCost(0D);
            // Leaf information is its own information.
            Double information = nodeID < sensorList.size() ? sensorList.get(nodeID).getCapacidad() : 0D;
            sensorConnections.get(nodeID).setCost(information);

            Double distance = nodeID < sensorList.size() ? calculateOutputDistance(nodeID) : 0D;
            return new Pair<>(information, information * distance);
        }

        SensorNode currentNode = sensorConnections.get(nodeID);
        for (Integer input : inputs) {
            Pair<Double, Double> aux = calculateData(input, sensorConnections.get(input).getInputs());

            sensorConnections.get(nodeID).setInformation(currentNode.getInformation() + aux.getInformation());
            sensorConnections.get(nodeID).setCost(currentNode.getCost() + aux.getCost());
        }

        Double distance = nodeID < sensorList.size() ? calculateOutputDistance(nodeID) : 0D;
        return new Pair<>(currentNode.getInformation(), currentNode.getCost() + (currentNode.getInformation() * distance));
    }

    private Double calculateOutputDistance(Integer sensorID) {
        Integer outputID = sensorConnections.get(sensorID).getOutputSensor();
        Integer xOutput = outputID < sensorList.size() ? sensorList.get(outputID).getCoordX() : centerList.get(outputID % sensorList.size()).getCoordX();
        Integer yOutput = outputID < sensorList.size() ? sensorList.get(outputID).getCoordY() : centerList.get(outputID % sensorList.size()).getCoordY();
        return (Math.pow(sensorList.get(sensorID).getCoordX() - xOutput, 2)
                + Math.pow(sensorList.get(sensorID).getCoordY() - yOutput, 2));
    }

    /**
     * Checks if a a connection between two sensors can be established.
     *
     * @param first  Sensor from that will be an output connection.
     * @param second Sensor that will receive the input connection.
     * @return If the connection can be performed.
     */
    private Boolean isAllowedConnection(int first, int second) {
        Integer maxConnections = second < sensorList.size() ? MAX_SENSOR_CONNECTIONS : MAX_DATA_CENTER_CONNECTIONS;
        if (sensorConnections.get(second).getInputsCount() >= maxConnections) {
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
     * Gets problem distance cost.
     *
     * @return Cost of the current configuration.
     */
    private Double getTotalCost() {
        return totalCost;
    }

    /**
     * Gets problem information cost.
     *
     * @return Information value of the current configuration.
     */
    private Double getTotalInformation() {
        return totalInformation;
    }

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
