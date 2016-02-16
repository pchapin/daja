@echo off
REM
REM This is the testing tool described in "Definitive ANTRL 4."
REM

cd build\production\Daja
java -cp .;..\..\..\lib\antlr-4.5.1.jar org.antlr.v4.gui.TestRig edu.vtc.daja.Daja %1 %2 %3 %4
cd ..\..\..
