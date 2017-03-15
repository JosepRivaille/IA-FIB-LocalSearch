import IA.Red.Centro;
import IA.Red.CentrosDatos;
import IA.Red.Sensor;
import IA.Red.Sensores;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of the board with sensors and data centers information.
 */
public class SensorsBoard {

    private Double totalCost;
    private Double totalInformation;

    private static final Integer MAX_SENSOR_CONNECTIONS = 3;
    private static final Integer MAX_DATA_CENTER_CONNECTIONS = 25;

    private static List<Sensor> sensorList;
    private static List<Centro> centerList;

    private List<SensorConnections> sensorConnections;

    /**
     * Default constructor
     */
    SensorsBoard() {
        sensorList = new ArrayList<>();
        centerList = new ArrayList<>();

        generateBoard();
        sensorConnections = new ArrayList<>(sensorList.size());

        totalCost = 0D;
        totalInformation = 0D;

        generateEmptyInitialState();
    }

    /**
     * Child state constructor
     *
     * @param board Parent state board.
     */
    SensorsBoard(SensorsBoard board) {
        sensorConnections = new ArrayList<>(board.sensorConnections);
        totalCost = board.getTotalCost();
        totalInformation = board.getTotalInformation();
    }

    /**
     * Generates random board with N sensors and N data centers given a seed.
     */
    private void generateBoard() {
        // TODO: Random
        sensorList.addAll(new Sensores(100, 123));
        // TODO: Random
        centerList.addAll(new CentrosDatos(1, 123));
    }

    /*---- INITIAL STATES ----*/

    /**
     * Initial state generation without connections.
     */
    private void generateEmptyInitialState() {
        for (int i = 0; i < getProblemSize(); i++) {
            List<Integer> inputs = new ArrayList<>();
            sensorConnections.add(new SensorConnections(null, inputs));
        }
    }

    /**
     * Intial state generation with random sequential connections.
     */
    private void generateDummyInitialState() {
        for (int i = 0; i < sensorList.size() - 1; i++) {
            List<Integer> inputs = new ArrayList<>();
            if (i > 0) {
                inputs.add(i - 1);
            }
            sensorConnections.add(new SensorConnections(i + 1, inputs));
            //totalCost += calculateCost(first, sensorList.get(i), sensorList.get(i + 1));
            //totalInformation += calculateInformation(sensorList.get(i), sensorList.get(i + 1));
        }
        List<Integer> inputs = new ArrayList<>();
        inputs.add(sensorList.size() - 2);
        sensorConnections.add(new SensorConnections(null, inputs));
        //totalCost += calculateCost(first, sensorList.get(sensorList.size() - 2), sensorList.get(sensorList.size() - 1));
        //totalInformation += calculateInformation(sensorList.get(sensorList.size() - 2), sensorList.get(sensorList.size() - 1));
    }

    /*---- OPERATORS ----*/

    /**
     * Adds a connection between two sensors.
     *
     * @param first  Sensor from that will be an output connection.
     * @param second Sensor that will receive the input connection.
     * @return If connection has been added.
     */
    boolean addConnection(int first, int second) {
        if (isAllowedConnection(first, second)) {
            sensorConnections.get(second).inputSensors.add(first);
            sensorConnections.get(first).outputSensor = second;

            totalCost += second < sensorList.size()
                    ? calculateCost(first, sensorList.get(first), sensorList.get(second))
                    : calculateCostDataCenters(first, sensorList.get(first), centerList.get(sensorList.size() - second));

            totalInformation = calculateInformation();
            System.out.println(totalInformation);

            return true;
        }
        return false;
    }

    /**
     * Removes a connection between two sensors.
     *
     * @param first  Sensor from that we wil will be an output connection.
     * @param second Sensor that will receive the input connection.
     * @return If
     */
    boolean removeConnection(int first, int second) {
        for (int i = 0; i < sensorConnections.get(second).inputSensors.size(); ++i) {
            if (sensorConnections.get(second).inputSensors.get(i) == first) {
                sensorConnections.get(second).inputSensors.remove(i);
                sensorConnections.get(first).outputSensor = null;
                return true;
            }
        }
        return false;
    }

    // TODO: Peta fort
    public void swapConnection(int index) {

        //Double oldCost = calculateCost(first, sensorList.get(index), sensorList.get(sensorConnections.get(index).outputSensor));

        // Swap outputs
        int aux = sensorConnections.get(index).outputSensor;
        sensorConnections.get(index).outputSensor = sensorConnections.get((index + 1) % sensorList.size()).outputSensor;
        sensorConnections.get((index + 1) % sensorList.size()).outputSensor = aux;

        // Swap inputs
        List<Integer> aux2 = sensorConnections.get(index).inputSensors;
        sensorConnections.get(index).inputSensors = sensorConnections.get((index + 1) % sensorList.size()).inputSensors;
        sensorConnections.get((index + 1) % sensorList.size()).inputSensors = aux2;

        if (sensorConnections.get(index).outputSensor < sensorList.size()) {
            //totalCost += (calculateCost(first, sensorList.get(index), sensorList.get(sensorConnections.get(index).outputSensor)) - oldCost);
        }
    }

