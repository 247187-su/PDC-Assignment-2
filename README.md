
# PDC Assignment 2

**Student:** Sultan Majeed  
**Registration Number:** 247187  
**Course:** Parallel and Distributed Computing  
**Language:** Java  
**Development Branch:** assignment-2-development

## Project Overview

This assignment contains seven Java programming exercises focused on object-oriented programming, data structures, multithreading, parallel processing, concurrent collections, and memory performance.

Every question is implemented in a separate directory. Additional test programs verify correctness and handle boundary conditions.

## Question 1 — Trading Engine

**Directory:** `Q1-TradingEngine`

Implements a financial event processing system using object-oriented programming.

Features:
- Abstract TradeEvent base class
- FIX order event processing
- Reuters quote event processing
- Polymorphism and method overriding
- Transaction fee and bid-ask spread calculations
- Automated validation tests

Main program: `TradingEngineApp.java`

Run:

```cmd
javac Q1-TradingEngine\*.java
java -cp Q1-TradingEngine TradingEngineApp
```

## Question 2 — Packet Symmetry Auditor

**Directory:** `Q2-PacketSymmetryAuditor`

Implements a packet symmetry checker using two data structures.

Features:
- LIFO transaction stack
- Circular FIFO request queue
- Symmetric packet verification
- Stack and queue boundary protection
- Automated correctness tests

Main program: `PacketSymmetryAuditor.java`

Run:

```cmd
javac Q2-PacketSymmetryAuditor\*.java
java -cp Q2-PacketSymmetryAuditor PacketSymmetryAuditor
```

## Question 3 — Asynchronous Web Engine

**Directory:** `Q3-AsyncWebEngine`

Demonstrates concurrent execution with Java threads.

Features:
- Runnable-based log processing
- Thread-based telemetry monitoring
- Concurrent worker execution
- Worker lifecycle monitoring
- Graceful thread interruption
- Input validation and iteration tracking

Main program: `AsyncWebEngineApp.java`

Run:

```cmd
javac Q3-AsyncWebEngine\*.java
java -cp Q3-AsyncWebEngine AsyncWebEngineApp
```

## Question 4 — Fork/Join Portfolio Risk Calculator

**Directory:** `Q4-ForkJoinPortfolio`

Uses Java's Fork/Join framework to process portfolio risk values.

Features:
- RecursiveTask for parallel risk summation
- RecursiveAction for risk adjustment
- 5% risk reduction for values above $5,000
- Sequential versus parallel benchmarking
- Boundary and integration tests

Main program: `ForkJoinPortfolioApp.java`

Run:

```cmd
javac Q4-ForkJoinPortfolio\*.java
java -cp Q4-ForkJoinPortfolio ForkJoinPortfolioApp
```

## Question 5 — Parallel Sensor Grid Monitor

**Directory:** `Q5-SensorGridMonitor`

Processes 50,000 sensor readings using multiple worker threads.

Features:
- Abstract sensor search engine
- Parallel minimum search
- Parallel maximum search
- ExecutorService and Future
- Balanced array partitioning
- Correctness, boundary, and consistency tests

Expected results:

- Global minimum: -25.50
- Global maximum: 145.75
- Worker threads: 4

Main program: `SensorGridMonitorApp.java`

Run:

```cmd
javac Q5-SensorGridMonitor\*.java
java -cp Q5-SensorGridMonitor SensorGridMonitorApp
```

## Question 6 — Inventory Management Benchmark

**Directory:** `Q6-InventoryBenchmark`

Compares thread-safe map implementations under concurrent workloads.

Map implementations:
- Collections.synchronizedMap
- ConcurrentHashMap

Features:
- 100 inventory products
- Concurrent stock reads and updates
- 50,000 operations in the main benchmarks
- Atomic stock updates
- Multithreaded stress testing
- Repeated execution-time measurements

Main comparison program: `InventoryComparisonApp.java`

Statistics program: `InventoryStatisticsBenchmark.java`

Run:

```cmd
javac Q6-InventoryBenchmark\*.java
java -cp Q6-InventoryBenchmark InventoryComparisonApp
java -cp Q6-InventoryBenchmark InventoryStatisticsBenchmark
```

## Question 7 — Cache Locality Benchmark

**Directory:** `Q7-CacheLocalityBenchmark`

Compares row-major and column-major matrix traversal.

Features:
- Deterministic matrix generation
- Row-major traversal
- Column-major traversal
- Multiple matrix-size benchmarks
- Statistical performance analysis
- Automated correctness and validation tests

The main experiment uses a 2000 x 2000 matrix containing 4,000,000 elements.

Expected matrix sum: 22,000,000.

Main program: `CacheLocalityBenchmark.java`

Statistics program: `CacheStatisticsBenchmark.java`

Run:

```cmd
javac Q7-CacheLocalityBenchmark\*.java
java -cp Q7-CacheLocalityBenchmark CacheLocalityBenchmark
java -cp Q7-CacheLocalityBenchmark CacheStatisticsBenchmark
```

## Testing

Each question includes automated correctness tests.

Detailed instructions for running them are provided in `TESTING.md`.

## Technologies Used

- Java Development Kit
- Visual Studio Code
- Java Threads
- ExecutorService and Future
- ForkJoinPool
- RecursiveTask and RecursiveAction
- Collections.synchronizedMap
- ConcurrentHashMap
- Git and GitHub

## Benchmark Notes

Execution times depend on hardware, JVM optimization, system load, and thread scheduling.

Measured results are recorded from actual program executions. Performance advantages are not assumed in advance.

## Version Control

Development is maintained on the branch:

`assignment-2-development`

Every commit represents a meaningful implementation, testing, or documentation change.

## Conclusion

This assignment demonstrates practical Java programming techniques for concurrent execution, synchronization, parallel computation, data validation, and memory access optimization.
