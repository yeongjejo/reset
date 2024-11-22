
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.server.WebSocketServer;
import slimevr.Quaternion;
import slimevr.Vector3;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UDPServer implements Runnable {


    public long start = System.currentTimeMillis();
    private final Quaternion AXES_OFFSET = new Quaternion().fromRotationVector(new Vector3((float) -(0.5 * Math.PI), 0f, 0f));


    @Override
    public void run() {
        start();
    }

    private void start() {
        try {
            List<IMotionParsingByteArea> iMotionByteAreaList = new ArrayList<>();

            iMotionByteAreaList.add(new IMotionParsingByteArea(3, 20, SensorPart.WAIST));
            iMotionByteAreaList.add(new IMotionParsingByteArea(20, 37, SensorPart.BACK));
            iMotionByteAreaList.add(new IMotionParsingByteArea(37, 54, SensorPart.HEAD));
            iMotionByteAreaList.add(new IMotionParsingByteArea(54, 71, SensorPart.LEFT_UPPER_ARM));
            iMotionByteAreaList.add(new IMotionParsingByteArea(71, 88, SensorPart.LEFT_LOWER_ARM));
            iMotionByteAreaList.add(new IMotionParsingByteArea(88, 105, SensorPart.LEFT_HAND));
            iMotionByteAreaList.add(new IMotionParsingByteArea(125, 142, SensorPart.LEFT_SHOULDER));
            iMotionByteAreaList.add(new IMotionParsingByteArea(142, 159, SensorPart.RIGHT_UPPER_ARM));
            iMotionByteAreaList.add(new IMotionParsingByteArea(159, 176, SensorPart.RIGHT_LOWER_ARM));
            iMotionByteAreaList.add(new IMotionParsingByteArea(176, 193, SensorPart.RIGHT_HAND));
            iMotionByteAreaList.add(new IMotionParsingByteArea(213, 230, SensorPart.RIGHT_SHOULDER));
            iMotionByteAreaList.add(new IMotionParsingByteArea(230, 247, SensorPart.LEFT_UPPER_LEG));
            iMotionByteAreaList.add(new IMotionParsingByteArea(247, 264, SensorPart.LEFT_LOWER_LEG));
            iMotionByteAreaList.add(new IMotionParsingByteArea(264, 281, SensorPart.LEFT_FOOT));
            iMotionByteAreaList.add(new IMotionParsingByteArea(281, 298, SensorPart.RIGHT_UPPER_LEG));
            iMotionByteAreaList.add(new IMotionParsingByteArea(298, 315, SensorPart.RIGHT_LOWER_LEG));
            iMotionByteAreaList.add(new IMotionParsingByteArea(315, 332, SensorPart.RIGHT_FOOT));


            int port = 56663;
            DatagramSocket ds = new DatagramSocket(port);
            DataInfo dataInfo = DataInfo.getInstance();
            while (true) {
                // 리셋 중이면 잠시 대기
//                if (dataInfo.isResetting()) {
//                    continue;
//                }


                byte buffer[] = new byte[350]; // 수신할 데이터 사이즈 설정

                DatagramPacket dp = new DatagramPacket(buffer,buffer.length);
                ds.receive(dp); // 데이터 수신

                byte[] receiveStationByteData = dp.getData(); // 수신한 데이터

                // 데이터 확인
//                for (int i = 0; i < receiveStationByteData.length; i++) {
//                    System.out.println("[" + i + "] :" + dp.getData()[i]);
//                }
//                long end = System.currentTimeMillis();
//                System.out.println("현재 밀리초: " + (end- start));
//                start = end;

                // 헤더 데이터 저장
                dataInfo.setHeader0(receiveStationByteData[0]);
                dataInfo.setHeader1(receiveStationByteData[1]);

                dataInfo.setVersion((receiveStationByteData[2] & 0xFF)); // 버전 저장

                // 센서 데이터 저장
                for (IMotionParsingByteArea iMotionByteArea : iMotionByteAreaList) {
                    // receiveStationData(350byte)에서 센서별 필요한 부분(byte)만 복사해서 sensorByteData에 복사
                    // iMotionByteArea.getStartByteNum() 시작 바이트 num
                    // iMotionByteArea.getEndByteNum() 끝 바이트 num
                    byte[] sensorByteData = Arrays.copyOfRange(receiveStationByteData, iMotionByteArea.getStartByteNum(), iMotionByteArea.getEndByteNum());

                    // 센서 번호 저장
                    int sensorID = (sensorByteData[0] & 0xFF);
//                    int sensorID = sensorByteData[0];


                    //TODO: 1, 0하고 255는 뭔지 모르겠어서 일단 continue
                    if (sensorID == 0 || sensorID == 255 || sensorID == 1 || sensorID == 2) {
                        continue;
                    }

                    // 쿼터니언 w, x, y, z 계산
                    float w = culQuaternionData(Arrays.copyOfRange(sensorByteData, 1, 5), sensorID, true);
                    float x = culQuaternionData(Arrays.copyOfRange(sensorByteData, 5, 9), sensorID, false);
                    float y = culQuaternionData(Arrays.copyOfRange(sensorByteData, 9, 13), sensorID, false);
                    float z = culQuaternionData(Arrays.copyOfRange(sensorByteData, 13, 17), sensorID, false);

                    Quaternion rawQuaternion = new Quaternion(w, x, y, z);
//                    System.out.println(AXES_OFFSET);
                    rawQuaternion = AXES_OFFSET.multiply(rawQuaternion);

//
//                    if (iMotionByteArea.getSensorPart() == SensorPart.RIGHT_FOOT)
//                        System.out.println("센서" + sensorID + " 쿼터니언 w:" + w + ", x:" + x + ", y:" + y + ", z:" + z);

                    double[] eulerValue = quaternionToEuler(w, x, y, z); // 오일러 계산

                    // sensorData 기존에 생성 여부 확인
                    SensorData sensorData = dataInfo.getSensorData(sensorID+"");
                    if (sensorData == null) {
                        sensorData = new SensorData(sensorID, rawQuaternion.getW(), rawQuaternion.getX(), rawQuaternion.getY(), rawQuaternion.getZ(), eulerValue[0], eulerValue[1], eulerValue[2], iMotionByteArea.getSensorPart(), new NewReset());
                    } else  {
                        // 쿼터니언 값 저장
                        sensorData.setW(rawQuaternion.getW());
                        sensorData.setX(rawQuaternion.getX());
                        sensorData.setY(rawQuaternion.getY());
                        sensorData.setZ(rawQuaternion.getZ());
//                        System.out.println(1111111111);

                        // 오일러 값 저장
                        sensorData.setEulerX(eulerValue[0]);
                        sensorData.setEulerY(eulerValue[1]);
                        sensorData.setEulerZ(eulerValue[2]);

                    }

                    // TODO: 이거를 모든 데이터를 구하고 난뒤 실행할지 지금 실행할지가 고민이네
                    boolean test = iMotionByteArea.getSensorPart() == SensorPart.RIGHT_FOOT || iMotionByteArea.getSensorPart() == SensorPart.RIGHT_UPPER_LEG;
                    sensorData.getMovingAverage().getFpsTimer().update();
                    sensorData.getMovingAverage().update(test);
                    sensorData.getMovingAverage().addQuaternion(sensorData.getRawRotation(), test);
                    if (test || true) {
//                        System.out.println(sensorData.getRotation().toString());
//                        System.out.println(sensorData.getRawRotation().toString());
                        sensorData.getRotation();

//                        System.out.println("--------------------------------------");
//                        long cul = System.currentTimeMillis();
//                        System.out.println(cul - start);
//                        start = cul;
                    }


                    // 센서 정보 저장
                    dataInfo.addSensorData(sensorData);

                }

                // 웹소켓 전송
                WebSocketServer webSocket = WebSocketController.getInstance().getWebSocketServer();
                if (webSocket != null) {
                    for (WebSocket client : webSocket.getConnections()) {
//                                System.out.println(webSocket.getConnections().size());
                        if (client.isOpen()) {
                            try {
//                                        client.send(DataInfo.getInstance().getSensorData(0).toString());
                                String sendData = dataInfo.getSensorDataListJson();
                                System.out.println(sendData);
                                if (sendData != null) {
                                    client.send(sendData);
                                } else {
                                    System.out.println("null 발생!!!!!!!!!!!");
                                }

//                                System.out.println(DataInfo.getInstance().getSensorDataListJson());
                            } catch (WebsocketNotConnectedException e) {
                                System.out.println("Failed to send message. Client is not connected.");
                            }
                        }
                    }
                }

//                System.out.println("---------------------");

                // TODO: 손가락 센서 데이터 저장 (코드 작성 해야됨)

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // 쿼터니언 값 계산
    private float culQuaternionData(byte[] sensorData, int id, boolean w) {
//         4바이트를 int로 변환
        int intBits = (sensorData[0] & 0xFF) << 24 |
                (sensorData[1] & 0xFF) << 16 |
                (sensorData[2] & 0xFF) << 8  |
                (sensorData[3] & 0xFF);

        float floatValue = Float.intBitsToFloat(intBits);

        return floatValue;
    }

    // 쿼터니언을 오일러 값으로 변환 (ZYX 추후 수정)
    private double[] quaternionToEuler(double w, double x, double y, double z) {
        double roll, pitch, yaw;

        // 롤 (X축 회전)
        double sinr_cosp = 2.0 * (w * x + y * z);
        double cosr_cosp = 1.0 - 2.0 * (x * x + y * y);
        roll = Math.atan2(sinr_cosp, cosr_cosp);

        // 피치 (Y축 회전)
        double sinp = 2.0 * (w * y - z * x);
        if (Math.abs(sinp) >= 1) {
            pitch = Math.copySign(Math.PI / 2, sinp); // 피치가 -90도 또는 90도
        } else {
            pitch = Math.asin(sinp);
        }

        // 요 (Z축 회전)
        double siny_cosp = 2.0 * (w * z + x * y);
        double cosy_cosp = 1.0 - 2.0 * (y * y + z * z);
        yaw = Math.atan2(siny_cosp, cosy_cosp);

        return new double[]{roll, pitch, yaw}; // 결과: [롤, 피치, 요]
    }
}
