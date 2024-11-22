import slimevr.EulerAngles;
import slimevr.EulerOrders;
import slimevr.Quaternion;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class Reset {

    // 글꼴 원래 13


    public void resetSensor() {
        // 리셋 요청
        DataInfo.getInstance().setResetting(true);
        byte[] resetDatagram = createDatagram((byte) 0xa1);
        sendResetSensor(resetDatagram);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                resetDoneSensor();
            }
        };

        // 2초 (2000ms) 후에 task를 실행
        timer.schedule(task, 2000);
    }

    // 리셋 종료
    private void resetDoneSensor() {
        byte[] resetDatagram = createDatagram((byte) 0xa0);
        sendResetSensor(resetDatagram);
        DataInfo.getInstance().setResetting(false);

        System.out.println("리셋 종료");
    }


    // 전송할 데이터 생성
    private byte[] createDatagram(byte command) {
        byte datagram[] = new byte[10];

        datagram[0] = (byte) 0xfa;
        datagram[1] = (byte) 0xef;
        datagram[2] = (byte) 0x30;
        datagram[3] = (byte) 0x30;
        datagram[4] = (byte) 0x30;
        datagram[5] = (byte) 0xaa;
        datagram[6] = command;
        datagram[7] = (byte) 0x00;
        datagram[8] = (byte) 0xfb;
        datagram[9] = (byte) 0xff;


        return datagram;
    }

    private void sendResetSensor(byte[] sendData) {
        try {
            DatagramSocket socket = new DatagramSocket();

//            InetAddress serverAddress = InetAddress.getByName("192.168.201.106"); // ip 주소 설정
            InetAddress serverAddress = InetAddress.getByName("192.168.201.113"); // ip 주소 설정
//            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 65000);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 56663);
            socket.send(sendPacket); // 데이터 전송

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    
    // Qt = 현재 로우 쿼터니언 각도
    // Qt.inv() * Q(t+1) = reset값
    // 근데 요놈이 비툴게 센서를 장착했을때 문제가 있었음
    public void newResetSensor() {
        Map<String, SensorData> sensorDataClone = new HashMap<>(); // 센서 데이터 클론 저장용
        // 센서 데이터 클론 생성
        for (Map.Entry<String, SensorData> entry : DataInfo.getInstance().getSensorDataList().entrySet()) {
            sensorDataClone.put(entry.getKey(), entry.getValue().clone());
        }

        // 생성된 센서 데이터 클론을 순회 하며 데이터 리셋
        for (Map.Entry<String, SensorData> entry : sensorDataClone.entrySet()) {
            SensorData sensorData = entry.getValue();
            sensorData.getNewReset().resetFull(sensorData); // 리셋\

        }

//        System.out.println(sensorDataClone + "11111111111111");
    }

    public void newResetMounting() {
        Map<String, SensorData> sensorDataClone = new HashMap<>(); // 센서 데이터 클론 저장용
        // 센서 데이터 클론 생성
        for (Map.Entry<String, SensorData> entry : DataInfo.getInstance().getSensorDataList().entrySet()) {
            sensorDataClone.put(entry.getKey(), entry.getValue().clone());
        }

        // 생성된 센서 데이터 클론을 순회 하며 데이터 리셋
        for (Map.Entry<String, SensorData> entry : sensorDataClone.entrySet()) {
            SensorData sensorData = entry.getValue();
            sensorData.getNewReset().resetMouning(sensorData);

        }

//        System.out.println(sensorDataClone + "11111111111111");
    }





}

//5501957
