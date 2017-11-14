package com.gelerion.flink.batching.wordcount;

import com.gelerion.flink.streaming.wordcount.WordCountData;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.AggregateOperator;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.Collector;

/**
 * Created by denis.shuvalov on 14/11/2017.
 */
public class WordCountBatch {

    public static void main(String[] args) throws Exception {
        ParameterTool params = ParameterTool.fromArgs(args);

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(params);

        DataSource<String> text = env.fromElements(WordCountData.WORDS);
        AggregateOperator<Tuple2<String, Integer>> wordCount = text.map(sentence -> sentence.replaceAll("[^A-Za-z0-9 ]", ""))
            .flatMap((String sentence, Collector<Tuple2<String, Integer>> out) -> {
                for (String word : sentence.split("\\s")) {
                    out.collect(new Tuple2<>(word, 1));
                }
            })
            .returns(new TypeHint<Tuple2<String, Integer>>() {})
            .groupBy(0)
            .sum(1);

        wordCount/*.partitionByRange(1)*/
                 .sortPartition(1, Order.DESCENDING)
                 .setParallelism(1)
                 .print();

        //env.execute();
    }
}
