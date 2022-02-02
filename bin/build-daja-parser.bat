@echo off

cd src\edu\vtc\daja
java -cp ..\..\..\..\lib\antlr4-4.9.2.jar org.antlr.v4.Tool -visitor Daja.g4
cd ..\..\..\..

