@echo off
set alicompress=%cd%\merge.cmd
echo %alicompress%
set "alicompress=%alicompress:\=\\%"
echo Windows Registry Editor Version 5.00 > regfile.reg 
echo [HKEY_CLASSES_ROOT\Folder\Shell\compress file] >> regfile.reg 
echo [HKEY_CLASSES_ROOT\Folder\Shell\compress file\Command] >> regfile.reg 
echo @="%alicompress% %%1" >> regfile.reg
regedit -s regfile.reg
del regfile.reg
@set/p a=install success , press enter to exit