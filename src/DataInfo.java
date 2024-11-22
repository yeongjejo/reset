import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.ConcurrentHashMap;

// 싱글톤으로 구현
public class DataInfo {

    private byte header0;
    private byte header1;
    private int version;
//    private List<SensorData> sensorDataList = new ArrayList<>();
    private ConcurrentHashMap<String, SensorData> sensorDataMap = new ConcurrentHashMap<>();
    private FingerSensorData leftFingerSensorData;
    private FingerSensorData rightFingerSensorData;

    private Gson gson = new GsonBuilder().setPrettyPrinting().create(); // json 변환 용도
//    private Gson gson = new GsonBuilder().setPrettyPrinting().serializeSpecialFloatingPointValues().create(); // json 변환 용도

    private boolean resetting = false;


    // 싱글톤 용도
    private static DataInfo singletonObject;

    private DataInfo() {}

    public static DataInfo getInstance() {
        if (singletonObject == null) {
            singletonObject = new DataInfo();
        }

        return singletonObject;
    }


    // getter and setter
    public byte getHeader0() {
        return header0;
    }

    public void setHeader0(byte header0) {
        this.header0 = header0;
    }

    public byte getHeader1() {
        return header1;
    }

    public void setHeader1(byte header1) {
        this.header1 = header1;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public FingerSensorData getLeftFingerSensorData() {
        return leftFingerSensorData;
    }

    public void setLeftFingerSensorData(FingerSensorData leftFingerSensorData) {
        this.leftFingerSensorData = leftFingerSensorData;
    }

    public FingerSensorData getRightFingerSensorData() {
        return rightFingerSensorData;
    }

    public void setRightFingerSensorData(FingerSensorData rightFingerSensorData) {
        this.rightFingerSensorData = rightFingerSensorData;
    }

    public ConcurrentHashMap<String, SensorData> getSensorDataList() {
        return sensorDataMap;
    }

    public String getSensorDataListJson() {
//        System.out.println(sensorDataMap);
        try {
            return gson.toJson(sensorDataMap);
        } catch (IllegalArgumentException e) {
            System.out.println("2  " + sensorDataMap);
            System.err.println("Null 에러!!!!!!!!!!!!!!!! \n" + e);
            return null;
        }
    }




//    public String getSensorDataListJson() {
//        return gson.toJson(sensorDataList);
//    }

//    public void setSensorDataList(List<SensorData> sensorDataList) {
//        this.sensorDataList = sensorDataList;
//    }

    public void addSensorData(SensorData sensorData) {
        this.sensorDataMap.put(sensorData.getID()+"", sensorData);
//        this.sensorDataList.add(sensorData);
    }

    public SensorData getSensorData(String id) {
        return this.sensorDataMap.get(id);
    }

    public boolean isResetting() {
        return resetting;
    }

    public void setResetting(boolean resetting) {
        this.resetting = resetting;
    }
}
