# 架构说明

《修仙挂机》是 **文字修仙** 开源框架下的修仙题材实例，采用 WebView 壳 + JavaScript 游戏架构。

## 整体架构

```
┌─────────────────────────────────────────────────────────┐
│              Android 原生层（现有 Gradle 工程）           │
│  MainActivity.kt → WebView + AndroidInterface           │
├─────────────────────────────────────────────────────────┤
│              iOS 原生层（Capacitor，云端构建）            │
│  WKWebView + NativeBridge 插件 → 垫片为 AndroidInterface │
├─────────────────────────────────────────────────────────┤
│         app/src/main/assets/ 游戏层（双端共享源）         │
│  platform-bridge.js → 跨平台桥接垫片                     │
│  theme/config.js    → 题材配置（境界名、Tab、文案池）    │
│  theme/contracts.js → 天机阁契约模板                     │
│  meta-systems.js    → 纪事 / 道果 / 天机阁               │
│  game.js            → 核心数值与玩法（~2.3 万行）        │
│  index.html / styles.css / math.js                      │
└─────────────────────────────────────────────────────────┘
```

Capacitor 构建时通过 `npm run sync:web` 将 assets 复制到 `www/`，再 `cap sync ios` 写入 `ios/App/App/public/`。

## 脚本加载顺序

```
platform-bridge.js → theme/config.js → theme/contracts.js → math.js → game.js → meta-systems.js
```

`meta-systems.js` 依赖 `game.js` 的存档与 UI 函数，必须最后加载。

## 元系统（meta-systems.js）

| 模块 | 存档字段（主角） | 说明 |
|------|------------------|------|
| 修仙纪事 | `metaChronicle` | entries + titles |
| 道果图鉴 | `metaAchievements` | unlocked id 列表 |
| 进度计数 | `metaProgress` | 击杀、炼丹、契约等 |
| 天机阁 | `metaTianjige` | 天机点、日/周契约 |

触发钩子：`MetaSystems.onBreakthroughSuccess`、`onBattleWin`、`onAlchemySuccess` 等，由 `game.js` 在关键节点调用。

## 数据流

### 启动流程

1. WebView 加载 `index.html`
2. 加载 theme → game → meta-systems
3. `loadGameData()` → `MetaSystems.init()` → 各 UI 刷新

### 存档持久化

- 角色数据：`localStorage` + `StorageCache`
- 全局共享（主角存档）：返虚路、本源、纪事/成就/天机阁

### 原生桥接（AndroidInterface）

- **Android**：[`MainActivity.kt`](../app/src/main/java/com/idle/wenzixiuxian/MainActivity.kt) 注入 `AndroidInterface`
- **iOS**：[`platform-bridge.js`](../app/src/main/assets/platform-bridge.js) + [`plugins/native-bridge`](../plugins/native-bridge) 插件，挂载同名对象

见 [DEVELOPMENT.md](DEVELOPMENT.md) 或 MainActivity 注释。

## 目录职责

| 路径 | 职责 |
|------|------|
| `theme/config.js` | Fork 首选：产品名、境界、Tab、奇遇文案 |
| `theme/contracts.js` | 天机阁日/周契与商店 |
| `meta-systems.js` | 纪事、成就、天机阁逻辑 |
| `game.js` | 修炼/战斗/洞府/内天地等核心玩法 |
| `MainActivity.kt` | Android WebView 壳与 JS 桥接 |
| `plugins/native-bridge/` | iOS Capacitor 原生桥接插件 |
| `ios/` | Capacitor iOS 工程（Xcode） |

## 二次创作

换皮只需改 `theme/`，详见 [二次创作指南.md](二次创作指南.md)。

## 相关文档

- [文字修仙定位.md](文字修仙定位.md)
- [GAME_SYSTEMS.md](GAME_SYSTEMS.md)
- [ROADMAP.md](ROADMAP.md)
- [BUILD.md](BUILD.md)
- [DEVELOPMENT.md](DEVELOPMENT.md)
