package com.hb.client;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

class WebSocketClientInst extends WebSocketClient {

    public WebSocketClientInst(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected");
        send("eyJhY3Rpb24iOiJsb2dpbkJ5U2lkIiwic2lkIjoiZDE3MTI5YjQ5ZGM4MTk4ODhlMjY2MmE1ZGNhNjUzNjBhNjhkY2U5NyIsImd0eXBlIjoiNTkwMiIsImxhbmciOiJjbiIsImRJbmZvIjp7InJkIjoiZngiLCJ1YSI6Ik1vemlsbGEvNS4wIChNYWNpbnRvc2g7IEludGVsIE1hYyBPUyBYIDEwXzE0XzMpIEFwcGxlV2ViS2l0LzUzNy4zNiAoS0hUTUwsIGxpa2UgR2Vja28pIENocm9tZS84Ny4wLjQyODAuMTQxIFNhZmFyaS81MzcuMzYiLCJvcyI6IiBJbnRlbCBNYWMgT1MgWCAxMC4xNC4zIiwic3JzIjoiMTkyMHgxMDgwIiwid3JzIjoiMTI5MHg4NzEiLCJkcHIiOjEuNjAwMDAwMDIzODQxODU4LCJwbCI6Ikg1IiwicGYiOiJDaHJvbWUgODcuMC40MjgwLjE0MSIsInd2IjoiZmFsc2UiLCJhaW8iOmZhbHNlLCJ2Z2EiOiJJbnRlbChSKSBVSEQgR3JhcGhpY3MgNjE3IiwidGFibGV0IjpmYWxzZSwiY3RzIjoxNjEwODAyMDY1MTY5LCJtdWEiOiIiLCJkdHAiOiIifSwiaGFsbElEIjoiMzgxOTQ5NyJ9");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("got: " + message);

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected");

    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();

    }



}

