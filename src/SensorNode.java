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
    SensorNode(Integer outputSensor, List<Integer> inputSensors) {
        cost = 0D;
        information = 0D;
        this.outputSensor = outputSensor;
        this.inputSensors = inputSensors;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Double getInformation() {
        return information;
    }

    public void setInformation(Double information) {
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

    public List<Integer> getInputs() {
        return inputSensors;
    }
}
