# 贡献指南

感谢关注本项目。欢迎通过 Issue 与 Pull Request 参与。

## 本地构建

1. 按 [docs/BUILD.md](docs/BUILD.md) 配置 Android SDK 与 `local.properties`
2. 复制 `local.properties.example` → `local.properties`，填写 `sdk.dir`
3. 正式签名：复制 `keystore.properties.example` → `keystore.properties`（勿提交）
4. Windows 可运行 `.\编译.bat`；或 `./gradlew assembleRelease`

iOS / Capacitor 见 BUILD.md 与 README 中的 `npm run ios:sync`。

## 改什么、改哪里

| 目标 | 入口 |
|------|------|
| 换皮 / 二次创作 | [docs/二次创作指南.md](docs/二次创作指南.md) |
| 玩法与壳层开发 | [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md) |
| 系统与数值概览 | [docs/GAME_SYSTEMS.md](docs/GAME_SYSTEMS.md) |
| 架构 | [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) |

题材配置优先改 `app/src/main/assets/theme/`；核心数值在 `game.js`。

## Pull Request

1. Fork 本仓库
2. 从 `main` 拉特性分支
3. 保持改动聚焦；附简要说明动机与验证方式
4. 提交 PR 到上游 `main`

## 请勿提交

- `keystore.properties`、`*.jks`、`*.keystore`
- `local.properties`
- `dist/`、`build/`、`node_modules/`、`.env*`

## 许可

贡献内容默认按仓库 [MIT License](LICENSE)（Copyright (c) 2026 tower）授权。
