# 修仙挂机

一款单机挂机类修仙游戏，完全开源免费。

## 项目简介

《修仙挂机》是一款单机挂机类修仙游戏，无联网功能，所有游戏数据均在本地处理。玩家可以体验从炼气到化神的修仙之旅，包含角色养成、装备道具、法则领悟、内天地建设等丰富玩法。

## 功能特性

- **单机运行**：无需联网，所有数据本地存储
- **修仙系统**：从炼气到化神的完整修仙境界体系
- **挂机玩法**：自动修炼、自动战斗、离线收益
- **角色养成**：属性提升、法则领悟、神通修炼
- **装备系统**：法宝强化、灵药种植、丹药炼制
- **内天地**：世界本源、灵族养成、世界攻略
- **多角色**：支持创建化身，共享资源
- **存档系统**：支持存档导出/导入，数据备份

## 技术栈

| 项 | 说明 |
|----|------|
| 平台 | Android 7.0+（minSdk 24） |
| 原生层 | Kotlin + WebView |
| 游戏层 | HTML / CSS / JavaScript |
| 数学库 | [math.js](https://github.com/josdejong/mathjs)（高精度运算） |
| 构建 | Gradle 8.13 + AGP 8.12 + Kotlin 2.0 |

架构概览：**Android WebView 加载 `assets` 内网页游戏**，存档用 `localStorage`，文件导入导出通过 `AndroidInterface` 桥接原生能力。详见 [架构说明](docs/ARCHITECTURE.md)。

## 项目结构

```
xiuxianguaji/
├── app/
│   ├── src/main/
│   │   ├── assets/              # 游戏本体（改玩法主要改这里）
│   │   │   ├── game.js          # 游戏主逻辑
│   │   │   ├── index.html       # 页面结构
│   │   │   ├── math.js          # 数学库
│   │   │   └── styles.css       # 样式
│   │   ├── java/.../MainActivity.kt  # WebView 壳与 JS 桥接
│   │   └── res/                 # 图标、主题、字符串
│   └── build.gradle.kts         # 版本号、签名、构建配置
├── docs/
│   ├── BUILD.md                 # 构建指南（详细）
│   ├── ARCHITECTURE.md          # 架构说明
│   └── DEVELOPMENT.md           # 开发指南
├── gradlew.bat / 编译.bat        # 命令行编译
├── local.properties.example   # SDK 路径配置模板
├── keystore.properties.example
├── 用户协议.md
├── 隐私政策.md
└── README.md
```

## 快速构建

### 环境要求

- JDK 11+（推荐 17）
- Android SDK Platform **36** + Build-Tools 36.x

### 三步编译

```powershell
# 1. 配置 SDK 路径（首次）
copy local.properties.example local.properties
# 编辑 local.properties，填写 sdk.dir=...

# 2. 编译 Release APK
.\编译.bat

# 3. 安装包位置
# app\build\outputs\apk\release\app-release.apk
```

完整说明（环境安装、签名、排错）见 **[docs/BUILD.md](docs/BUILD.md)**。

### Android Studio

1. File → Open → 选择项目根目录
2. 等待 Gradle Sync
3. Build → Build APK(s) 或直接 Run

## 如何使用

1. 安装 APK（允许未知来源），或 `adb install -r app-release.apk`
2. 打开游戏即可开始
3. 游戏机制详见 [游戏说明.txt](app/src/main/assets/游戏说明.txt)

## 文档索引

| 文档 | 说明 |
|------|------|
| [docs/BUILD.md](docs/BUILD.md) | 构建与环境配置 |
| [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) | 架构与 JS 桥接 |
| [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md) | 开发、调试、发布 |
| [游戏说明.txt](app/src/main/assets/游戏说明.txt) | 游戏机制与公式 |
| [随机数说明文档.md](随机数说明文档.md) | 气运与随机数设计 |

## 开源许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE)。

## 第三方开源库

- [math.js](https://github.com/josdejong/mathjs) — Apache 2.0 License

## 贡献

欢迎提交 Issue 和 Pull Request：

1. Fork 本仓库
2. 创建特性分支（`git checkout -b feature/xxx`）
3. 提交更改并推送
4. 发起 Pull Request

开发说明见 [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md)。

## 联系方式

- 邮箱：qa@live.cn
- GitHub Issues：[th000cw02-afk/xiuxianguaji](https://github.com/th000cw02-afk/xiuxianguaji/issues)

## 免责声明

本应用按「现状」提供。详细条款见 [用户协议.md](用户协议.md) 与 [隐私政策.md](隐私政策.md)。

---

**修仙挂机** — 个人开发者 | 开源项目 | 免费游戏
