# 开发指南

## 快速开始

1. 按 [BUILD.md](BUILD.md) 配置 SDK 与 `local.properties`
2. 用 Android Studio 打开项目根目录，或直接用命令行编译
3. 修改游戏逻辑后重新 `assembleRelease` 或 `assembleDebug`

## 修改游戏逻辑（最常见）

游戏代码全部在 `app/src/main/assets/`：

```
assets/
├── index.html      # 页面结构：Tab 布局、静态占位 DOM
├── game.js         # 核心：状态机、战斗、挂机、存档、UI 更新
├── math.js         # 第三方数学库，一般无需修改
├── styles.css      # MUD 终端风格样式
├── 游戏说明.txt     # 游戏机制文档（可在游戏内读取）
├── 用户协议.md
└── 隐私政策.md
```

### 调试技巧

- **Chrome 远程调试：** 手机开启 USB 调试，PC Chrome 访问 `chrome://inspect`，选中 WebView 即可调试 JS/CSS
- **改 JS 后：** 需重新编译 APK 安装（assets 打包进 APK，不支持热更新）
- **日志：** 在 `game.js` 中使用 `console.log`，通过 Chrome DevTools 或 `adb logcat` 查看

### 代码组织（game.js）

| 模块 | 说明 |
|------|------|
| `$evalBig` / `$pow` 等 | 大数数学工具 |
| `Scheduler` | 定时任务调度（挂机 tick） |
| `StorageCache` | localStorage 缓存层 |
| `getCharacterList` / 多角色 API | 化身管理 |
| Tab 渲染函数 | 各界面动态更新 DOM |

## 修改 Android 壳

文件：[`app/src/main/java/com/idle/wenzixiuxian/MainActivity.kt`](../app/src/main/java/com/idle/wenzixiuxian/MainActivity.kt)

典型场景：

- 新增 JS 桥接方法：在 `WebAppInterface` 中添加 `@JavascriptInterface` 方法
- 调整全屏/状态栏/软键盘：修改 `onCreate` 中的 Window 与 Insets 逻辑
- 新增权限：在 `AndroidManifest.xml` 声明，并在 `MainActivity` 中请求

新增桥接方法后，在 `game.js` 中通过 `AndroidInterface.方法名(...)` 调用。

## 资源与品牌

| 文件 | 用途 |
|------|------|
| `res/values/strings.xml` | `app_name` 字符串资源 |
| `AndroidManifest.xml` | `android:label` 桌面显示名 |
| `res/mipmap-*/` | 应用图标 |
| `res/values/themes.xml` | 启动主题（状态栏颜色等） |

## 发布新版本

1. 更新 `app/build.gradle.kts` 中的 `versionCode`（必须递增）和 `versionName`
2. 执行 `.\编译.bat` 或 `gradlew assembleRelease`
3. 测试 `app/build/outputs/apk/release/app-release.apk`
4. 分发 APK 或提交应用商店

## 代码规范

- **Kotlin：** 遵循项目现有风格，桥接方法需加 `@JavascriptInterface`
- **JavaScript：** 与现有 `game.js` 风格保持一致，大数运算走 `$evalBig` 系列函数
- **注释：** 仅对非显而易见的业务逻辑添加注释

## 技术文档索引

| 文档 | 说明 |
|------|------|
| [ARCHITECTURE.md](ARCHITECTURE.md) | 架构与数据流 |
| [BUILD.md](BUILD.md) | 编译与环境配置 |
| [游戏说明.txt](../app/src/main/assets/游戏说明.txt) | 游戏机制与公式 |
| [随机数说明文档.md](../随机数说明文档.md) | 气运系数与随机数设计 |
| [Math.random使用情况分析.md](../Math.random使用情况分析.md) | 随机数调用分析 |
| [右界判断问题分析.md](../右界判断问题分析.md) | 边界判断问题记录 |

## 贡献

欢迎提交 Issue 与 Pull Request。Fork → 特性分支 → 提交 PR。详见根目录 [README.md](../README.md)。
