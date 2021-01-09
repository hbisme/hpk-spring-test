package other;

import java.io.IOException;


/**
 * java 自带的runtime测试
 */
public class JavaRuntimeTest {
    public static void main(String[] args) throws IOException {
        Runtime runtime = Runtime.getRuntime();

        // 1.用来执行系统命令
        Process o = runtime.exec("pwd");

        // 2. 增加关闭时的钩子
        ShutdownHook shutdownHook = new ShutdownHook();
        runtime.addShutdownHook(shutdownHook);


        System.out.println("Java虚拟机可用的处理器数: " + runtime.availableProcessors());
        System.out.println("Java虚拟机剩余内存字节: " + runtime.freeMemory());
        System.out.println("Java虚拟机最大内存字节: " + runtime.maxMemory());
        System.out.println("Java虚拟机已使用内存字节: " + (runtime.totalMemory() - runtime.freeMemory()));





    }


    public static class ShutdownHook extends Thread {
        @Override
        public void run() {
            System.out.println("showdown hook is call");
        }
    }

}
