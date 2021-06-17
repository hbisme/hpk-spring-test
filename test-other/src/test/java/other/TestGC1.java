package other;


/**
 * 测试GC用,在运行配置里增加JVM参数:-Xloggc:/tmp/gc.log
 * 因为placeHolder变量是分配在栈上的,虽然超过了作用域,但是
 * placeHolder 栈的索引还没有被覆盖,所以GC不会释放这个变量.
 */
public class TestGC1 {

    public static void main(String[] args) {
        if (true) {
            byte[] placeHolder = new byte[64 * 1024 * 1024];
            System.out.println(placeHolder.length / 1024);
        }
        System.gc();
    }
}
