ECHO this script will execute assuming LIVE information, please update the user-name for other environments
PAUSE 

FOR %%i IN (*.sql) DO (
	sqlcmd -S 10.21.176.156 -d DM_DMS_docbase -i %%i -o %%i.out -U DMS -P %1
)