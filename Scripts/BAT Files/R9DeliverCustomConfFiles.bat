:: ###############################################################################################################
:: #	DMS Apache tomcat upgrade, scripted
:: #	Author: Ryan Smith, SMEECM Ltd
:: #	Client: HMRC
:: #	Date Started: 09/01/2017
:: #	Last Updated Started: 09/01/2017
:: #	Last Updated by: RS
:: #	Information: Delivers Custom Cont information to New App Server Servers for Release 9
:: #	Limitations: Not Known at this time
:: #	Disclaimer: Script is delivered as is, no support is offered or implied by its author or updater
:: #	Update Script to Include New Hosts Confs
:: ###############################################################################################################
:: ###############################################################################################################
:: #							Script Warnings
:: ###############################################################################################################
@ECHO OFF
::Script Warnings
ECHO Warning Move Configuration Files to the new Servers
ECHO Ensure all Java based applications are not executing.
ECHO Please Terminate the script if you are unsure.
PAUSE

:: ###############################################################################################################
:: #							Get Conf for xCP Component Hosts
:: ###############################################################################################################
ECHO Get Conf files for the bam server
robocopy E:\tomcat-bam8020\Customconf E:\tomcat-instances\tomcat-bam-8020\Customconf /copyall /e /tee /v /xf log4j.properties
ECHO Get Conf for new bps
robocopy E:\tomcat-bps8021\webapps\bps\WEB-INF\classes E:\tomcat-instances\tomcat-bps-8021\Customconf /copyall /e /tee /v  /xf version.properties
ECHO Get Conf for DA's New Host
robocopy E:\tomcat-da8023\Customconf E:\tomcat-instances\tomcat-da-8023\Customconf /copyall /e /tee /v  /xf log4j.properties
ECHO Get conf for XMS's new Host
robocopy E:\tomcat-xms-agent8022\Customconf E:\tomcat-instances\tomcat-xms-8022\Customconf /copyall /e /tee /v 
:: ###############################################################################################################
:: #							Get Conf for Customised application hosts
:: ###############################################################################################################
ECHO Get DFC, Tagpooling Conf for new bisda 
robocopy E:\tomcat-bisda8025\Customconf E:\tomcat-instances\tomcat-bisda-8025\Customconf /copyall /e /tee /v  /xf log4j.properties
ECHO Get Conf for the CPS 8028 host
robocopy E:\tomcat-apphost8028\Customconf E:\tomcat-instances\tomcat-cps-8028\Customconf /copyall /e /tee /v 
ECHO Get DFC props for the REST host Do not Deliver the LOG4J
robocopy E:\tomcat-REST8070\CustomConf E:\tomcat-instances\tomcat-rest-8070\Customconf /copyall /e /tee /v  /xf log4j.properties RoboticAuthProps.properties
:: ###############################################################################################################
:: #							Get Conf for DMS application hosts
:: ###############################################################################################################
ECHO Get Conf for the 8000 host
robocopy E:\tomcat-apphost8000\Customconf E:\tomcat-instances\tomcat-dms-8000\Customconf /copyall /e /tee /v 
ECHO Get Conf for the 8001 host
robocopy E:\tomcat-apphost8001\Customconf E:\tomcat-instances\tomcat-dms-8001\Customconf /copyall /e /tee /v 
ECHO Get Conf for the 8002 host
robocopy E:\tomcat-apphost8002\Customconf E:\tomcat-instances\tomcat-dms-8002\Customconf /copyall /e /tee /v 
ECHO Get Conf for the 8003 host
robocopy E:\tomcat-apphost8003\Customconf E:\tomcat-instances\tomcat-dms-8003\Customconf /copyall /e /tee /v 
ECHO Get Conf for the 8004 host
robocopy E:\tomcat-apphost8004\Customconf E:\tomcat-instances\tomcat-dms-8004\Customconf /copyall /e /tee /v 
ECHO Get Conf for the 8005 host
robocopy E:\tomcat-apphost8005\Customconf E:\tomcat-instances\tomcat-dms-8005\Customconf /copyall /e /tee /v 
ECHO Get Conf for the 8006 host
robocopy E:\tomcat-apphost8006\Customconf E:\tomcat-instances\tomcat-dms-8006\Customconf /copyall /e /tee /v 
ECHO Get Conf for the 8007 host
robocopy E:\tomcat-apphost8007\Customconf E:\tomcat-instances\tomcat-dms-8007\Customconf /copyall /e /tee /v 
