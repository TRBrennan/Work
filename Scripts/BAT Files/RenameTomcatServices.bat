:: ###############################################################################################################
:: #							DMS Rename Tomcats
:: #							Author: Tom Brennan
:: #							Client: HMRC
:: #							Date Started: 09/01/2017
:: #							Last Updated Started:  09/01/2017
:: #							Last Updated by: RS
:: #							Information: This script renames tomcats for upgrade prep.
:: #							Information: Script is dependant on a current instance of tomcat custom config
:: #							Limitations: Must be run on each host in turn!
:: #							Script is delivered as is, no support is offered or implied by its author or updater
:: ###############################################################################################################
@ECHO OFF
:: # Cheecky date time format for log file
:: # Sourced: http://stackoverflow.com/questions/203090/how-to-get-current-datetime-on-windows-command-line-in-a-suitable-format-for-us
ECHO Setting up Date Time
SET mydate=A
SET mytime=b
For /f "tokens=2-4 delims=/ " %%a in ('date /t') do (set mydate=%%c-%%a-%%b)
For /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a%%b)
echo %mydate%_%mytime%
SET start=%time%
ECHO Started

:: ###############################################################################################################
:: #							Uninstall and reinstall Services
:: ###############################################################################################################
pushd E:\
ECHO %CD%
cd tomcat-apphost8000\bin
ECHO Uninstalling tomcat-apphost8000
call service.bat uninstall tomcat-apphost8000
ECHO Installing apphost8000
call service.bat install apphost8000
ren tomcat-apphost8000w apphost8000w

cd tomcat-apphost8001\bin
ECHO Uninstalling tomcat-apphost8001
call service.bat uninstall tomcat-apphost8001
ECHO Installing apphost8001
call service.bat install apphost8001
ren tomcat-apphost8001w apphost8001w

cd tomcat-apphost8002\bin
ECHO Uninstalling tomcat-apphost8002
call service.bat uninstall tomcat-apphost8002
ECHO Installing apphost8002
call service.bat install apphost8002
ren tomcat-apphost8002w apphost8002w

cd tomcat-apphost8003\bin
ECHO Uninstalling tomcat-apphost8003
call service.bat uninstall tomcat-apphost8003
ECHO Installing apphost8003
call service.bat install apphost8003
ren tomcat-apphost80003w apphost8003w

cd tomcat-apphost8004\bin
ECHO Uninstalling tomcat-apphost8004
call service.bat uninstall tomcat-apphost8004
ECHO Installing apphost8004
call service.bat install apphost8004
ren tomcat-apphost8004w apphost8004w

cd tomcat-apphost8005\bin
ECHO Uninstalling tomcat-apphost8005
call service.bat uninstall tomcat-apphost8005
ECHO Installing apphost8005
call service.bat install apphost8005
ren tomcat-apphost8005w apphost8005w

cd tomcat-apphost8006\bin
ECHO Uninstalling tomcat-apphost8006
call service.bat uninstall tomcat-apphost8006
ECHO Installing apphost8006
call service.bat install apphost8006
ren tomcat-apphost8006w apphost8006w

cd tomcat-apphost8007\bin
ECHO Uninstalling tomcat-apphost8007
call service.bat uninstall tomcat-apphost8007
ECHO Installing apphost8007
call service.bat install apphost8007
ren tomcat-apphost8007w apphost8007w

SET end=%time%
SET options="tokens=1-4 delims=:."
for /f %options% %%a in ("%start%") do SET start_h=%%a&set /a start_m=100%%b %% 100&set /a start_s=100%%c %% 100&set /a start_ms=100%%d %% 100
for /f %options% %%a in ("%end%") do SET end_h=%%a&set /a end_m=100%%b %% 100&set /a end_s=100%%c %% 100&set /a end_ms=100%%d %% 100

SET /a hours=%end_h%-%start_h%
SET /a mins=%end_m%-%start_m%
SET /a secs=%end_s%-%start_s%
SET /a ms=%end_ms%-%start_ms%
if %hours% lss 0 SET /a hours = 24%hours%
if %mins% lss 0 SET /a hours = %hours% - 1 & SET /a mins = 60%mins%
if %secs% lss 0 SET /a mins = %mins% - 1 & SET /a secs = 60%secs%
if %ms% lss 0 SET /a secs = %secs% - 1 & SET /a ms = 100%ms%
if 1%ms% lss 100 SET ms=0%ms%
:: mission accomplished
SET /a totalsecs = %hours%*3600 + %mins%*60 + %secs% 
ECHO Execution Time= %hours%:%mins%:%secs%.%ms% (%totalsecs%.%ms%s total)
PAUSE
