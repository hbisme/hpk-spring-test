// package com.hb.websocket;
//
// import java.net.URI;
// import java.net.URISyntaxException;
// import java.util.Map;
// import org.java_websocket.client.WebSocketClient;
// import org.java_websocket.drafts.Draft;
// import org.java_websocket.handshake.ServerHandshake;
//
// public class WebsocketClientEndpoint {
//     public static void main(String[] args) {
//         WebSocketClient mWs = new WebSocketClient( new URI( "ws://socket.example.com:1234" ), new Draft_10() )
//         {
//             @Override
//             public void onMessage( String message ) {
//                 JSONObject obj = new JSONObject(message);
//                 String channel = obj.getString("channel");
//             }
//
//             @Override
//             public void onOpen( ServerHandshake handshake ) {
//                 System.out.println( "opened connection" );
//             }
//
//             @Override
//             public void onClose( int code, String reason, boolean remote ) {
//                 System.out.println( "closed connection" );
//             }
//
//             @Override
//             public void onError( Exception ex ) {
//                 ex.printStackTrace();
//             }
//
//         };
//
//         mWs.connect();
//         JSONObject obj = new JSONObject();
//         obj.put("event", "addChannel");
//         obj.put("channel", "ok_btccny_ticker");
//         String message = obj.toString();
//         //send message
//         mWs.send(message);
//
//     }
// }
