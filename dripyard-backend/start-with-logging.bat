@echo off
echo ============================================
echo Starting DripYard Backend with Debug Logging
echo ============================================

echo Creating logs directory if it doesn't exist...
if not exist "logs" mkdir logs

echo Starting backend server...
echo Logs will be written to logs/dripyard-backend.log
echo.
echo To monitor logs in real-time, run:
echo    tail -f logs/dripyard-backend.log (Linux/Mac)
echo    Get-Content logs/dripyard-backend.log -Wait (PowerShell)
echo.
echo Network troubleshooting endpoints available at:
echo    http://localhost:8080/api/debug/network-check
echo    http://localhost:8080/api/images/debug/network
echo    http://localhost:8080/api/debug/cors-test
echo.

start "Log Tail" powershell -Command "Get-Content logs/dripyard-backend.log -Wait -ErrorAction SilentlyContinue"

./mvnw spring-boot:run

pause