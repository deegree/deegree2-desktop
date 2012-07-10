set BASEDIR=D:\java\projekte\deegree2_client
set DGRCLSDIR=D:\java\projekte\deegree2\classes

cd %BASEDIR%\dist
mkdir lib
mkdir resources
mkdir scripts
mkdir scripts\sql

cd %BASEDIR%\classes
C:\Programme\Java\jdk1.5.0_06\bin\jar -cmf %BASEDIR%\scripts\batch\IGeoDesktop.mf %BASEDIR%\dist\lib\igeodesktop.jar *.*


copy D:\java\projekte\deegree2\temp\Version.properties %DGRCLSDIR%\org\deegree\framework\version
copy D:\java\projekte\deegree2\temp\buildId.properties %DGRCLSDIR%\org\deegree\framework\version
cd %DGRCLSDIR%

C:\Programme\Java\jdk1.5.0_06\bin\jar cf %BASEDIR%\dist\lib\deegree2.jar org

cd %BASEDIR%

xcopy resources %BASEDIR%\dist\resources\ /s

xcopy scripts\sql %BASEDIR%\dist\scripts\sql\ /s


rem copy libraries
setLocal EnableDelayedExpansion

set src=%BASEDIR%\lib
set dest=%BASEDIR%\dist\lib

pushd !src!

for /f "tokens=* delims= " %%a in ('dir/b/ad') do (
	pushd %%a
	set DN=%%a
	for /f "tokens=* delims= " %%F in ('dir/b/a-d') do (
	 	copy %%F !dest!\%%F
		
	)
	popd
)
::==
del %dest%\dir-prop-base
del %dest%\entries


copy %BASEDIR%\src\*.* %BASEDIR%\dist\

pause