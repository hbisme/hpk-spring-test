package priv.hb.sample.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

/**
 * This example demonstrates how to create a websocket connection to a server. Only the most
 * important callbacks are overloaded.
 */
public class ExampleClient extends WebSocketClient {

    public ExampleClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public ExampleClient(URI serverURI) {
        super(serverURI);
    }

    public ExampleClient(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("eyJhY3Rpb24iOiJsb2dpbkJ5U2lkIiwic2lkIjoiZDE3MTI5YjQ5ZGM4MTk4ODhlMjY2MmE1ZGNhNjUzNjBhNjhkY2U5NyIsImd0eXBlIjoiNTkwMiIsImxhbmciOiJjbiIsImRJbmZvIjp7InJkIjoiZngiLCJ1YSI6Ik1vemlsbGEvNS4wIChNYWNpbnRvc2g7IEludGVsIE1hYyBPUyBYIDEwXzE0XzMpIEFwcGxlV2ViS2l0LzUzNy4zNiAoS0hUTUwsIGxpa2UgR2Vja28pIENocm9tZS84Ny4wLjQyODAuMTQxIFNhZmFyaS81MzcuMzYiLCJvcyI6IiBJbnRlbCBNYWMgT1MgWCAxMC4xNC4zIiwic3JzIjoiMTkyMHgxMDgwIiwid3JzIjoiMTI5MHg4NzEiLCJkcHIiOjEuNjAwMDAwMDIzODQxODU4LCJwbCI6Ikg1IiwicGYiOiJDaHJvbWUgODcuMC40MjgwLjE0MSIsInd2IjoiZmFsc2UiLCJhaW8iOmZhbHNlLCJ2Z2EiOiJJbnRlbChSKSBVSEQgR3JhcGhpY3MgNjE3IiwidGFibGV0IjpmYWxzZSwiY3RzIjoxNjEwODAyMDY1MTY5LCJtdWEiOiIiLCJkdHAiOiIifSwiaGFsbElEIjoiMzgxOTQ5NyJ9");
        System.out.println("opened connection");
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage(String message) {
        System.out.println("received: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The codecodes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        ExampleClient c = new ExampleClient(new URI(
                "wss://yangheshengtai.com/fxCasino/fxLB?gameType=5902")); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
        c.connect();

        Thread.sleep(10000);
        System.out.println("init");
        String init = "eyJhY3Rpb24iOiJsb2dpbkJ5U2lkIiwic2lkIjoiZDE3MTI5YjQ5ZGM4MTk4ODhlMjY2MmE1ZGNhNjUzNjBhNjhkY2U5NyIsImd0eXBlIjoiNTkwMiIsImxhbmciOiJjbiIsImRJbmZvIjp7InJkIjoiZngiLCJ1YSI6Ik1vemlsbGEvNS4wIChNYWNpbnRvc2g7IEludGVsIE1hYyBPUyBYIDEwXzE0XzMpIEFwcGxlV2ViS2l0LzUzNy4zNiAoS0hUTUwsIGxpa2UgR2Vja28pIENocm9tZS84Ny4wLjQyODAuMTQxIFNhZmFyaS81MzcuMzYiLCJvcyI6IiBJbnRlbCBNYWMgT1MgWCAxMC4xNC4zIiwic3JzIjoiMTkyMHgxMDgwIiwid3JzIjoiMTI5MHg4NzEiLCJkcHIiOjEuNjAwMDAwMDIzODQxODU4LCJwbCI6Ikg1IiwicGYiOiJDaHJvbWUgODcuMC40MjgwLjE0MSIsInd2IjoiZmFsc2UiLCJhaW8iOmZhbHNlLCJ2Z2EiOiJJbnRlbChSKSBVSEQgR3JhcGhpY3MgNjE3IiwidGFibGV0IjpmYWxzZSwiY3RzIjoxNjEwODAyMDY1MTY5LCJtdWEiOiIiLCJkdHAiOiIifSwiaGFsbElEIjoiMzgxOTQ5NyJ9";
        c.send(init);

    }

}