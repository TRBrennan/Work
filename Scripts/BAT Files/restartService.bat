@ECHO OFF
SET start=%time%

:: Stop Application Host services
call stopAppHosts.bat
call stopxCP.bat

::Stopping Xplore Services
call stopxPlore.bat

:: Stopping Core Documentum Services
call stopDCTM.bat

:: Starting Core Documentum Services
call startDCTM.bat

::Starting Xplore Services
call startxPlore.bat

:: Starting Application Host Services
call startAppHosts.bat
