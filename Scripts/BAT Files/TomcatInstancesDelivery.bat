:: #SupportApplicationsDelivery.bat
:: ###############################################################################################################
:: #		DMS Apache tomcat upgrade, scripted
:: #		Author: Chris Brennan, Ambrozco CMS Services Ltd
:: #		Client: HMRC
:: #		Created: 10/01/2017
:: #		Updated: 10/01/2017
:: #		Update Notes: Altered Working Dir for release store, Added Unzip for the Tomcat-Server Instaces
:: #		Script unzips the file ".\tomcat-instances.zip" and moves the components to "E:\tomcat-instances\"
:: #		Script is delivered as is, no support is offered or implied by its author or updater
:: ###############################################################################################################


@ECHO OFF
ECHO Extracting the Files to the current directory
pushd "E:\Media"
jar xf "tomcat-instances.zip"
jar xf "tomcat-server.zip"
:: ###############################################################################################################
:: #							Move the media to its delivery locations
:: ###############################################################################################################
ECHO Moving the Files to the location "E:\tomcat-instances"
MOVE /y ".\tomcat-instances" "E:\tomcat-instances"
MOVE /y ".\tomcat-server" "E:\tomcat-server"
popd
ECHO PROCESS COMPLETE

