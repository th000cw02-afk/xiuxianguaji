# 构建指南

本文说明如何在本地编译《修仙挂机》Android 安装包（APK），以及如何通过 Capacitor 在云端构建 iOS 模拟器包。

## Android 环境要求

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

## CI 自动构建

项目包含 GitHub Actions 工作流（[`.github/workflows/android-build.yml`](../.github/workflows/android-build.yml)），在 push/PR 时自动编译 Release APK 并上传为 Artifact。

未配置仓库 Secrets 时，CI 会使用临时生成的签名密钥（仅供验证编译，不可用于正式发布）。正式发布请在仓库 Settings → Secrets 中配置：

- `KEYSTORE_BASE64` — `keystore.jks` 的 Base64 编码
- `KEYSTORE_PASSWORD` / `KEYSTORE_KEY_PASSWORD` / `KEYSTORE_KEY_ALIAS`

---

## iOS / Capacitor 构建

iOS 无法在 Windows 上本地编译，需 **macOS + Xcode** 或 **GitHub Actions macOS runner**。项目采用 Capacitor 包装同一套 Web 资源，Android 仍使用现有 Gradle WebView 壳（双轨）。

### 环境要求（本地 Mac 调试，可选）

| 组件 | 版本要求 |
|------|----------|
| Node.js | 20+ |
| Xcode | 15+ |
| CocoaPods | 1.6+ |

### Web 资源同步

游戏源码仍在 `app/src/main/assets/`。iOS 构建前将资源复制到 Capacitor 的 `www/`：

```powershell
npm install
npm run sync:web          # assets → www
npm run ios:sync          # sync:web + cap sync ios
```

### 本地 Mac 打开 Xcode

```bash
npm run ios:open
# 在 Xcode 中选择模拟器或真机运行
```

### iOS 原生桥接

[`platform-bridge.js`](../app/src/main/assets/platform-bridge.js) 在 iOS 上将 Capacitor 插件 [`plugins/native-bridge`](../plugins/native-bridge) 挂载为 `AndroidInterface`，与 Android [`MainActivity.kt`](../app/src/main/java/com/idle/wenzixiuxian/MainActivity.kt) 行为对齐（存档导入/导出、屏幕常亮等）。

### iOS CI 自动构建

工作流 [`.github/workflows/ios-build.yml`](../.github/workflows/ios-build.yml) 在 push/PR 时：

1. `npm ci` → `npm run ios:sync`
2. `pod install`
3. `xcodebuild` 编译 **iOS Simulator** 包（无需 Apple 签名）

构建完成后，在 GitHub Actions 的 **Artifacts** 中下载 `ios-simulator-app`（`.app`  bundle，仅供模拟器验证编译）。

### 真机 / App Store（Phase 2）

需 Apple Developer 账号（$99/年）并在仓库 Secrets 中配置签名证书与描述文件。当前 CI 默认仅产出 Simulator 包。

| Secret | 说明 |
|--------|------|
| `IOS_BUILD_CERTIFICATE_BASE64` | 分发证书 `.p12` 的 Base64 |
| `IOS_P12_PASSWORD` | 证书密码 |
| `IOS_PROVISIONING_PROFILE_BASE64` | 描述文件 Base64 |
| `IOS_KEYCHAIN_PASSWORD` | 临时钥匙串密码 |
