:: ###############################################################################################################
:: #	DMS Apache tomcat upgrade, scripted
:: #	Author: Ryan Smith, SMEECM Ltd
:: #	Client: HMRC
:: #	Date Started: 09/01/2017
:: #	Last Updated Started: 09/01/2017
:: #	Last Updated by: RS
:: #	Information: Script Will remove current installation of Java on an Application server and Deliver 
:: #					New Version
:: #	Limitations: Not Known at this time
:: #	Disclaimer: Script is delivered as is, no support is offered or implied by its author or updater
:: ###############################################################################################################
:: ###############################################################################################################
:: #							Uninstall Existing Java Instance
:: ###############################################################################################################
::Script Warnings
@ECHO OFF
ECHO Warning, this script will remove all installations of JAVA from the system
ECHO Ensure all Java based applications are not executing.
ECHO Please Terminate the script if you are unsure.
PAUSE
::Ask user for current Java instance Version
ECHO Removing Any existing Installation of Java
::Uninstall current J instance
wmic product where "name like 'Java%%'" call uninstall
::Ask user for new java Instance
SET /p newversion=What is the new JDK version for .exe (example: 7u79)?
::Install new java instance
ECHO Silent Installing New JDK
pushd E:\Media
jdk-%newversion%-windows-x64.exe /s ADDLOCAL="ToolsFeature,SourceFeature"
popd
:: ###############################################################################################################
:: #							deliver custom java.security file to the new java installation
:: ###############################################################################################################
ECHO Delivering custom java.security file to the new java installation
SET /p newversiona=What is the new JDK version (example: 1.7.0_79)?
copy /y E:\Media\java.security "C:\Program Files\Java\jdk%newversiona%\jre\lib\security"
:: ###############################################################################################################
:: #							Fix JAVA_HOME
:: ###############################################################################################################
ECHO Overide JAVA_HOME
SETX -m JAVA_HOME "C:\Progra~1\Java\jdk%newversiona%"
SET JAVA_HOME="C:\Progra~1\Java\jdk%newversiona%