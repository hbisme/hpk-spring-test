package com.hb.mapreduce;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 计算每年气温的最高值,一行输入格式为 "年份:气温"
 * mapreduce中的map函数
 */
public class MaxTemperatureMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        super.map(key, value, context);

        String line = value.toString();
        String[] t = line.split(":");
        String year = t[0];
        int airTemperature = Integer.parseInt(t[1]);
        context.write(new Text(year), new IntWritable(airTemperature));
    }


}