    /*---- HEURISTICS ----*/

    /**
     * Heuristic based on cost.
     *
     * @return Heuristic value.
     */
    Double costHeuristic() {
        return totalCost * -1D;
    }

    /**
     * Heuristic based on information.
     *
     * @return Heuristic value.
     */
    public double informationHeuristic() {
        return 1 / totalInformation;
    }

    /**
     * Heuristic based on cost and information.
     *
     * @return Heuristic value.
     */
    public double superHeuristic() {
        return totalCost - Math.pow(totalInformation, 2);
    }

    /*---- Goal functions ----*/

    /**
     * Checks if it's a goal solution state.
     *
     * @return Is a goal state.
     */
    boolean isGoal() {
        return false;
    }

    /*---- Auxiliary classes and functions ----*/

    /**
     * Representation of a sensor input and output connections.
     */
    private class SensorConnections {

        private Integer outputSensor;
        private List<Integer> inputSensors;

        SensorConnections(Integer outputSensor, List<Integer> inputSensors) {
            this.outputSensor = outputSensor;
            this.inputSensors = inputSensors;
        }
    }

    /**
     * Calculates the Euclidean distance between two sensors.
     *
     *
     * @param first
     * @param sensor1 First sensor with its coordinates.
     * @param sensor2 Second sensor with its coordinates.
     * @return Distance cost.
     */
    private Double calculateCost(int first, Sensor sensor1, Sensor sensor2) {
        return (sensor1.getCoordX() - sensor2.getCoordX()) * (sensor1.getCoordX() - sensor2.getCoordX())
                + (sensor1.getCoordY() - sensor2.getCoordY()) * (sensor1.getCoordY() - sensor2.getCoordY())
                + (sensor1.getCapacidad() + Math.max(sensor1.getCapacidad(), recursiveCalculateInformation(sensorConnections.get(first).inputSensors)));
    }

    private Double calculateCostDataCenters(int first, Sensor sensor, Centro dataCenter) {
        return Math.sqrt((sensor.getCoordX() - dataCenter.getCoordX()) * (sensor.getCoordX() - dataCenter.getCoordX())
                + (sensor.getCoordY() - dataCenter.getCoordY()) * (sensor.getCoordY() - dataCenter.getCoordY()))
                + (sensor.getCapacidad() + Math.max(sensor.getCapacidad(), recursiveCalculateInformation(sensorConnections.get(first).inputSensors)));
    }

    /**
     * Calculates information cost between two sensors.
     *
     * @return Information cost.
     */
    private Double calculateInformation() {
        Double totalInformation = 0D;
        for (int i = sensorList.size(); i < sensorConnections.size(); i++) {
            List<Integer> inputSensors = sensorConnections.get(i).inputSensors;
            totalInformation += recursiveCalculateInformation(inputSensors);
        }
        return totalInformation;
    }

    private Double recursiveCalculateInformation(List<Integer> sensors) {
        Double information = 0D;
        for (Integer sensorID : sensors) {
            Double capacity = sensorList.get(sensorID).getCapacidad();
            List<Integer> inputSensors = sensorConnections.get(sensorID).inputSensors;
            if (inputSensors.isEmpty()) {
                return capacity;
            }
            information += capacity + Math.max(2 * capacity, recursiveCalculateInformation(inputSensors));
        }
        return information;
    }

    /**
     * Checks if a a connection between two sensors can be established.
     *
     * @param first  Sensor from that will be an output connection.
     * @param second Sensor that will receive the input connection.
     * @return If the connection can be performed.
     */
    private boolean isAllowedConnection(int first, int second) {
        // Already 3 incoming connections or outgoing connection
        if (sensorConnections.get(second).inputSensors.size() == MAX_SENSOR_CONNECTIONS || sensorConnections.get(first).outputSensor != null) {
            return false;
        }

        // Already connected first -> second (redundant)
        Integer nextSensor = sensorConnections.get(first).outputSensor;
        while (nextSensor != null) {
            if (nextSensor == second) {
                return false;
            }
            nextSensor = sensorConnections.get(nextSensor).outputSensor;
        }

        // Already connected second -> first (cycle)
        nextSensor = sensorConnections.get(second).outputSensor;
        while (nextSensor != null) {
            if (nextSensor == first) {
                return false;
            }
            nextSensor = sensorConnections.get(nextSensor).outputSensor;
        }

        return true;
    }

    /**
     * Gets the problem size.
     *
     * @return Number of sensors.
     */
    int getSensorsSize() {
        return sensorList.size();
    }

    int getProblemSize() {
        return sensorList.size() + centerList.size();
    }

    /**
     * Gets problem distance cost.
     *
     * @return Cost of the current configuration.
     */
    Double getTotalCost() {
        return totalCost;
    }

    /**
     * Gets problem information cost.
     *
     * @return Information value of the current configuration.
     */
    Double getTotalInformation() {
        return totalInformation;
    }

}
