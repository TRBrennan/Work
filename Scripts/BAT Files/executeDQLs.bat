ECHO this script will execute assuming LIVE information, please update the username for other environments
PAUSE 

FOR %%i IN (*.dql) DO (
ECHO %%i
idql DMS -U5858172 -Pa -R%%i >> %%i.out
)