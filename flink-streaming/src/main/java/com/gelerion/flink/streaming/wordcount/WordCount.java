package com.gelerion.flink.streaming.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;

/**
 * Implements the "WordCount" program that computes a simple word occurrence
 * histogram over text files in a streaming fashion.
 */
public class WordCount {

    public static void main(String[] args) throws Exception {

        //check input parameters
        final ParameterTool params = ParameterTool.fromArgs(args);

        //set up execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //make parameters available in the web interface
        env.getConfig().setGlobalJobParameters(params);

        DataStream<String> text;
        if(params.has("input")) {
            text = env.readTextFile(params.get("input"));
        }
        else {
            System.out.println("Executing WordCount example with default input data set.");
            System.out.println("Use --input to specify file input.");
            text = env.fromElements(WordCountData.WORDS);
        }

        DataStream<Tuple2<String, Integer>> counts = text
                .flatMap(new Tokenizer())
                // group by the tuple field "0" and sum up tuple field "1"
                .keyBy(0)
                .sum(1);

        System.out.println("Printing result to stdout. Use --output to specify output path.");
        counts.print();

        // execute program
        env.execute("Streaming WordCount");

    }

    public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
        private static final long serialVersionUID = 1L;

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
            String[] tokens = value.toLowerCase().split("\\W+");
            Arrays.stream(tokens)
                  .filter(token -> token.length() > 0)
                  .forEach(token -> out.collect(new Tuple2<>(token, 1)));
        }
    }

}
