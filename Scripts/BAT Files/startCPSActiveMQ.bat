@ECHO OFF
SET start=%time%
ECHO ##########################################################
ECHO #		CPS ActiveMQ Start
ECHO #		Author: Tom Brennan
ECHO #		Client: HMRC
ECHO #		Date Started: 08/09/2016
ECHO #		Last Updated Started: 08/09/2016
ECHO #		Last Updated by: TB
ECHO #		Information: This script starts all Application Host CPS and Active MQ Services and clears all caches		
ECHO #		References : Luke Sampson:@ stack Overflow (Timer Code)
ECHO #		References : Ryan Smith: @HMRC DeployALLATC.bat	
ECHO #		Disclaimer: Script is delivered as is, 
ECHO #			support is not offered or implied, 
ECHO #############################################################



SET APPLICATION.SERVER.NAME.ACTIVEMQADMIN=tomcat-activemq-8024
SET APPLICATION.SERVER.NAME.CPS=tomcat-cps-8028

:: # Set up all the Hosts
SET APPLICATION.SERVER.HOST.1=10.21.176.146
SET APPLICATION.SERVER.HOST.2=10.21.176.147

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

for /F "tokens=2 delims==" %%s in ('SET APPLICATION.SERVER.HOST.') do (	

	taskkill /s %%s /im monitoringhost* /f

	ECHO Starting Active MQ and CPS
	call safeServiceStart.bat %%s %APPLICATION.SERVER.NAME.ACTIVEMQADMIN%	
	call safeServiceStart.bat %%s %APPLICATION.SERVER.NAME.CPS%
	call safeServiceStart %%s ActiveMQ
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