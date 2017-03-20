@echo off
set /p url="Enter Android sdk path: "
if "%url%"=="" goto clean
pause
set NEW_PATH=%url:\=/%
echo # Location of the android SDK>local.properties
echo sdk.dir=%NEW_PATH%>>local.properties
:clean
call gradlew cleanEclipse
call gradlew eclipse
echo end
exit