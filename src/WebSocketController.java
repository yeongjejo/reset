import java.net.*;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;

public class WebSocketController implements Runnable {
    private final int WS_PORT = 8084;

    private WebSocketServer webSocketServer;


    // 싱글톤 용도
    private static WebSocketController singletonObject;

    private WebSocketController() {}

    public static WebSocketController getInstance() {
        if (singletonObject == null) {
            singletonObject = new WebSocketController();
        }

        return singletonObject;
    }

    @Override
    public void run() {
        startWebSocketServer();
    }

    public void startWebSocketServer() {
        webSocketServer = new WebSocketServer(new InetSocketAddress(WS_PORT)) {
            @Override
            public void onOpen(WebSocket conn, ClientHandshake handshake) {
                System.out.println("New WebSocket connection");
            }

            @Override
            public void onMessage(WebSocket conn, String message) {
                // Handle messages from WebSocket clients here if needed
                System.out.println("Received from client: " + message);

                switch (message) {
                    case "reset":
                        System.out.println("리셋 요청 수신");
                        new Reset().resetSensor();
                        break;

                    case "new reset":
                        System.out.println("뉴리셋 요청 수신");
                        new Reset().newResetSensor();
                        break;
                        
                    case "reset mounting":
                        System.out.println("뉴리셋 마운팅 수신");
                        new Reset().newResetMounting();
                        break;

                    default:
                        System.out.println("알 수 없는 메시지: " + message);
                        break;
                }
            }


            @Override
            public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                System.out.println("WebSocket connection closed");
            }

            @Override
            public void onError(WebSocket conn, Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void onStart() {
                System.out.println("WebSocket server started successfully");
            }

//            @Override
//            public void onWebsocketHandshakeReceivedAsServer(WebSocket conn, ClientHandshake handshake) {
//                super.onWebsocketHandshakeReceivedAsServer(conn, handshake);
//                // Custom headers to allow CORS-like behavior
//                conn.addHeader("Access-Control-Allow-Origin", "*");
//                conn.addHeader("Access-Control-Allow-Headers", "Content-Type");
//                conn.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
//            }
        };
        webSocketServer.start();
    }

    public WebSocketServer getWebSocketServer() {
        return webSocketServer;
    }



}
