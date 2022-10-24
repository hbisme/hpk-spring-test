package priv.hb.sample.processBuilder;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * 测试java运行 shell脚本. 会从标准输出中一直得到输出.不会一直阻塞到子进程结束才会获得输出.
 */
public class RuntimeTest {

    @Test
    public void ProcessTest() throws IOException, InterruptedException {
        // test.sh 里面内容是:  echo start;sleep 10;echo end
        Process process = Runtime.getRuntime().exec("bash /Users/hubin/tmp/testProcess.sh");
        InputStream is = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;

        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("exitCode= " + exitCode);
    }

    @Test
    public void ProcessBuilderTest() throws IOException, InterruptedException {
        ArrayList<String> params = new ArrayList<String>();
        params.add("bash");
        params.add("/Users/hubin/tmp/testProcess.sh");

        ProcessBuilder processBuilder = new ProcessBuilder(params);
        processBuilder.redirectErrorStream(true);

        // 这个方法来生成Process
        Process process = processBuilder.start();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;

        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("exitCode= " + exitCode);
    }

    @Test
    public void ProcessBuilderTimeoutTest() throws IOException, InterruptedException {
        ArrayList<String> params = new ArrayList<String>();
        params.add("bash");
        params.add("/Users/hubin/tmp/testProcess.sh");

        ProcessBuilder processBuilder = new ProcessBuilder(params);
        processBuilder.redirectErrorStream(true);

        // 这个方法来生成Process
        Process process = processBuilder.start();


        System.out.println("-------");
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));



        String line;
        // while ((line = br.readLine()) != null) {
        //     System.out.println(line);
        // }

        Thread.sleep(3000);

        int exitCode = -1;
        if (process.waitFor(2, TimeUnit.SECONDS)) {
            System.out.println("timeout 2 second");
            exitCode = -2;
        }



        System.out.println("=====");
        System.out.println("exitCode= " + exitCode);
    }

}
