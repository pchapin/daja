@echo off

cd src\org\pchapin\daja
java -cp ..\..\..\..\lib\antlr4-4.5.3.jar org.antlr.v4.Tool -visitor Daja.g4
cd ..\..\..\..

