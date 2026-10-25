
# PDC Assignment 2 — Testing Guide

**Student:** Sultan Majeed  
**Registration Number:** 247187

## Prerequisites

Java and javac must be installed.

Run all commands from the root directory:

`PDC-Assignment-2-Sultan`

## Question 1 — Trading Engine

```cmd
javac Q1-TradingEngine\*.java
java -cp Q1-TradingEngine TradingEngineApp
java -cp Q1-TradingEngine TradeEventTest
java -cp Q1-TradingEngine TradeEventBoundaryTest
```

Verify:
- FIX order fee calculations
- Reuters bid-ask spread
- Input and boundary validation
- All tests pass

## Question 2 — Packet Symmetry Auditor

```cmd
javac Q2-PacketSymmetryAuditor\*.java
java -cp Q2-PacketSymmetryAuditor PacketSymmetryAuditor
java -cp Q2-PacketSymmetryAuditor PacketDataStructureTest
java -cp Q2-PacketSymmetryAuditor PacketSymmetryTest
```

Verify:
- RADAR is symmetrical
- HELLO is not symmetrical
- Stack follows LIFO
- Queue follows FIFO
- All tests pass

## Question 3 — Asynchronous Web Engine

```cmd
javac Q3-AsyncWebEngine\*.java
java -cp Q3-AsyncWebEngine AsyncWebEngineApp
java -cp Q3-AsyncWebEngine AsyncWorkerLifecycleTest
java -cp Q3-AsyncWebEngine AsyncWorkerInterruptionTest
java -cp Q3-AsyncWebEngine WorkerIterationCountTest
java -cp Q3-AsyncWebEngine WorkerInputValidationTest
```

Verify:
- Workers execute concurrently
- Threads terminate
- Interruption is handled
- Iteration counts are correct
- Invalid inputs are rejected

## Question 4 — Fork/Join Portfolio

```cmd
javac Q4-ForkJoinPortfolio\*.java
java -cp Q4-ForkJoinPortfolio ForkJoinPortfolioApp
java -cp Q4-ForkJoinPortfolio PortfolioPerformanceBenchmark
java -cp Q4-ForkJoinPortfolio ForkJoinRiskTest
java -cp Q4-ForkJoinPortfolio PortfolioThresholdTest
java -cp Q4-ForkJoinPortfolio ForkJoinIntegrationTest
```

Verify:
- Risk summation is correct
- Threshold adjustments are correct
- Sequential and parallel results match
- Integration tests pass

## Question 5 — Sensor Grid Monitor

```cmd
javac Q5-SensorGridMonitor\*.java
java -cp Q5-SensorGridMonitor SensorGridMonitorApp
java -cp Q5-SensorGridMonitor SensorMinMaxTest
java -cp Q5-SensorGridMonitor SensorPartitionBoundaryTest
java -cp Q5-SensorGridMonitor SensorParallelConsistencyTest
```

Verify:
- Global minimum is -25.50
- Global maximum is 145.75
- Partitions cover the entire array
- Results remain consistent across threads

## Question 6 — Inventory Benchmark

```cmd
javac Q6-InventoryBenchmark\*.java
java -cp Q6-InventoryBenchmark SynchronizedMapBenchmark
java -cp Q6-InventoryBenchmark ConcurrentHashMapBenchmark
java -cp Q6-InventoryBenchmark InventoryComparisonApp
java -cp Q6-InventoryBenchmark InventoryCatalogTest
java -cp Q6-InventoryBenchmark InventoryStressTest
java -cp Q6-InventoryBenchmark InventoryStatisticsBenchmark
```

Verify:
- Correct number of operations
- Expected inventory stock is preserved
- No updates are lost
- Both implementations pass stress tests
- Benchmark statistics are displayed

## Question 7 — Cache Locality

```cmd
javac Q7-CacheLocalityBenchmark\*.java
java -cp Q7-CacheLocalityBenchmark CacheLocalityBenchmark
java -cp Q7-CacheLocalityBenchmark TraversalCorrectnessTest
java -cp Q7-CacheLocalityBenchmark MatrixBoundaryValidationTest
java -cp Q7-CacheLocalityBenchmark MultiSizeCacheBenchmark
java -cp Q7-CacheLocalityBenchmark CacheStatisticsBenchmark
```

Verify:
- Row-major and column-major sums match
- Invalid matrices are rejected
- Different matrix sizes are tested
- Statistical benchmark completes successfully

## Final Verification

Check Git status:

```cmd
git status
```

Check the current branch:

```cmd
git branch --show-current
```

Check total commits:

```cmd
git rev-list --count HEAD
```

Before submission, confirm that all test programs pass, the intended development branch is selected, and there are no uncommitted changes.

## Notes

Measured execution times vary across systems and runs.

The tests verify program behavior; benchmark comparisons measure performance under the current execution environment.
