@echo off
title 修仙挂机 - Release 编译
chcp 65001 > nul
cd /d "%~dp0"

echo ==============================================
echo    修仙挂机 - Release APK 编译
echo ==============================================
echo.

call gradlew.bat assembleRelease --no-daemon
if errorlevel 1 (
    echo.
    echo [失败] 编译出错，请查看上方日志。
    pause
    exit /b 1
)

echo.
echo [成功] APK 已生成：
echo   app\build\outputs\apk\release\app-release.apk
echo.
pause
