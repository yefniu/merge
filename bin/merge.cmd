@echo off
set mergePath=%cd%
rem echo %~dp0
echo %~nx1
cd D:\merge\bin
java Merge %mergePath%\%~nx1 110.75.196.23 !!cmd:compress=true !!cmd:conv2unicode=true !!cmd:jsCompressOpt=["--disable-optimizations"]
pause