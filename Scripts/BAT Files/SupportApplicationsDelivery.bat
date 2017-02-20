:: #SupportApplicationsDelivery.bat
:: ###############################################################################################################
:: #							DMS Apache tomcat upgrade, scripted
:: #							Author: Chris Brennan, Ambrozco CMS Services Ltd
:: #							Client: HMRC
:: #							Created: 09/01/2017
:: #							Script unzips the file ".\Support Applications.zip" and moves the components to "E:\Support Applications\"
:: #							Script is delivered as is, no support is offered or implied by its author or updater
:: ###############################################################################################################


ECHO Extracting the Files to the current directory
pushd "e:\media"
mkdir "Support Applications"
cd "Support Applications"
jar xf "..\Support Applications.zip"
:: ###############################################################################################################
:: #							Move the media to its delivery locations
:: ###############################################################################################################
cd ..
ECHO Moving the Files to the location "E:\Support Applications"
MOVE /y ".\Support Applications" "E:\"
popd
ECHO PROCESS COMPLETE