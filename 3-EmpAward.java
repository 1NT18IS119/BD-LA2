package employee;

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class EmpAward {
	   
public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
//private final static IntWritable one = new IntWritable(1);
private Text word = new Text();

public void map(LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException {
	String myString = value.toString();
	String[] empCount = myString.split(",");
	IntWritable awards = new IntWritable(Integer.parseInt(empCount[3]));
	output.collect(new Text(empCount[2]), awards);
	}
}

public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter) throws IOException { //{little: {1,1}} 
	int total = 0;
	Text mykey = key;
	while (values.hasNext())	{
		IntWritable count = values.next();
		total += count.get();
	}
	output.collect(mykey, new IntWritable(total));
	}
}

public static void main(String[] args) throws Exception {
	JobConf conf = new JobConf(EmpAward.class);
	conf.setJobName("No. of awards got by employee whose salary is 30000");
	conf.setOutputKeyClass(Text.class);
	conf.setOutputValueClass(IntWritable.class);
	conf.setMapperClass(Map.class);
	conf.setCombinerClass(Reduce.class);
	conf.setReducerClass(Reduce.class);
	conf.setInputFormat(TextInputFormat.class);
	conf.setOutputFormat(TextOutputFormat.class);
	FileInputFormat.setInputPaths(conf, new Path(args[0]));
	FileOutputFormat.setOutputPath(conf, new Path(args[1]));
	JobClient.runJob(conf);   
}
}