import java.util.Map;

public class FilterThread implements Runnable {


    @Override
    public void run() {
        while (true) {

            DataInfo dataInfo = DataInfo.getInstance();
            Map<String, SensorData> sensorDataList = dataInfo.getSensorDataList();

//            System.out.println("11111111111111111111 : " +dataInfo.getSensorDataList().size());
            if (dataInfo.getSensorDataList().size() > 0) {
//                System.out.println("22222222222");
                for (Map.Entry<String, SensorData> entry : sensorDataList.entrySet()) {
                    SensorData sensorData = entry.getValue();
                    sensorData.getMovingAverage().update(true);
//                    if (sensorData.getSensorPart() == SensorPart.RIGHT_FOOT) {
//                        System.out.println(sensorData.getMovingAverage().test().toString());
//                    }



//                    //TODO: 추후 1밀리세컨드 슬립 코드 추가해야 될수도 있음
//                    try {
//                        Thread.sleep(1);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }


                }
            }
        }
    }
}
