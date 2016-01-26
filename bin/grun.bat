@echo off
REM
REM This is the testing tool described in "Definitive ANTRL 4."
REM

cd build\production\Daja\edu\vtc\daja\lev0
java -cp .;..\..\..\..\..\..\..\lib\antlr-4.5.1.jar org.antlr.v4.gui.TestRig edu.vtc.daja.lev0.Daja module %1 %2 %3 %4
cd ..\..\..\..\..\..\..
