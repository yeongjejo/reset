
import java.net.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {

        // UDP 브로드캐스트 서버 Thread 실행 (port 65000)
        Thread udpBroadcastServerThread = new Thread(new UDPBroadcastReceiver());
        udpBroadcastServerThread.start();

//      UDP 서버 Thread 실행 (port 56569)
        Thread udpServerThread = new Thread(new UDPServer());
        udpServerThread.start();

//      웹소켓 서버 Thread 실행 (port 8080)
        Thread webSocketThread= new Thread(WebSocketController.getInstance());
        webSocketThread.start();

//      필터 Thread 실행
//        Thread filterThread= new Thread(new FilterThread());
//        filterThread.start();


    }


}
