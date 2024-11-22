
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPBroadcastReceiver implements Runnable {
    @Override
    public void run() {
        broadcastReceive();
        System.out.println("11111111111111");
    }


    // 채널과 시리얼은 추후 필요하면 따로 저장
    private void broadcastReceive() {
        try {
            int port = 65000;
            DatagramSocket ds = new DatagramSocket(port);
            boolean udpClientSendCheck = false;

            while (true) {
                byte buffer[] = new byte[11]; // 수신할 데이터 사이즈 설정

//                System.out.println("22222222222");
                DatagramPacket dp = new DatagramPacket(buffer,buffer.length);
                ds.receive(dp); // 데이터 수신

                byte[] getData = dp.getData(); // 수신한 데이터

                // 클라이언트 ip 주소 및 포트 번호 확인
                InetAddress ia = dp.getAddress();
                port = dp.getPort();
//                System.out.println("client ip : " + ia + " , client port : " + port);

                // 아이피 주소 저장
                String ipNum = (getData[2] & 0xFF) + "." + (getData[3] & 0xFF) + "." + (getData[4] & 0xFF) + "." + (getData[5] & 0xFF);

                // 시리얼 번호 저장
                int serial = ((getData[6] & 0xFF) << 8) | (getData[7] & 0xFF); // byte 값을 int로 변환 후 결합 (6, 7 serial 계산)

//                System.out.println(Integer.toHexString(serial).toUpperCase());

//                System.out.println(serial); // 880 포트

                // 포트 번호 (분활) 저장 (880 포트라고 가정)
//                int portNum = 56569; // 880포트
                int portNum = 56663; // 880포트
//                int portNum = 56775; // 880포트
                byte port6 = (byte) ((portNum >> 8) & 0xFF);
                byte port7 = (byte) (portNum & 0xFF);

                byte ch = getData[8]; // 채널 정보 저장

//               // 데이터 확인
//                System.out.println("Port6 : " + dataInfo.getPort6());
//                System.out.println("Port7 : " + dataInfo.getPort7());

                if (!udpClientSendCheck) {
                    udpClient(ipNum, port6, port7);
                    udpClientSendCheck = true;
                }

            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    private void udpClient(String ip, byte port6, byte port7) {
        try {
            DatagramSocket socket = new DatagramSocket();

            // 송신할 데이터 설정
            byte[] sendData = new byte[10];
            sendData[0] = (byte) 0xFA;
            sendData[1] = (byte) 0xEA;
            sendData[2] = (byte) ((192 << 32) & 0xFF); // 아이피 주소
            sendData[3] = (byte) ((168 << 32) & 0xFF);
//            sendData[4] = (byte) ((201 << 32) & 0xFF);
//            sendData[5] = (byte) ((199 << 32) & 0xFF);
            sendData[4] = (byte) ((0 << 32) & 0xFF);
            sendData[5] = (byte) ((20 << 32) & 0xFF);
            sendData[6] = port6; // 포트 번호
            sendData[7] = port7;
//            sendData[8] = dataInfo.getCh(); // 채널
            sendData[8] = (byte) 0xFB;
            sendData[9] = (byte) 0xFF;

            InetAddress serverAddress = InetAddress.getByName(ip); // ip 주소 설정
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, 65000);
            socket.send(sendPacket); // 데이터 전송

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


}
