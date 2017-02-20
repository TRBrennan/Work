:: ###############################################################################################################
:: #							DMS Restart All Documentum and AppHosts Services
:: #							Author: Tom Brennan
:: #							Client: HMRC
:: #							Date Started: 09/01/2017
:: #							Last Updated Started:  09/01/2017
:: #							Last Updated by: TB
:: #							Information: This script completes full Documentum and App Host Services Restart
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

ECHO Starting Core Documentum Services
:: Starting Core Documentum Services
call startDCTM.bat

ECHO Start xPlore Services
::Starting Xplore Services
call startxPlore.bat

ECHO Start Application Host Services
:: Starting Application Host Services
call startApplicationHostServices.bat


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