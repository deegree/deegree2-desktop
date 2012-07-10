setLocal EnableDelayedExpansion

set src=D:\java\projekte\deegree2_client\lib
set dest=D:\java\projekte\deegree2_client\dist\lib

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

copy D:\java\projekte\deegree2_client\src\*.* D:\java\projekte\deegree2_client\dist\

pause