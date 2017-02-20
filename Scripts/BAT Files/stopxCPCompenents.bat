@ECHO OFF
SET start=%time%
ECHO ##########################################################
ECHO #		xCP Tomcats Host Stop
ECHO #		Author: Tom Brennan
ECHO #		Client: HMRC
ECHO #		Date Started: 08/09/2016
ECHO #		Last Updated Started: 08/09/2016
ECHO #		Last Updated by: TB
ECHO #		Information: This script stops all Application Host xCP Components Services and clears all caches		
ECHO #		References : Luke Sampson:@ stack Overflow (Timer Code)
ECHO #		References : Ryan Smith: @HMRC DeployALLATC.bat	
ECHO #		Disclaimer: Script is delivered as is, 
ECHO #			support is not offered or implied, 
ECHO #############################################################


SET APPLICATION.SERVER.FOLDER.PATH=E$\tomcat-instances\
SET APPLICATION.SERVER.NAME.BAM=tomcat-bam-8020
SET APPLICATION.SERVER.NAME.BPS=tomcat-bps-8021
SET APPLICATION.SERVER.NAME.XMS=tomcat-xms-8022
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
	
	ECHO Stopping and Clearing %%s BAM
	call safeServiceStop.bat %%s %APPLICATION.SERVER.NAME.BAM%
	ECHO DEL DIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.BAM%\temp
	RMDIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.BAM%\temp /q /s
	ECHO DEL DIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.BAM%\work
	RMDIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.BAM%\work /q /s
	MKDIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.BAM%\work
	MKDIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.BAM%\temp
	
	ECHO Stopping and Clearing %%s BPS
	call safeServiceStop.bat %%s %APPLICATION.SERVER.NAME.BPS%
	ECHO DEL DIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.BPS%\temp
	RMDIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.BPS%\temp /q /s
	ECHO DEL DIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.BPS%\work
	RMDIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.BPS%\work /q /s
	MKDIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.BPS%\work
	MKDIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.BPS%\temp

	ECHO Stopping and Clearing %%s XMS
	call safeServiceStop.bat %%s %APPLICATION.SERVER.NAME.XMS%
	ECHO DEL DIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.XMS%\temp
	RMDIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.XMS%\temp /q /s
	ECHO DEL DIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.XMS%\work
	RMDIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.XMS%\work /q /s
	MKDIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.XMS%\work
	MKDIR \\%%s\%APPLICATION.SERVER.FOLDER.PATH%%APPLICATION.SERVER.NAME.XMS%\temp
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