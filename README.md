LandSpeak
基于 Lands 领地权限控制 Simple Voice Chat 语音权限的小型插件。

概览
- 功能：为 Lands 新增自定义 flag `speak`；在禁用时，处于该区域/子区且没有豁免权限的玩家无法通过 Simple Voice Chat 发送语音。
- 目标：轻量、直观、与 Lands 和 Simple Voice Chat 协同工作；在未安装 Voice Chat 时仅注册 flag 不报错。
- 适配：Paper/Purpur 1.21+（以 `api-version: 1.21` 为基准）。

依赖
- 必需：Lands（用于领地与 flag 管理）
- 推荐：Simple Voice Chat（启用语音拦截能力）

安装与更新
1) 从构建产物获取 `landspeak-<version>.jar` 并放入服务器 `plugins/` 目录。
2) 启动或重启服务器，确认控制台/`/plugins` 列表中出现 LandSpeak。
3) 安全更新：替换旧版 jar 后重启服务器即可（无数据迁移）。

使用说明
- speak flag：
  - 作用范围：领地、子区、荒野（按 Lands 的角色与层级生效）
  - 默认值：允许（玩家可发语音）
  - 配置位置：通过 Lands GUI 的“设置/角色/子区”界面切换，或按你的服务器习惯在相应设置中调整。
- 语音拦截：
  - 当玩家尝试发送语音时，若当前位置针对其角色禁止 `speak`，则拦截该语音包。
  - 拥有 `landspeak.bypass` 的玩家不受限制。

权限
- `landspeak.bypass`：在任意区域忽略 `speak` 限制（默认授予 OP）。

示例场景
- 创建“安静博物馆”区域：在该领地或其子区关闭 `speak`，玩家进入后无法发语音；讲解员（有 `landspeak.bypass`）仍可发言。
- 反刷屏：在交易市场子区关闭 `speak`，旁观者不发声；商户可通过角色设置保留发言权。

构建与开发
- 前置：JDK 21、Maven（已配置 `shade` 打包）。
- 构建：
  ```bash
  mvn -v     # 确认环境
  mvn package
  ```
  产物位于 `target/landspeak-1.0.0.jar`（以 POM 为准）。
- 开发状态：当前代码为骨架（主类 `io.github.loliiiico.landSpeak.LandSpeak`），后续将补充 flag 注册与事件拦截逻辑。

兼容性与行为约定
- 未安装 Simple Voice Chat：LandSpeak 仅注册 `speak` flag，不进行拦截。
- Lands 版本：请使用 Lands 的稳定版本；如遇 API 变更导致的兼容问题，请在 Issue 中反馈。

常见问题
- 看不到 `speak` flag？
  - 确认 LandSpeak 已启用且无报错；
  - 检查 Lands 版本与日志；
  - 若仍无效，请附日志开 Issue。
- 禁言未生效？
  - 检查玩家是否拥有 `landspeak.bypass`；
  - 确认是在对应领地/子区关闭了 `speak`，且角色设置正确；
  - 确认已安装并启用 Simple Voice Chat。

路线图（Roadmap）
- 注册自定义 RoleFlag `speak` 并在 GUI 可见
- 接入 Simple Voice Chat 事件，按 `speak` 进行拦截
- 多语言本地化支持
- 更细粒度的日志与调试开关

参考
- Lands API 文档：https://wiki.incredibleplugins.com/lands/developers/api
- Simple Voice Chat API 文档：https://modrepo.de/minecraft/voicechat/api/overview

