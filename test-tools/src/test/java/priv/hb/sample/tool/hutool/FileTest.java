package priv.hb.sample.tool.hutool;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.Tailer;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.lang.Console;

/**
 *
 * @author hubin
 * @date 2022年10月14日 09:17
 */
public class FileTest {
    /**
     * 监听文件测试,好像不成功.
     * @throws InterruptedException
     */
    @Test
    public void testWatchFile() throws InterruptedException {

        File file = FileUtil.file("/Users/hubin/work/ideaProject/myIdeaProjects/hpk-spring-test/test-tools/src/test/resources/example.properties");
        //这里只监听文件或目录的修改事件
        WatchMonitor watchMonitor = WatchMonitor.create(file, WatchMonitor.ENTRY_MODIFY);
        watchMonitor.setWatcher(new Watcher() {
            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("创建：{}-> {}", currentPath, obj);
            }

            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("修改：{}-> {}", currentPath, obj);
            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("删除：{}-> {}", currentPath, obj);
            }

            @Override
            public void onOverflow(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("Overflow：{}-> {}", currentPath, obj);
            }
        });

        //设置监听目录的最大深入，目录层级大于制定层级的变更将不被监听，默认只监听当前层级目录
        watchMonitor.setMaxDepth(3);
        //启动监听
        watchMonitor.start();

        Thread.sleep(1000000);

    }


    /**
     * 类似于tail -f的功能.
     *
     */
    @Test
    public void testTailer() {
        Tailer tailer = new Tailer(FileUtil.file("/Users/hubin/work/ideaProject/myIdeaProjects/hpk-spring-test/test-tools/src/test/resources/example.properties"), Tailer.CONSOLE_HANDLER, 2);
        tailer.start();
    }


}
