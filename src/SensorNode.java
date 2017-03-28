import java.util.ArrayList;
import java.util.List;

class SensorNode {

    private Double cost;
    private Double information;
    private Integer outputSensor;
    private List<Integer> inputSensors;

    /**
     * Node constructor with default value for costs as 0.
     *
     * @param outputSensor Outgoing sensorID.
     * @param inputSensors Incoming list of sensorIDs.
     */
    SensorNode(Integer outputSensor, List<Integer> inputSensors, Double cost, Double information) {
        this.outputSensor = outputSensor;
        this.inputSensors = inputSensors;
        this.cost = cost;
        this.information = information;
    }

    SensorNode(SensorNode sensorNode) {
        outputSensor = sensorNode.getOutputSensor();
        inputSensors = new ArrayList<>();
        for (Integer inputSensor : sensorNode.getInputSensors()) {
            inputSensors.add(inputSensor);
        }
        cost = sensorNode.getCost();
        information = sensorNode.getInformation();
    }

    @Override
    protected SensorNode clone() throws CloneNotSupportedException {
        return (SensorNode) super.clone();
    }

    Double getCost() {
        return cost;
    }

    void setCost(Double cost) {
        this.cost = cost;
    }

    void addCost(Double cost) {
        this.cost += cost;
    }

    void addInformation(Double information) {
        this.information += information;
    }

    Double getInformation() {
        return information;
    }

    void setInformation(Double information) {
        this.information = information;
    }

    Integer getOutputSensor() {
        return outputSensor;
    }

    void setOutputSensor(Integer outputSensor) {
        this.outputSensor = outputSensor;
    }

    List<Integer> getInputSensors() {
        return inputSensors;
    }

    Integer getInput(int index) {
        return inputSensors.get(index);
    }

    Integer getInputsCount() {
        return inputSensors.size();
    }

    void addInput(Integer sensorID) {
        inputSensors.add(sensorID);
    }

    void removeInput(int index) {
        inputSensors.remove(index);
    }

    List<Integer> getInputs() {
        return inputSensors;
    }
}
