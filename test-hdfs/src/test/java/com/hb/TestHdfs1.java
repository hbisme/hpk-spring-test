package com.hb;

import com.hb.mapreduce.MaxTemperatureDriver;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.junit.Assert;
import org.junit.Test;

import static com.hb.HdfsUtils.getPreHdfsFs;
import static org.hamcrest.CoreMatchers.is;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * 测试Hadoop权威指南中操作hdfs的方法.
 */
public class TestHdfs1 {
    @Test
    public void test1() {
        System.out.println("start");
    }

    /**
     * 使用hadoop文件系统接口和其本地文件系统的实现类来测试-读写本地文件
     *
     * @throws IOException
     */
    @Test
    public void testLocalFileSystem() throws IOException {
        Configuration conf = new Configuration();
        LocalFileSystem fs = FileSystem.getLocal(conf);
        Path path = new Path("/Users/hubin/tmp/test.sh");

        FSDataInputStream in = null;
        try {
            in = fs.open(path);
            IOUtils.copyBytes(in, System.out, 4096);
        } catch (IOException e) {
            IOUtils.closeStream(in);
            e.printStackTrace();
        }
    }

    /**
     * 使用seek来重定向读取开始的点位.
     *
     * @throws IOException
     */
    @Test
    public void testLocalSeek() throws IOException {
        Configuration conf = new Configuration();
        LocalFileSystem fs = FileSystem.getLocal(conf);
        Path path = new Path("/Users/hubin/tmp/test.sh");
        FSDataInputStream in = null;
        try {
            in = fs.open(path);
            IOUtils.copyBytes(in, System.out, 4096);
            in.seek(0);
            IOUtils.copyBytes(in, System.out, 4096);
        } catch (IOException e) {
            IOUtils.closeStream(in);
            e.printStackTrace();
        }
    }

