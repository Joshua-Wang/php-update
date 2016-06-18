@echo off

set JAVA_HOME="D:\joshua\program files\jdk1.7.0_02\bin"

echo updating ...

java -cp .\PhpUpdate.jar update.UpdateMain "D:\joshua\workspace\share\SAVReporter" > d:\log.txt

echo Update Finished! Check log on d:\log.txt.

pause

