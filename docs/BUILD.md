# 构建指南

本文说明如何在本地编译《修仙挂机》Android 安装包（APK）。

## 环境要求

| 组件 | 版本要求 |
|------|----------|
| JDK | 11 或更高（推荐 17） |
| Android SDK Platform | **36**（与 `compileSdk` 一致） |
| Android SDK Build-Tools | 36.x（Gradle 可能额外拉取 35.x） |
| Android SDK Platform-Tools | 最新版 |
| Gradle | 8.13（项目 Wrapper 自动下载） |

可选：Android Studio（Arctic Fox 或更高），用于图形界面调试与 SDK 管理。

## 首次配置

### 1. 配置 Android SDK

**方式 A — Android Studio（推荐新手）**

1. 安装 [Android Studio](https://developer.android.com/studio)
2. 打开 SDK Manager，安装 Android SDK Platform 36 与 Build-Tools
3. 记下 SDK 路径（通常在 `%LOCALAPPDATA%\Android\Sdk`）

**方式 B — 命令行工具（仅构建 APK）**

```powershell
# 下载 commandlinetools 并解压到 %LOCALAPPDATA%\Android\Sdk\cmdline-tools\latest
# 然后安装必要组件：
sdkmanager --sdk_root=%LOCALAPPDATA%\Android\Sdk "platform-tools" "platforms;android-36" "build-tools;36.0.0"
sdkmanager --sdk_root=%LOCALAPPDATA%\Android\Sdk --licenses
```

### 2. 配置 local.properties

复制模板并填写本机 SDK 路径：

```powershell
copy local.properties.example local.properties
```

编辑 `local.properties`：

```properties
sdk.dir=C\:\\Users\\你的用户名\\AppData\\Local\\Android\\Sdk
```

> `local.properties` 已在 `.gitignore` 中，不会提交到 Git。

### 3. 配置签名（可选）

Release 包需要签名。项目根目录若已有 `keystore.jks`，可直接编译。

自定义签名时，复制 `keystore.properties.example` 为 `keystore.properties` 并填写密码与别名。

## 编译命令

在项目根目录执行：

```powershell
# Release 包（推荐，可直接安装分发）
.\gradlew.bat assembleRelease --no-daemon

# 或使用一键脚本
.\编译.bat

# Debug 包
.\gradlew.bat assembleDebug --no-daemon
```

## 输出位置

| 类型 | 路径 |
|------|------|
| Release APK | `app/build/outputs/apk/release/app-release.apk` |
| Debug APK | `app/build/outputs/apk/debug/app-debug.apk` |

## 安装到设备

**文件传输：** 将 APK 复制到手机，开启「允许未知来源」后安装。

**USB 调试：**

```powershell
adb install -r app\build\outputs\apk\release\app-release.apk
```

## 清理构建缓存

```powershell
.\清理项目.bat
# 或
.\gradlew.bat clean
```

## 常见问题

| 错误 | 处理方式 |
|------|----------|
| `SDK location not found` | 检查 `local.properties` 中 `sdk.dir` 路径是否正确 |
| 缺少 Platform 36 | SDK Manager 安装 Android 36 Platform |
| Gradle 下载超时 | 检查网络；首次构建需下载 Gradle 8.13 与依赖 |
| 签名失败 | 确认 `keystore.jks` 存在，或正确配置 `keystore.properties` |
| `java.lang.OutOfMemoryError` | 在 `gradle.properties` 中增大 `org.gradle.jvmargs` |

## 版本号修改

编辑 [`app/build.gradle.kts`](../app/build.gradle.kts)：

```kotlin
versionCode = 55      // 整数，每次发布递增
versionName = "1.73"  // 显示给用户的版本号
```

修改后重新执行 `assembleRelease`。
