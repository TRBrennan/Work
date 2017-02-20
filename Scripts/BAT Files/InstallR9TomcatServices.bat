:: ###############################################################################################################
:: #	DMS Apache tomcat upgrade, scripted
:: #	Author: Ryan Smith, SMEECM Ltd
:: #	Client: HMRC
:: #	Date Started: 09/01/2017
:: #	Last Updated Started: 09/01/2017
:: #	Last Updated by: RS
:: #	Information: Install the New App Server Services for Release 9
:: #	Limitations: Not Known at this time
:: #	Disclaimer: Script is delivered as is, no support is offered or implied by its author or updater
:: # 	Add Display Name config for this
:: ###############################################################################################################
:: ###############################################################################################################
:: #							Script Warnings
:: ###############################################################################################################
@ECHO OFF
::Script Warnings
ECHO Warning This script will install new services to the system
ECHO Ensure all Java based applications are not executing.
ECHO This script relies on the Username being entered in the format Name@Domain if it has not Please Terminate this execution
ECHO Please Terminate the script if you are unsure.
PAUSE

:: ###############################################################################################################
:: #							Set up the Tomcat User Account
:: ###############################################################################################################
SET TOMCAT.USERNAME=%1
SET TOMCAT.PASSWORD=%2

:: ###############################################################################################################
:: #							Install Services
:: ###############################################################################################################
E:
SET CATALINA_HOME=E:\tomcat-server
CD %CATALINA_HOME%\bin
:: ###############################################################################################################
:: #							Install Custom Services
:: ###############################################################################################################
ECHO INSTALL ActiveMQ
SET CATALINA_BASE=E:\tomcat-instances\tomcat-activemq-8024
call service.bat install tomcat-activemq-8024 
sc.exe config activemqAdmin8024 DisplayName= "OLD.A.Tomcat activemq 8024"
sc.exe config tomcat-activemq-8024  obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat activemq 8024"
ECHO INSTALL BISDA
SET CATALINA_BASE=E:\tomcat-instances\tomcat-bisda-8025
call service.bat install tomcat-bisda-8025
sc.exe config bisda8025 DisplayName= "OLD.A.Tomcat bisda 8025"
sc.exe config tomcat-bisda-8025 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat bisda 8025"
ECHO INSTALL CPS 
SET CATALINA_BASE=E:\tomcat-instances\tomcat-cps-8028
call service.bat install tomcat-cps-8028
sc.exe config apphost8028 DisplayName= "OLD.A.Tomcat cps 8028"
sc.exe config tomcat-cps-8028 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat cps 8028"
ECHO INSTALL REST
SET CATALINA_BASE=E:\tomcat-instances\tomcat-rest-8070
call service.bat install tomcat-rest-8070
sc.exe config tomcat-REST8070 DisplayName= "OLD.A.Tomcat rest 8070"
sc.exe config tomcat-rest-8070 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat rest 8070"
:: ###############################################################################################################
:: #							Install xCP Component Services
:: ###############################################################################################################
ECHO INSTALL BPS 
SET CATALINA_BASE=E:\tomcat-instances\tomcat-bps-8021
call service.bat install tomcat-bps-8021
sc.exe config bps8021 DisplayName= "OLD.A.Tomcat bps 8021"
sc.exe config tomcat-bps-8021 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat bps 8021"
ECHO INSTALL BAM
SET CATALINA_BASE=E:\tomcat-instances\tomcat-bam-8020
call service.bat install tomcat-bam-8020
sc.exe config bam8020 DisplayName= "OLD.A.Tomcat bam 8020"
sc.exe config tomcat-bam-8020 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat bam 8020"
ECHO INSTALL DA
SET CATALINA_BASE=E:\tomcat-instances\tomcat-da-8023
call service.bat install tomcat-da-8023
sc.exe config da8023 DisplayName= "OLD.A.Tomcat da 8023"
sc.exe config tomcat-da-8023 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat da 8023"
ECHO INSTALL XMS
SET CATALINA_BASE=E:\tomcat-instances\tomcat-xms-8022
call service.bat install tomcat-xms-8022
sc.exe config xms-agent8022 DisplayName= "OLD.A.Tomcat xms 8022"
sc.exe config tomcat-xms-8022 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat xms 8022"
:: ###############################################################################################################
:: #							Install DMS Services
:: ###############################################################################################################
ECHO INSTALL tomcat-dms-8000
SET CATALINA_BASE=E:\tomcat-instances\tomcat-dms-8000
call service.bat install tomcat-dms-8000
sc.exe config apphost8000 DisplayName= "OLD.A.Tomcat dms 8000"
sc.exe config tomcat-dms-8000 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat dms 8000"
ECHO INSTALL tomcat-dms-8001
SET CATALINA_BASE=E:\tomcat-instances\tomcat-dms-8001
call service.bat install tomcat-dms-8001
sc.exe config apphost8001 DisplayName= "OLD.A.Tomcat dms 8001"
sc.exe config tomcat-dms-8001 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat dms 8001"
ECHO INSTALL tomcat-dms-8002
SET CATALINA_BASE=E:\tomcat-instances\tomcat-dms-8002
call service.bat install tomcat-dms-8002
sc.exe config apphost8002 DisplayName= "OLD.A.Tomcat dms 8002"
sc.exe config tomcat-dms-8002 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat dms 8002"
ECHO INSTALL tomcat-dms-8003
SET CATALINA_BASE=E:\tomcat-instances\tomcat-dms-8003
call service.bat install tomcat-dms-8003
sc.exe config apphost8003 DisplayName= "OLD.A.Tomcat dms 8003"
sc.exe config tomcat-dms-8003 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat dms 8003"
ECHO INSTALL tomcat-dms-8004
SET CATALINA_BASE=E:\tomcat-instances\tomcat-dms-8004
call service.bat install tomcat-dms-8004
sc.exe config apphost8004 DisplayName= "OLD.A.Tomcat dms 8004"
sc.exe config tomcat-dms-8004 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat dms 8004"
ECHO INSTALL tomcat-dms-8005
SET CATALINA_BASE=E:\tomcat-instances\tomcat-dms-8005
call service.bat install tomcat-dms-8005
sc.exe config apphost8005 DisplayName= "OLD.A.Tomcat dms 8005"
sc.exe config tomcat-dms-8005 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat dms 8005"
ECHO INSTALL tomcat-dms-8006
SET CATALINA_BASE=E:\tomcat-instances\tomcat-dms-8006
call service.bat install tomcat-dms-8006
sc.exe config apphost8006 DisplayName= "OLD.A.Tomcat dms 8006"
sc.exe config tomcat-dms-8006 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat dms 8006"
ECHO INSTALL tomcat-dms-8007
SET CATALINA_BASE=E:\tomcat-instances\tomcat-dms-8007
call service.bat install tomcat-dms-8007
sc.exe config apphost8007 DisplayName= "OLD.A.Tomcat dms 8007"
sc.exe config tomcat-dms-8007 obj= %TOMCAT.USERNAME% password= %TOMCAT.PASSWORD% DisplayName= "A.Tomcat dms 8007"

:: ###############################################################################################################
:: #							Import New Registry Keys
:: ###############################################################################################################
ECHO Import New Registry Keys for Tomcats
regedit.exe /s E:\Media\ApacheTomcat_Procrun_RegistryEntries.reg
pushd e:\media