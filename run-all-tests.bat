
@echo off
setlocal EnableExtensions

cd /d "%~dp0"

set /a PASSED=0
set /a FAILED=0

set "LOGFILE=%TEMP%\pdc2-check-%RANDOM%-%RANDOM%.log"

echo ==============================================
echo     PDC ASSIGNMENT 2 - INTEGRATION TESTS
echo ==============================================
echo Student: Sultan Majeed
echo Registration Number: 247187
echo Development Branch: assignment-2-development
echo ==============================================

echo.
echo ===== QUESTION 1 - TRADING ENGINE =====
call :compile Q1-TradingEngine
if errorlevel 1 goto :finish
call :run Q1-TradingEngine TradingEngineApp
call :run Q1-TradingEngine TradeEventTest
call :run Q1-TradingEngine TradeEventBoundaryTest

echo.
echo ===== QUESTION 2 - PACKET SYMMETRY =====
call :compile Q2-PacketSymmetryAuditor
if errorlevel 1 goto :finish
call :run Q2-PacketSymmetryAuditor PacketSymmetryAuditor
call :run Q2-PacketSymmetryAuditor PacketDataStructureTest
call :run Q2-PacketSymmetryAuditor PacketSymmetryTest

echo.
echo ===== QUESTION 3 - ASYNCHRONOUS ENGINE =====
call :compile Q3-AsyncWebEngine
if errorlevel 1 goto :finish
call :run Q3-AsyncWebEngine AsyncWebEngineApp
call :run Q3-AsyncWebEngine AsyncWorkerLifecycleTest
call :run Q3-AsyncWebEngine AsyncWorkerInterruptionTest
call :run Q3-AsyncWebEngine WorkerIterationCountTest
call :run Q3-AsyncWebEngine WorkerInputValidationTest

echo.
echo ===== QUESTION 4 - FORK JOIN PORTFOLIO =====
call :compile Q4-ForkJoinPortfolio
if errorlevel 1 goto :finish
call :run Q4-ForkJoinPortfolio ForkJoinPortfolioApp
call :run Q4-ForkJoinPortfolio PortfolioPerformanceBenchmark
call :run Q4-ForkJoinPortfolio ForkJoinRiskTest
call :run Q4-ForkJoinPortfolio PortfolioThresholdTest
call :run Q4-ForkJoinPortfolio ForkJoinIntegrationTest

echo.
echo ===== QUESTION 5 - SENSOR GRID =====
call :compile Q5-SensorGridMonitor
if errorlevel 1 goto :finish
call :run Q5-SensorGridMonitor SensorGridMonitorApp
call :run Q5-SensorGridMonitor SensorMinMaxTest
call :run Q5-SensorGridMonitor SensorPartitionBoundaryTest
call :run Q5-SensorGridMonitor SensorParallelConsistencyTest

echo.
echo ===== QUESTION 6 - INVENTORY BENCHMARK =====
call :compile Q6-InventoryBenchmark
if errorlevel 1 goto :finish
call :run Q6-InventoryBenchmark SynchronizedMapBenchmark
call :run Q6-InventoryBenchmark ConcurrentHashMapBenchmark
call :run Q6-InventoryBenchmark InventoryComparisonApp
call :run Q6-InventoryBenchmark InventoryCatalogTest
call :run Q6-InventoryBenchmark InventoryStressTest
call :run Q6-InventoryBenchmark InventoryStatisticsBenchmark

echo.
echo ===== QUESTION 7 - CACHE LOCALITY =====
call :compile Q7-CacheLocalityBenchmark
if errorlevel 1 goto :finish
call :run Q7-CacheLocalityBenchmark CacheLocalityBenchmark
call :run Q7-CacheLocalityBenchmark TraversalCorrectnessTest
call :run Q7-CacheLocalityBenchmark MatrixBoundaryValidationTest
call :run Q7-CacheLocalityBenchmark MultiSizeCacheBenchmark
call :run Q7-CacheLocalityBenchmark CacheStatisticsBenchmark

goto :finish

:compile
javac %~1\*.java > "%LOGFILE%" 2>&1

if errorlevel 1 (
    echo [FAIL] Compile %~1
    type "%LOGFILE%"
    set /a FAILED+=1
    exit /b 1
)

echo [PASS] Compile %~1
set /a PASSED+=1
exit /b 0

:run
java -cp "%~1" "%~2" > "%LOGFILE%" 2>&1

if errorlevel 1 (
    echo [FAIL] %~2
    type "%LOGFILE%"
    set /a FAILED+=1
) else (
    echo [PASS] %~2
    set /a PASSED+=1
)

exit /b 0

:finish
if exist "%LOGFILE%" del /q "%LOGFILE%" >nul 2>&1

echo.
echo ==============================================
echo            INTEGRATION TEST SUMMARY
echo ==============================================
echo Passed: %PASSED%
echo Failed: %FAILED%
echo ==============================================

if %FAILED% GTR 0 (
    echo RESULT: SOME CHECKS FAILED
    exit /b 1
)

echo RESULT: ALL CHECKS PASSED
exit /b 0
