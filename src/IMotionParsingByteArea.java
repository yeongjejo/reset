public class IMotionParsingByteArea {
    private int startByteNum;
    private int endByteNum;
    private SensorPart sensorPart;


    public IMotionParsingByteArea(int startByteNum, int endByteNum, SensorPart sensorPart) {
        this.startByteNum = startByteNum;
        this.endByteNum = endByteNum;
        this.sensorPart = sensorPart;
    }

    public int getStartByteNum() {
        return startByteNum;
    }

    public void setStartByteNum(int startByteNum) {
        this.startByteNum = startByteNum;
    }

    public int getEndByteNum() {
        return endByteNum;
    }

    public void setEndByteNum(int endByteNum) {
        this.endByteNum = endByteNum;
    }

    public SensorPart getSensorPart() {
        return sensorPart;
    }

    public void setSensorPart(SensorPart sensorPart) {
        this.sensorPart = sensorPart;
    }


}
