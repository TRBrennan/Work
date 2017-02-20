@ECHO OFF
SET start=%time%
ECHO ###################################################################################
ECHO #	DFS Rapid Deploy
ECHO #	Author: Ryan Smith, SMEECM Ltd, Tom Brennan
ECHO #	Client: HMRC
ECHO #	Date Started: 06/09/2016
ECHO #	Last Updated Started: 10/10/2016
ECHO #	Last Updated by: TB
ECHO #	Information: This script simplifies the process of deploying any war file
ECHO #		to all remaning application hosts
ECHO #	Known Issue:
ECHO #	Dependancies: WAR deployed to 1st App Host and Port.
ECHO #	Dependancies: safeServiceStop.bat, safeServiceStart.bat delviered to the server.
ECHO #	Disclaimer: Script is delivered as is, no support is offered or implied by 
ECHO #		its author or updator
ECHO #	References : Luke Sampson:@ stack Overflow (Timer Code)
ECHO ###################################################################################
ECHO Do not execute in ANY NOT LIVE ENV unless the Parameters are updated.
PAUSE

SET APPLICATION.NAME=DMS
SET APPLICATION.NAME.WAR=%APPLICATION.NAME%.war
SET APPLICATION.DELIVERED.WAR.NAME=dgms-DMS-9.0.1.war
SET APPLICATION.SERVICE.NAME=tomcat-dms-
SET APPLICATION.DELIVERY.LOC=E$\Media
SET APPLICATION.WEBSERVICE.LOC=E$\tomcat-instances
::: # Set up all the Hosts
SET APPLICATION.SERVER.HOST.1=10.21.176.146
SET APPLICATION.SERVER.HOST.2=10.21.176.147
:: # Set up all the Ports
SET APPLICATION.SERVER.PORT.1=8000
SET APPLICATION.SERVER.PORT.2=8001
SET APPLICATION.SERVER.PORT.3=8002
SET APPLICATION.SERVER.PORT.4=8003
SET APPLICATION.SERVER.PORT.5=8004
SET APPLICATION.SERVER.PORT.6=8005
SET APPLICATION.SERVER.PORT.7=8006
SET APPLICATION.SERVER.PORT.8=8007
:: # EG of additional Hosts SET APPLICATION.SERVER.PORT.X=???
:: Stop all our application servers
:: Remove Cache Dirs
:: Remove Old War File Unpack

ECHO Copying Delivered Media to Each Application Host.
COPY  \\%APPLICATION.SERVER.HOST.1%\%APPLICATION.DELIVERY.LOC%\%APPLICATION.DELIVERED.WAR.NAME% \\%APPLICATION.SERVER.HOST.2%\%APPLICATION.DELIVERY.LOC% /y
COPY  \\%APPLICATION.SERVER.HOST.2%\%APPLICATION.DELIVERY.LOC%\%APPLICATION.DELIVERED.WAR.NAME% \\%APPLICATION.SERVER.HOST.1%\%APPLICATION.DELIVERY.LOC% /y

for /F "tokens=2 delims==" %%s in ('SET APPLICATION.SERVER.HOST.') do (
	for /F "tokens=2 delims==" %%a in ('SET APPLICATION.SERVER.PORT.') do (
	ECHO STOPPING SERVICE %%s %APPLICATION.SERVICE.NAME%%%a
	call safeServiceStop.bat %%s %APPLICATION.SERVICE.NAME%%%a
	ECHO REMOVING OLD APP \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\webapps\%APPLICATION.NAME%
	RMDIR \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\webapps\%APPLICATION.NAME% /q /s
	ECHO REMOVING TEMP \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\temp
	RMDIR \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\temp /q /s
	ECHO REMOVING WORK \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\work
	RMDIR \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\work /q /s
	RMDIR \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\logs /q /s
	ECHO MKDIR Work, Temp
	MKDIR \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\work
	MKDIR \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\temp
	MKDIR \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\logs
	REM COPY WAR FROM DELIVERY LOCATION TO THE SERVER INSTANCES
	ECHO Copy War From DELIVERY LOCATION \\%%s\%APPLICATION.DELIVERY.LOC%\%APPLICATION.DELIVERED.WAR.NAME% TO APP SERVER LOCATION \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\webapps
	COPY \\%%s\%APPLICATION.DELIVERY.LOC%\%APPLICATION.DELIVERED.WAR.NAME% \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\webapps /y
	ECHO RENAME THE DELIVERED APPLICATION TO THE TO-BE ONLINE APP NAME FROM \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\webapps\%APPLICATION.DELIVERED.WAR.NAME% TO %APPLICATION.NAME.WAR%
	REN \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\webapps\%APPLICATION.DELIVERED.WAR.NAME% %APPLICATION.NAME.WAR%
	DEL \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\webapps\%APPLICATION.DELIVERED.WAR.NAME%	
	)
)
:: Start the Application Server to Unpack
for /F "tokens=2 delims==" %%s in ('SET APPLICATION.SERVER.HOST.') do (
	for /F "tokens=2 delims==" %%a in ('SET APPLICATION.SERVER.PORT.') do (
	ECHO Starting and Unpacking %APPLICATION.SERVICE.NAME%%%a on host %%s
	call safeServiceStart.bat %%s %APPLICATION.SERVICE.NAME%%%a
	)
)
:: Stop them all again pre cleanup
for /F "tokens=2 delims==" %%s in ('SET APPLICATION.SERVER.HOST.') do (
	for /F "tokens=2 delims==" %%a in ('SET APPLICATION.SERVER.PORT.') do (
	call safeServiceStop.bat %%s %APPLICATION.SERVICE.NAME%%%a
	ECHO Removing the War File now we are unpacked for %%s %APPLICATION.SERVICE.NAME%%%a
	DEL /q /s \\%%s\%APPLICATION.WEBSERVICE.LOC%\%APPLICATION.SERVICE.NAME%%%a\webapps\%APPLICATION.NAME.WAR%
	)
)
:: Start them all again for usage
for /F "tokens=2 delims==" %%s in ('SET APPLICATION.SERVER.HOST.') do (
	for /F "tokens=2 delims==" %%a in ('SET APPLICATION.SERVER.PORT.') do (
	call safeServiceStart.bat %%s %APPLICATION.SERVICE.NAME%%%a
	ECHO %APPLICATION.SERVICE.NAME%%%a Host Now ready for Client usage on Server %%s
	)
)
:: Inform the Operator
for /F "tokens=2 delims==" %%s in ('SET APPLICATION.SERVER.HOST.') do (
	for /F "tokens=2 delims==" %%a in ('SET APPLICATION.SERVER.PORT.') do (
	ECHO Service: %APPLICATION.SERVICE.NAME%%%a
	ECHO Host: %%s
	ECHO Actions completed: Stop, Cache Clear, Old App clear, New App Distributed
	)
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