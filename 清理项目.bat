@echo off
title 修仙挂机 - 项目清理
color 0A
chcp 65001 > nul

echo ==============================================
echo    修仙挂机 - 项目清理
echo ==============================================
echo.

cd /d "%~dp0"
echo 当前目录: %CD%
echo.

set "DIRS_TO_CLEAN=.gradle .idea .kotlin build app\build"
set "FILES_TO_CLEAN=*.tmp *.log *.cache *.lock"

echo 开始清理...
echo ================================================

set "CLEANED_COUNT=0"
for %%d in (%DIRS_TO_CLEAN%) do (
    if exist %%d (
        echo [%CLEANED_COUNT%] 清理 %%d ...
        rmdir /s /q "%%d" 2>nul
        if not exist "%%d" (
            echo   [完成] 已删除 %%d
        ) else (
            echo   [失败] 无法删除 %%d
        )
        set /a CLEANED_COUNT+=1
    )
)

for %%f in (%FILES_TO_CLEAN%) do (
    if exist %%f (
        del /q "%%f" 2>nul
        echo   [完成] 已删除 %%f
    )
)

echo.
echo 停止 Gradle 守护进程...
taskkill /f /im java.exe 2>nul >nul
taskkill /f /im gradle.exe 2>nul >nul

echo.
echo ================================================
echo 清理完成！
echo ================================================
timeout /t 3 /nobreak >nul
exit
