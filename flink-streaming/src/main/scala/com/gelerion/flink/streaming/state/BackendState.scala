package com.gelerion.flink.streaming.state

import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
  * Created by denis.shuvalov on 03/12/2017.
  */
object BackendState {
  def main(args: Array[String]): Unit = {
   val env = StreamExecutionEnvironment.getExecutionEnvironment

    //optional module
/*    // configure path for checkpoints on the remote filesystem
    val backend = new RocksDBStateBackend(checkpointPath)
    // configure path for local RocksDB instance on worker
    backend.setDbStoragePath(dbPath)
    // configure RocksDB options
    backend.setOptions(optionsFactory)

    // configure state backend
    env.setStateBackend(backend*/)

    val backend = new FsStateBackend("uri")
    env.setStateBackend(backend) //default to MemoryStateBackend

  }
}
