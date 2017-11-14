package com.gelerion.flink.streaming.window.wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import static java.util.Arrays.stream;

/**
 * Created by denis.shuvalov on 05/11/2017.
 */
public class WindowWordCount {

    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Tuple2<String, Integer>> dataStream =
//                env.socketTextStream("localhost", 9999)
                env.fromElements("Mama mila ramu", "Mama bila papu")
                   .flatMap((String sentence, Collector<Tuple2<String, Integer>> out) -> {
                       System.out.println("sentence = " + sentence);
                       stream(sentence.split("\\s")).forEach(word -> out.collect(new Tuple2<>(word, 1)));

                   })
                   .returns(new TypeHint<Tuple2<String, Integer>>() {})
//                   .flatMap(new Splitter())
                   .keyBy(0)
                   .timeWindow(Time.seconds(5))
                   .sum(1);

//        dataStream.print();
        dataStream.addSink(value -> {
            System.out.println(value.f0 + ":" + value.f1);
        });

        env.execute("Window WordCount");
    }

    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String sentence, Collector<Tuple2<String, Integer>> out) throws Exception {
            for (String word: sentence.split(" ")) {
                out.collect(new Tuple2<>(word, 1));
            }
        }
    }
}
