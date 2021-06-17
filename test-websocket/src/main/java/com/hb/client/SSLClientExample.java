package com.hb.client;

import com.hb.client.WebSocketClientInst;

import org.java_websocket.enums.ReadyState;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import java.io.*;
import java.net.URI;
import java.security.KeyStore;

class SSLClientExample {

    public static void main(String[] args) throws Exception {
        WebSocketClientInst chatclient = new WebSocketClientInst(new URI("wss://yangheshengtai.com/fxCasino/fxLB?gameType=5902"));

        // load up the key store
        String KEYSTORE = "/Users/hubin/Downloads/my.store";  //基于证书生成的store秘钥文件的路径
        String STOREPASSWORD = "hb123456";  //使用keytool工具时，输入的密码
        String KEYPASSWORD = "hb123456";

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        File kf = new File(KEYSTORE);
        ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");

        kmf.init(ks, KEYPASSWORD.toCharArray());
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        SSLContext sslContext = null;
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        SSLSocketFactory factory = sslContext.getSocketFactory();// (SSLSocketFactory) SSLSocketFactory.getDefault();

        chatclient.setSocketFactory(factory);

        chatclient.connectBlocking();


        boolean loop = true;
        int times = 0;
        while (loop) {
            times++;
            if (ReadyState.OPEN.equals(chatclient.getReadyState())) {
                Thread.sleep(1000);
                System.out.println("in opend");
                // String init = "";
                // chatclient.send(init);
                Thread.sleep(1000);

                String str2 = "eyJhY3Rpb24iOiJvbkxvYWRJbmZvMiJ9";  //{"action":"onLoadInfo2"}
                chatclient.send(str2);
                Thread.sleep(1000);

                // String str3 = "eyJhY3Rpb24iOiJnZXRNYWNoaW5lRGV0YWlsIn0=";  //{"action":"getMachineDetail"}    // 分数兑换界面
                // chatclient.send(str3);
                // Thread.sleep(1000);

                // String str4 = "eyJhY3Rpb24iOiJjcmVkaXRFeGNoYW5nZSIsInJhdGUiOiIxOjEwIiwiY3JlZGl0IjoiNTAwIn0=";  //{"action":"creditExchange","rate":"1:10","credit":"500"} // d兑换500分
                // chatclient.send(str4);
                // Thread.sleep(1000);

                for (int i = 0; i < 1000; i++) {
                    String str5 = "eyJhY3Rpb24iOiJiZWdpbkdhbWUyIiwibGluZSI6IjEiLCJsaW5lQmV0IjoxMH0="; // {"action":"beginGame2","line":"1","lineBet":10}
                    chatclient.send(str5);
                    Thread.sleep(1000);
                    chatclient.sendPing();
                }

            } else {
                System.out.println("还没ready, 继续进行中");
                if (times <= 10) {
                    Thread.sleep(1000);
                } else {
                    System.out.println("超时");
                    break;
                }
            }
        }
    }

    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}


