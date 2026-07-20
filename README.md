# 修仙挂机 / 文字修仙

> **文字修仙** 开源框架下的修仙题材实例 — 单机 MUD 风挂机游戏，完全免费开源。

本仓库是 [文字修仙](docs/文字修仙定位.md) 框架的 Demo 实现。Fork 后可替换 `theme/config.js` 快速换皮，避免与同类修仙挂机产品同质化。详见 [二次创作指南](docs/二次创作指南.md)。

## 项目简介

无联网、数据本地存储。从炼气到化神，含洞府、法则、炼丹、化身、内天地、返虚路、韵灵等系统；v1.73 起新增 **修仙纪事**、**道果图鉴**、**天机阁·修行契**。

## 功能特性

- **文字修仙框架**：MUD 终端 UI + 可配置 theme 层
- **单机挂机**：自动修炼/战斗/离线收益
- **气运体系**：返虚路层数影响全局随机
- **多角色化身**：最多 4 角色，共享本源
- **纪事 / 道果 / 天机阁**：叙事反馈与短期目标

## 技术栈

| 项 | 说明 |
|----|------|
| 平台 | Android 7.0+；iOS 14+（Capacitor，云端构建） |
| 引擎 | WebView + JavaScript + math.js |
| 构建 | Android：Gradle 8.13；iOS：Capacitor 7 + Xcode，见 [docs/BUILD.md](docs/BUILD.md) |

## 项目结构

```
app/src/main/assets/
├── platform-bridge.js   # 跨平台原生桥接垫片
├── theme/config.js      # 题材配置（Fork 首选）
├── theme/contracts.js   # 天机阁契约模板
├── meta-systems.js      # 纪事/成就/天机阁
├── game.js              # 核心数值与玩法
├── index.html / styles.css
plugins/native-bridge/   # iOS Capacitor 原生插件
ios/                     # Capacitor iOS 工程
docs/
```

## 快速构建

**Android（Windows / macOS / Linux）：**

```powershell
copy local.properties.example local.properties
# 编辑 sdk.dir
.\编译.bat
# → app\build\outputs\apk\release\app-release.apk
```

**iOS（需 Mac 或 GitHub Actions）：**

```powershell
npm install
npm run ios:sync    # 同步 Web 资源到 Capacitor
npm run ios:open    # Mac 上打开 Xcode
```

推送代码后，GitHub Actions 会自动构建 Android APK 与 iOS Simulator 包，在 Actions → Artifacts 下载。

## 文档索引

| 文档 | 说明 |
|------|------|
| [文字修仙定位](docs/文字修仙定位.md) | 框架定位与差异化 |
| [GAME_SYSTEMS](docs/GAME_SYSTEMS.md) | 系统全景 |
| [二次创作指南](docs/二次创作指南.md) | Fork 换皮步骤 |
| [ROADMAP](docs/ROADMAP.md) | 版本规划 |
| [BUILD](docs/BUILD.md) | 编译与环境 |
| [ARCHITECTURE](docs/ARCHITECTURE.md) | 架构说明 |
| [随机数说明](docs/随机数说明文档.md) | 气运与随机数设计 |
| [游戏说明.txt](app/src/main/assets/游戏说明.txt) | 公式与机制 |
| [CONTRIBUTING](CONTRIBUTING.md) | 如何参与贡献 |

## 开源

MIT License — Copyright (c) 2026 tower。详见 [LICENSE](LICENSE)。第三方：[math.js](https://github.com/josdejong/mathjs) (Apache 2.0)。

问题与贡献请通过 [Issues](https://github.com/th000cw02-afk/xiuxianguaji/issues) 与 [CONTRIBUTING.md](CONTRIBUTING.md)。