    @Test
    public void testProgress() throws IOException {
        String dst = "file:///Users/hubin/tmp/fileOut1.txt";
        BufferedInputStream in = new BufferedInputStream(new FileInputStream("/Users/hubin/tmp/test.sh"));
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(dst), conf);
        FSDataOutputStream out = fs.create(new Path(dst));
        IOUtils.copyBytes(in, out, 4096, true);
    }

    /**
     * 测试创建目录(会自动创建中间目录), 如果目录已存在不会报错
     *
     * @throws IOException
     */
    @Test
    public void testMkdir() throws IOException {
        String dst = "file:///Users/hubin/tmp/fileOut1.txt";
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(dst), conf);
        fs.mkdirs(new Path("file:///Users/hubin/tmp/dir1/dir11"));
    }

    /**
     * 测试预发hdfs环境的 文件元数据
     *
     * @throws IOException
     */
    @Test
    public void testShowStatusForFile() throws IOException {
        FileSystem preFs = getPreHdfsFs();
        FileStatus fileStatus = preFs.getFileStatus(new Path("/user/hadoop/zhundian.txt"));
        System.out.println(fileStatus.isDirectory());
        Assert.assertFalse(fileStatus.isDirectory());
        System.out.println("path: " + fileStatus.getPath().toUri().getPath());
        // 获取占用空间(单位字节,单副本)
        System.out.println(fileStatus.getLen() + "KB");
        System.out.println("修改时间: " + fileStatus.getModificationTime());
        System.out.println("获取副本数量: " + fileStatus.getReplication());
        System.out.println("块的大小KB: " + fileStatus.getBlockSize());
        System.out.println("owner: " + fileStatus.getOwner());
        preFs.close();
    }

    /**
     * 测试预发hdfs环境的 目录元数据
     *
     * @throws IOException
     */
    @Test
    public void testStatusForDirectory() throws IOException {
        Path dir = new Path("/tmp");
        FileSystem preFs = getPreHdfsFs();
        FileStatus dirStatus = preFs.getFileStatus(dir);
        System.out.println("path: " + dirStatus.getPath().toUri().getPath());
        Assert.assertThat(dirStatus.isDirectory(), is(true));
        // 目录的len()都为0
        System.out.println("length: " + dirStatus.getLen());
        System.out.println("修改时间: " + dirStatus.getModificationTime());
        // 目录的副本数量都为0
        System.out.println("获取副本数量: " + dirStatus.getReplication());
        // 目录的块大小都为0
        System.out.println("块的大小KB: " + dirStatus.getBlockSize());
        System.out.println("owner: " + dirStatus.getOwner());
        preFs.close();
    }

    /**
     * 测试列出目录下一级子目录的信息
     *
     * @throws IOException
     */
    @Test
    public void testListStatus() throws IOException {
        FileSystem preFs = getPreHdfsFs();
        FileStatus[] status1 = preFs.listStatus(new Path("/tmp"));
        for (FileStatus fileStatus : status1) {
            System.out.println(fileStatus);
        }
    }

    /**
     * 在path里,使用linux通配符来得到, FileStatus数组
     *
     * @throws IOException
     */
    @Test
    public void testGoloStatus() throws IOException {
        FileSystem preFs = getPreHdfsFs();
        // tmp的一级目录下,已aa开头的文件或文件夹
        FileStatus[] status = preFs.globStatus(new Path("/tmp/aa*"));
        for (FileStatus fileStatus : status) {
            System.out.println(fileStatus);
        }
    }


    /**
     * 测试写hdfs文件, 如果会原文件中的内容.
     *
     * @throws IOException
     */
    @Test
    public void writeFile() throws IOException {
        FileSystem preFs = getPreHdfsFs();
        Path p = new Path("/tmp/hb/hb1.txt");
        FSDataOutputStream out = preFs.create(p);
        out.write("line111\n".getBytes(StandardCharsets.UTF_8));
        // close()中包含了hflush()方法
        out.close();
        Assert.assertThat(preFs.exists(p), is(true));
    }


    @Test
    public void testUrl() throws URISyntaxException {
        String string1 = "oss://LTAIqRZHwTq0kKh8:Yu2R1ko0qZTqmPdkSbm6iJBGtiqXT3@yt-bigdata.oss-cn-hangzhou-internal.aliyuncs.com/dw/ytdw/ods/ods_pt_item_base_snapshot_d/dayid=20210711/table_no=34";
        String string2 = "oss://STS.NTGM33nKjb5yc5Yet8HrHX3VV:EanfWhfPacdcK1RzH1sk8S68BGMzrBRpisiFSvUxs7JA:wt+7fwnBzL2vSmv9nr78StFyytfjTdjaB3qqkXFVysjsdcmtnXME/iCuxxLSxkhT@doc-server-test.oss-cn-hangzhou-internal.aliyuncs.com/hb/exampleobject.txt";
        // String string2 = "oss://STS.NTGM33nKjb5yc5Yet8HrHX3VV:EanfWhfPacdcK1RzH1sk8S68BGMzrBRpisiFSvUxs7JA:CAIS/AF1q6Ft5B2yfSjIr5fyBomHg5RL1feSYRPogXRtRP1kt/b9tDz2IHxOeHRhA+8etfg1nWhX7P0TlqVoRoReREvCKM1565kPMYEZ11uG6aKP9rUhpMCPOwr6UmzWvqL7Z+H+U6muGJOEYEzFkSle2KbzcS7YMXWuLZyOj+wMDL1VJH7aCwBLH9BLPABvhdYHPH/KT5aXPwXtn3DbATgD2GM+qxsmuPnunJfDtEqH1wSnm7FPnemrfMj4NfsLFYxkTtK40NZxcqf8yyNK43BIjvwq1vAZqG2Y5orBWQAJvE/dYvCt6cF0aQhife0zF6tFqr3GvNhoKHxjATlXHn0agAEb/KSfjWwcQusfCIMsYOGhHMOxDcol7luobG2C58qTLIoEbB5QnuFczK/GLzSj16fg1NYyuz1mwt+7fwnBzL2vSmv9nr78StFyytfjTdjaB3qqkXFVysjsdcmtnXMEiCuxxLSxkhT/aXuYsONkSSXkFje+7PmZcprjydVbG38B+A==@doc-server-test.oss-cn-hangzhou-internal.aliyuncs.com/hb/exampleobject.txt";
        // Path path1 = new Path("oss://LTAIqRZHwTq0kKh8:Yu2R1ko0qZTqmPdkSbm6iJBGtiqXT3@yt-bigdata.oss-cn-hangzhou-internal.aliyuncs.com/dw/ytdw/ods/ods_pt_item_base_snapshot_d/dayid=20210711/table_no=34");
        // Path path2 = new Path("oss://STS.NTGM33nKjb5yc5Yet8HrHX3VV:EanfWhfPacdcK1RzH1sk8S68BGMzrBRpisiFSvUxs7JA:CAIS/AF1q6Ft5B2yfSjIr5fyBomHg5RL1feSYRPogXRtRP1kt/b9tDz2IHxOeHRhA+8etfg1nWhX7P0TlqVoRoReREvCKM1565kPMYEZ11uG6aKP9rUhpMCPOwr6UmzWvqL7Z+H+U6muGJOEYEzFkSle2KbzcS7YMXWuLZyOj+wMDL1VJH7aCwBLH9BLPABvhdYHPH/KT5aXPwXtn3DbATgD2GM+qxsmuPnunJfDtEqH1wSnm7FPnemrfMj4NfsLFYxkTtK40NZxcqf8yyNK43BIjvwq1vAZqG2Y5orBWQAJvE/dYvCt6cF0aQhife0zF6tFqr3GvNhoKHxjATlXHn0agAEb/KSfjWwcQusfCIMsYOGhHMOxDcol7luobG2C58qTLIoEbB5QnuFczK/GLzSj16fg1NYyuz1mwt+7fwnBzL2vSmv9nr78StFyytfjTdjaB3qqkXFVysjsdcmtnXMEiCuxxLSxkhT/aXuYsONkSSXkFje+7PmZcprjydVbG38B+A==@doc-server-test.oss-cn-hangzhou-internal.aliyuncs.com/hb/exampleobject.txt");
        URI uri1 = new URI(string1);
        URI uri2 = new URI(string2);
        System.out.println(uri1.getHost());
        System.out.println(uri2.getHost());
    }

    @Test
    public void testLocal() throws Exception {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "file:///");
        conf.set("mapreduce.framework.name", "local");
        conf.setInt("mapreduce.task.io.sort.mb", 1);

        Path input = new Path("/Users/hubin/tmp/test.sh");
        Path output = new Path("/tmp/output");

        LocalFileSystem fs = FileSystem.getLocal(conf);

        MaxTemperatureDriver driver = new MaxTemperatureDriver();
        driver.setConf(conf);

        int exitCode = driver.run(new String[]{
                input.toString(), output.toString()
        });

        System.out.println("exitCode: " + exitCode);


    }


}
