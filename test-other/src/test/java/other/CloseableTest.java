package other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;

public class CloseableTest implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloseableTest.class);

    private final ServerSocket serverSocket;

    public CloseableTest() throws IOException {

        serverSocket = new ServerSocket(9901);
        LOGGER.info("start serverSocket success");

    }

    @Override
    public void close() throws IOException {
        if(serverSocket != null) {
            serverSocket.close();
            LOGGER.info("close serverSocket success");
        }

    }


    public static void main(String[] args) throws IOException {
        CloseableTest c1 = new CloseableTest();
        c1.close();
    }
}
