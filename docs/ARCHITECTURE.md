# 架构说明

《修仙挂机》采用 **WebView 壳 + JavaScript 游戏** 的混合架构：Android 原生层只负责容器与系统能力桥接，全部游戏逻辑运行在 WebView 内的 HTML/JS 中。

## 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                    Android 原生层                        │
│  MainActivity.kt                                        │
│    ├── WebView（全屏加载 assets/index.html）             │
│    └── WebAppInterface（JS 桥接，别名 AndroidInterface）│
├─────────────────────────────────────────────────────────┤
│                    assets/ 游戏层                        │
│  index.html  → 页面结构与 Tab 布局                       │
│  game.js     → 全部游戏逻辑（~2 万行）                   │
│  math.js     → math.js 高精度数学库                      │
│  styles.css  → MUD 终端风格 UI                          │
└─────────────────────────────────────────────────────────┘
```

## 数据流

### 启动流程

1. `MainActivity.onCreate()` 创建 WebView
2. 配置 JavaScript、DOM Storage、文件访问等 WebSettings
3. 注入 `AndroidInterface`（`WebAppInterface` 实例）
4. 加载 `file:///android_asset/index.html`
5. `index.html` 依次加载 `math.js`、`game.js`，初始化游戏

### 存档持久化

- 游戏内数据通过 **`localStorage`** 存储（WebView 的 DOM Storage）
- `game.js` 中的 `StorageCache` 提供读写缓存层
- 支持多角色存档（最多 4 个化身）

### 原生桥接（AndroidInterface）

| JS 方法 | 用途 |
|---------|------|
| `downloadFile(base64, fileName, mimeType)` | 导出存档到 Downloads |
| `pickFile(mimeType, onSuccess, onError)` | 选择并读取文本文件（导入存档） |
| `pickImage(onSuccess, onError)` | 选择图片，返回 Base64 |
| `pickMultipleImages(onSuccess, onError)` | 多选图片 |
| `readFile(fileName)` | 从 assets 或 Downloads 读取文件 |
| `showToast(message)` | 显示 Toast |
| `setKeepScreenOn(boolean)` | 控制屏幕常亮 |
| `getDeviceInfo()` | 返回 Android 版本信息 |

## 目录职责

| 路径 | 职责 |
|------|------|
| `app/src/main/java/.../MainActivity.kt` | WebView 生命周期、文件选择、下载、返回键、Insets |
| `app/src/main/assets/game.js` | 挂机/战斗/洞府/法则/内天地/存档等全部玩法 |
| `app/src/main/assets/index.html` | UI 骨架与 Tab 容器 |
| `app/src/main/assets/styles.css` | 终端风格样式 |
| `app/src/main/AndroidManifest.xml` | 权限、Activity、FileProvider |
| `app/build.gradle.kts` | 版本号、签名、混淆、依赖 |

## 技术选型说明

- **为何用 WebView 而非 Compose 做游戏 UI？** 游戏 UI 为大量动态文本与 Tab 切换，HTML/CSS/JS 迭代更快；Compose 依赖主要用于 Activity 框架，实际界面由 WebView 渲染。
- **为何引入 math.js？** 挂机游戏数值跨度大，需要高精度幂运算、对数等，`game.js` 封装了 `$evalBig`、`$pow` 等工具函数。
- **单机无网络：** 除 WebView 内置能力外不发起网络请求；`INTERNET` 权限主要用于 WebView 兼容性。

## 修改指南速查

| 想改什么 | 改哪里 |
|----------|--------|
| 游戏玩法、数值、界面文字 | `assets/game.js`、`styles.css`、`index.html` |
| 应用名、图标、权限 | `AndroidManifest.xml`、`res/` |
| 存档导入导出、屏幕常亮 | `MainActivity.kt` / `WebAppInterface` |
| 版本号 | `app/build.gradle.kts` |

更详细的开发说明见 [DEVELOPMENT.md](DEVELOPMENT.md)。

## 相关文档

- [构建指南](BUILD.md)
- [开发指南](DEVELOPMENT.md)
- [游戏机制说明](../app/src/main/assets/游戏说明.txt)
- [随机数说明](../随机数说明文档.md)
- [Math.random 使用分析](../Math.random使用情况分析.md)
