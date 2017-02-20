@echo off
 
setLocal EnableDelayedExpansion
 set classpath1="
 for /R ./dfc7 %%a in (*.jar) do (
   set classpath1=!classpath1!;%%a
 )
 set classpath=%classpath% + !classpath1!"
 echo !classpath!


idql.exe DMS -Udctm015 -POOpWTVkRXf2YAr1vJQNY -n -R"e:\CreateQueues\createqueue2.dql" > "e:\CreateQueues\createqueue2_report.txt"

iapi.exe DMS -Udctm015 -POOpWTVkRXf2YAr1vJQNY -R"e:\CreateQueues\createqueue2.api" > "e:\CreateQueues\createqueue2_report_api.txt"

set subDir = "%date:~6,4%-%date:~0,2%-%date:~3,2%-%time:~0,2%-%time:~3,2%-%time:~6,2%"

rem Mkdir /archive/%subDir%

rem Move *.api archive/subdir
rem Move *.dql archive/subdir
rem Move *.txt archive/subdir/*