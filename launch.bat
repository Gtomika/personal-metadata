@echo off
echo Setting JAVA_HOME
set JAVA_HOME=[path to JDK 17 bin folder]
echo setting PATH
set PATH=[path to JDK 17 bin folder];%PATH%
echo Display java version
java -version
java -jar PersonalMetadata.jar
pause