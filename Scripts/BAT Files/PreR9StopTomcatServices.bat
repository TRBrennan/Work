@ECHO OFF
SET start=%time%
ECHO ##########################################################
ECHO #		DMS Application Host Stop
ECHO #		Author: Tom Brennan
ECHO #		Client: HMRC
ECHO #		Date Started: 11/01/2017
ECHO #		Last Updated Started: 11/01/2017
ECHO #		Last Updated by: TB
ECHO #		Information: This script stops  all Application Host Services, DAs, BPS,BAM,ActiveMQ and xms-agent.	
ECHO #		References : Luke Sampson:@ stack Overflow (Timer Code)
ECHO #		References : Ryan Smith: @HMRC DeployALLATC.bat	
ECHO #		Disclaimer: Script is delivered as is, 
ECHO #			support is not offered or implied, 
ECHO #############################################################


SET APPLICATION.SERVER.FOLDER.PATH=E$\tomcat-instances\
SET APPLICATION.SERVER.NAME=apphost
:: # Set up all the Hosts
ECHO
ECHO

ECHO ##################### WARNING ############################
ECHO
ECHO                 DO NOT PROCEED UNLESS YOU HAVE CONFIRMED
ECHO                 THAT THE APPLICATION.SERVER.HOST VALUES
ECHO                     ARE CORRECT FOR YOUR ENVIRONMENT
ECHO
ECHO                 BY DEFAULT, THESE ARE CONFIGURED FOR THE
ECHO                            LIVE ENVIRONMENT
ECHO
ECHO #################### END WARNING ##########################

PAUSE 

SET APPLICATION.SERVER.HOST.1=10.21.176.146
SET APPLICATION.SERVER.HOST.2=10.21.176.147
:: # Set up all the DMS Host Ports
SET APPLICATION.SERVER.PORT.1=8000
SET APPLICATION.SERVER.PORT.2=8001
SET APPLICATION.SERVER.PORT.3=8002
SET APPLICATION.SERVER.PORT.4=8003
SET APPLICATION.SERVER.PORT.5=8004
SET APPLICATION.SERVER.PORT.6=8005
SET APPLICATION.SERVER.PORT.7=8006
SET APPLICATION.SERVER.PORT.8=8007
SET APPLICATION.SERVER.PORT.9=8028

:: Stop all our application servers
:: Remove Cache Dirs
for /F "tokens=2 delims==" %%s in ('SET APPLICATION.SERVER.HOST.') do (
	for /F "tokens=2 delims==" %%a in ('SET APPLICATION.SERVER.PORT.') do (
		ECHO Stopping and clearing %%s %APPLICATION.SERVER.NAME%%%a
		call safeServiceStop.bat %%s %APPLICATION.SERVER.NAME%%%a
	)	
	call safeServiceStop.bat %%s bam8020
	call safeServiceStop.bat %%s xms-agent8022
	call safeServiceStop.bat %%s bps8021
	call safeServiceStop.bat %%s da8023
	call safeServiceStop.bat %%s bisda8025
	call safeServiceStop.bat %%s tomcat-REST8070
	call safeServiceStop.bat %%s activemqAdmin8024			
	call safeServiceStop.bat %%s ActiveMQ			
)

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