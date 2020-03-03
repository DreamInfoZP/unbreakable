package com.uaepay.rm.unbreakable;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class BenchmarkTest {
  @Test
  public void launchBenchmark() throws Exception {
    Options opt = new OptionsBuilder()
        // Specify which benchmarks to run.
        // You can be more specific if you'd like to run only one benchmark per test.
        .include(this.getClass().getName() + ".*")
        // Set the following options as needed
        .mode(Mode.AverageTime).timeUnit(TimeUnit.MILLISECONDS).warmupTime(TimeValue.seconds(1)).warmupIterations(2)
        .measurementTime(TimeValue.seconds(1)).measurementIterations(2).threads(2).forks(1).shouldFailOnError(true)
        .shouldDoGC(true)
        // .jvmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining")
        // .addProfiler(WinPerfAsmProfiler.class)
        .build();

    new Runner(opt).run();
  }

  @Benchmark
  public void benchmark1() {
    approach1();
  }

  @Benchmark
  public void benchmark2() {
    approach2();
  }

  void approach1() {
    for (int i = 0; i < 100; i++) {
      int i1 = Integer.parseInt("100");
      int i2 = 0;
      try {
        i2 = Integer.parseInt("hello");
      } catch (Exception ex) {
        i2 = 0;
      }
    }

  }

  void approach2() {
    for (int i = 0; i < 100; i++) {
      int i1 = Result.trying(() -> Integer.parseInt("100")).getOrElse(() -> 0);
      int i2 = Result.trying(() -> Integer.parseInt("hello")).getOrElse(() -> 0);
    }

  }
}