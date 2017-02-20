:: ###############################################################################################################
:: #	DMS App Server Removal Pre R9
:: #	Author: Ryan Smith, SMEECM Ltd
:: #	Client: HMRC
:: #	Date Started: 09/01/2017
:: #	Last Updated Started: 09/01/2017
:: #	Last Updated by: RS
:: #	Information: Script Will remove current installation of Application servers and Deliver 
:: #	Limitations: Not Known at this time
:: #	Disclaimer: Script is delivered as is, no support is offered or implied by its author or updater
:: ###############################################################################################################
@ECHO OFF
::Script Warnings
ECHO Warning, Script will Uninstall Services, Remove Registries and Clear out the FileSystem for DMS Pre Release 9 App Servers
ECHO Ensure All App Server Instances Are Stopped
ECHO Please Terminate the script if you are unsure.
PAUSE

:: ###############################################################################################################
:: #							Delete Old Services
:: ###############################################################################################################
ECHO # get rid of old app hosts
sc delete apphost8000
sc delete apphost8001
sc delete apphost8002
sc delete apphost8003
sc delete apphost8004
sc delete apphost8005
sc delete apphost8006
sc delete apphost8007
ECHO Remove CPS instance
sc delete apphost8028
ECHO ## Remove Old XMS instance
sc delete xms-agent8022
ECHO ## Remove old BPS instance
sc delete bps8021
ECHO ## Remove old BAM instance
sc delete bam8020
ECHO Remove Old DA instance
sc delete da8023
ECHO Remove Old Rest instace
sc delete tomcat-REST8070
ECHO Remove Apache MQ Active Admin Host
sc delete activemqAdmin8024
ECHO Remove old BisDA instance
sc delete bisda8025
:: ###############################################################################################################
:: #							Clean up old Registry Keys
:: ###############################################################################################################
ECHO Clean up old Registry Keys
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8000" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8000" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8001" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8001" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8002" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8002" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8003" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8003" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8004" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8004" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8005" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8005" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8006" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8006" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8007" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8007" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8028" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\apphost8028" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\bps8021" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\bps8021" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\bam8020" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\bam8020" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\tomcat-REST8070" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\tomcat-REST8070" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\da8023" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\da8023" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\activemqAdmin8024" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\activemqAdmin8024" /f
ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\bisda8025" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\bisda8025" /f

ECHO reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\xms-agent8022" /f
reg delete "HKEY_LOCAL_MACHINE\SOFTWARE\Wow6432Node\Apache Software Foundation\Procrun 2.0\xms-agent8022" /f

:: ###############################################################################################################
:: #							Trash the Old FileSystems
:: ###############################################################################################################
taskkill /im monitoringhost* /f
ECHO Remove the Old FileSystems
RMDIR E:\tomcat-apphost8000 /s /q
RMDIR E:\tomcat-apphost8001 /s /q
RMDIR E:\tomcat-apphost8002 /s /q
RMDIR E:\tomcat-apphost8003 /s /q
RMDIR E:\tomcat-apphost8004 /s /q
RMDIR E:\tomcat-apphost8005 /s /q
RMDIR E:\tomcat-apphost8006 /s /q
RMDIR E:\tomcat-apphost8007 /s /q
RMDIR E:\tomcat-apphost8028 /s /q
RMDIR E:\tomcat-bam8020 /s /q
RMDIR E:\tomcat-bps8021 /s /q
RMDIR E:\tomcat-REST8070 /s /q
RMDIR E:\tomcat-da8023 /s /q
RMDIR E:\tomcat-activemqAdmin8024 /s /q
RMDIR E:\tomcat-bisda8025 /s /q
RMDIR e:\tomcat-xms-agent8022 /s /q