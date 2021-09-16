package com.hb.mapreduce;

import org.apache.commons.math3.analysis.function.Max;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * mapdeduce 的 驱动程序.
 */
public class MaxTemperatureDriver extends Configured implements Tool {
    @Override
    public int run(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("参数数量错误");
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }

        Job job = new Job(getConf(), "hb Max temperature");
        job.setJarByClass(getClass());

        // FileInputFormat.addInputPath(job.getPriority(), new Path(args[0]));

        FileInputFormat.addInputPath(new JobConf(getConf()), new Path(args[0]));
        FileOutputFormat.getOutputPath(new JobConf(getConf()));



        job.setMapperClass(MaxTemperatureMapper.class);
        job.setReducerClass(MaxTemperatureReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new MaxTemperatureDriver(), args);
        System.exit(exitCode);
    }
}
