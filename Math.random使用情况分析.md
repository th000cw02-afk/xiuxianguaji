# Math.random() 使用情况分析文档

> **说明**：本文档列出所有使用 `Math.random()` 的地方（受气运影响的随机数）
> 
> **气运影响机制**：
> - `Math.random()` 返回值 = 原随机数 × 系数
> - 系数 = 1 / (1 + 气运值 / 100)，范围 [0, 1]
> - 气运越大，系数越小，随机数越倾向于小值（利于玩家）
> - 对于概率判定 `Math.random() < p`，偏移后实际概率 = min(1, p / 系数)

---

## 一、概率判定类（直接使用随机数）

### 1. 时间结晶掉落判定
**位置**：[game.js:1799](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L1799)
```javascript
if (Math.random() < fracPart) {
    return intPart + 1;
}
```
- **用途**：时间结晶掉落数量判定（保底+概率机制）
- **触发条件**：`Math.random() < fracPart`，fracPart为小数部分
- **使用方式**：直接随机数
- **影响**：气运提高 → 实际掉落概率提高 → 时间结晶掉落更多

---

### 2. 境界突破成功率判定
**位置**：[game.js:6160](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L6160)
```javascript
if (Math.random() < successRateNum) {
    player.level += 1;
    // 突破成功
}
```
- **用途**：境界突破是否成功
- **触发条件**：`Math.random() < successRateNum`，successRateNum为突破成功率
- **使用方式**：直接随机数
- **影响**：气运提高 → 实际突破概率提高 → 更容易突破成功

---

### 3. 灵石掉落判定
**位置**：[game.js:10743](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L10743)
```javascript
if (Math.random() < 0.01) {
    // 获得灵石
}
```
- **用途**：战斗胜利后灵石掉落
- **触发条件**：`Math.random() < 0.01`（1%概率）
- **使用方式**：直接随机数
- **影响**：气运提高 → 实际掉落概率提高 → 灵石掉落更频繁

---

### 4. 仙石掉落判定
**位置**：[game.js:10749](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L10749)
```javascript
if (Math.random() < 0.0002) {
    // 获得仙石
}
```
- **用途**：战斗胜利后仙石掉落
- **触发条件**：`Math.random() < 0.0002`（0.02%概率）
- **使用方式**：直接随机数
- **影响**：气运提高 → 实际掉落概率提高 → 仙石掉落更频繁

---

### 5. 法则碎片掉落判定
**位置**：[game.js:10759](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L10759)
```javascript
if (Math.random() < 0.05) {
    // 获得法则碎片
}
```
- **用途**：战斗胜利后法则碎片掉落
- **触发条件**：`Math.random() < 0.05`（5%概率）
- **使用方式**：直接随机数
- **影响**：气运提高 → 实际掉落概率提高 → 法则碎片掉落更频繁

---

### 6. 仙草掉落判定
**位置**：[game.js:10768](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L10768)
```javascript
if (Math.random() < 0.01) {
    // 获得仙草
}
```
- **用途**：战斗胜利后仙草掉落
- **触发条件**：`Math.random() < 0.01`（1%概率）
- **使用方式**：直接随机数
- **影响**：气运提高 → 实际掉落概率提高 → 仙草掉落更频繁

---

### 7. 时间结晶掉落判定（普通战斗）
**位置**：[game.js:10798](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L10798)
```javascript
if (Math.random() < 1/6000) {
    // 获得时间结晶
}
```
- **用途**：普通战斗时间结晶掉落
- **触发条件**：`Math.random() < 1/6000`（约0.0167%概率）
- **使用方式**：直接随机数
- **影响**：气运提高 → 实际掉落概率提高 → 时间结晶掉落更频繁

---

### 8. 技能传承概率判定
**位置**：[game.js:13564](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L13564)
```javascript
if (unit.skills && unit.skills.length > 0 && Math.random() < 0.2) {
    // 技能传承
}
```
- **用途**：单位死亡时技能传承判定
- **触发条件**：`Math.random() < 0.2`（20%概率）
- **使用方式**：直接随机数
- **影响**：气运提高 → 实际传承概率提高 → 技能更容易传承

---

### 9. 玩家出手概率判定
**位置**：[game.js:14480](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L14480)
```javascript
if (Math.random() < actChance) {
    // 玩世界探索玩家出手
}
```
- **用途**：环世界探索中玩家是否出手
- **触发条件**：`Math.random() < actChance`，actChance = 速度/100
- **使用方式**：直接随机数
- **影响**：气运提高 → 实际出手概率提高 → 更容易出手

---

### 10. 敌人出手概率判定（反向）
**位置**：[game.js:14506](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L14506)
```javascript
if (Math.random() >= (1 - actChance)) {
    // 敌人出手
}
```
- **用途**：环世界探索中敌人是否出手
- **触发条件**：`Math.random() >= (1 - actChance)`，等价于 `Math.random() < actChance`
- **使用方式**：直接随机数
- **影响**：气运提高 → 随机数倾向小值 → `Math.random() >= (1 - actChance)` 更难成立 → 敌人更难出手

---

### 11. 玩家出手概率判定（另一处）
**位置**：[game.js:14608](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L14608)
```javascript
if (Math.random() < actChance) {
    // 玩世界探索玩家出手
}
```
- **用途**：环世界探索中玩家是否出手（另一处）
- **触发条件**：`Math.random() < actChance`
- **使用方式**：直接随机数
- **影响**：气运提高 → 实际出手概率提高 → 更容易出手

---

### 12. 敌人出手概率判定（反向，另一处）
**位置**：[game.js:14620](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L14620)
```javascript
if (Math.random() >= (1 - actChance)) {
    // 敌人出手
}
```
- **用途**：环世界探索中敌人是否出手（另一处）
- **触发条件**：`Math.random() >= (1 - actChance)`
- **使用方式**：直接随机数
- **影响**：气运提高 → 敌人更难出手

---

### 13. 炼丹成功率判定
**位置**：[game.js:19754](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L19754)
```javascript
const isSuccess = Math.random() < successRate;
```
- **用途**：炼丹是否成功
- **触发条件**：`Math.random() < successRate`
- **使用方式**：直接随机数
- **影响**：气运提高 → 实际成功率提高 → 炼丹更容易成功

---

### 14. 批量炼丹成功率判定
**位置**：[game.js:19855](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L19855)
```javascript
const isSuccess = Math.random() < successRate;
```
- **用途**：批量炼丹中每次炼制是否成功
- **触发条件**：`Math.random() < successRate`
- **使用方式**：直接随机数
- **影响**：气运提高 → 实际成功率提高 → 炼丹更容易成功

---

### 15. 炼丹成功率判定（另一处）
**位置**：[game.js:20338](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L20338)
```javascript
const isSuccess = Math.random() < successRate;
```
- **用途**：炼丹是否成功（另一处）
- **触发条件**：`Math.random() < successRate`
- **使用方式**：直接随机数
- **影响**：气运提高 → 实际成功率提高 → 炼丹更容易成功

---

## 二、随机值生成类（用于计算）

### 16. 灵药阶位计算
**位置**：[game.js:1974](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L1974)
```javascript
const r = Math.random();
if (r === 0) return 0;
const logR = parseFloat((-Math.log10(r)).toFixed(1));
```
- **用途**：计算灵药阶位
- **触发条件**：每次掉落灵草时调用
- **使用方式**：直接随机数，用于对数计算
- **影响**：气运提高 → r倾向小值 → logR倾向大值 → 灵药阶位倾向更高

---

### 17. 批量灵药阶位计算
**位置**：[game.js:1995](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L1995)
```javascript
const r = Math.random();
if (r === 0) continue;
const logR = parseFloat((-Math.log10(r)).toFixed(1));
```
- **用途**：批量计算灵药阶位
- **触发条件**：批量掉落灵草时调用
- **使用方式**：直接随机数，用于对数计算
- **影响**：气运提高 → 灵药阶位倾向更高

---

### 18. 仙草阶位计算
**位置**：[game.js:10774](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L10774)
```javascript
const r = Math.random();
const grade = calcHerbGrade(monsterLevel);
```
- **用途**：仙草阶位计算
- **触发条件**：仙草掉落时调用
- **使用方式**：直接随机数（通过calcHerbGrade函数）
- **影响**：气运提高 → 仙草阶位倾向更高

---

### 19. 技能数量判定
**位置**：[game.js:13486](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L13486)
```javascript
const rand = Math.random() * 100;
if (rand < 13) return 1;
if (rand < 25) return 2;
// ...
```
- **用途**：决定世界探索单位的技能数量
- **触发条件**：生成世界探索单位时
- **使用方式**：直接随机数 × 100
- **影响**：气运提高 → rand倾向小值 → 玩家单位技能数量倾向更多，敌人单位技能数量倾向更少

---

### 20. 技能数值生成（世界探索）
**位置**：[game.js:13504](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L13504)
```javascript
const rand = Math.random();
// 例如：values.healPercent = isEnemy ? Math.floor(50 + rand * 150) : randomStep(50, 200);
```
- **用途**：生成世界探索技能的数值
- **触发条件**：生成技能时
- **使用方式**：
  - 敌人：`Math.floor(基础值 + rand * 范围)` → 直接随机数
  - 玩家：`randomStep(最小值, 最大值)` → 受气运影响
- **影响**：气运提高 → 敌人技能数值倾向更低，玩家技能数值倾向更优

---

### 21. 技能数值生成（环世界）
**位置**：[game.js:13921](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L13921)
```javascript
const rand = Math.random();
// 例如：values.healPercent = isEnemy ? Math.floor(50 + rand * 150) : Math.floor(200 - rand * 150);
```
- **用途**：生成环世界技能的数值
- **触发条件**：生成技能时
- **使用方式**：
  - 敌人：`Math.floor(基础值 + rand * 范围)` → 直接随机数
  - 玩家：`Math.floor(最大值 - rand * 范围)` → 1-随机数效果
- **影响**：气运提高 → 敌人技能数值倾向更低，玩家技能数值倾向更高

---

### 22. 技能数值生成（环世界另一处）
**位置**：[game.js:14861](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L14861)
```javascript
const rand = Math.random();
// 例如：values.healPercent = Math.floor(200 - rand * 150);
```
- **用途**：生成环世界技能的数值（另一处）
- **触发条件**：生成技能时
- **使用方式**：`Math.floor(最大值 - rand * 范围)` → 1-随机数效果
- **影响**：气运提高 → rand倾向小值 → 技能数值倾向更高

---

### 23. 怪物属性波动
**位置**：[game.js:9401](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L9401)
```javascript
const fluctuationLevels = [0.85, 0.90, 0.95, 1.00, 1.05, 1.10, 1.15];
const fluctuation = fluctuationLevels[Math.floor(Math.random() * fluctuationLevels.length)];
```
- **用途**：怪物基础属性波动系数
- **触发条件**：生成怪物时
- **使用方式**：直接随机数选择波动档位
- **影响**：气运提高 → Math.random()倾向小值 → 波动系数倾向更小 → 怪物属性倾向更低

---

### 24. 怪物技能数量
**位置**：[game.js:9459](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L9459)
```javascript
const numSkills = 2 + $floor(Math.random() * 2);
```
- **用途**：怪物拥有的技能数量（2-3个）
- **触发条件**：生成怪物时
- **使用方式**：直接随机数
- **影响**：气运提高 → Math.random()倾向小值 → 怪物技能数量倾向更少

---

### 25. 敌人数量
**位置**：[game.js:13884](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L13884)
```javascript
enemyCount: Math.floor(9000 - Math.random() * 3001),
```
- **用途**：环世界敌人数量（5999-9000）
- **触发条件**：生成环世界时
- **使用方式**：`Math.floor(9000 - Math.random() * 3001)` → 1-随机数效果
- **影响**：气运提高 → Math.random()倾向小值 → 敌人数量倾向更少

---

### 26. 额外等级增益
**位置**：[game.js:17773](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L17773)
```javascript
const baseExtraLevelGain = Math.max(1, Math.floor(Math.random() * maxGain) + 1);
```
- **用途**：论道额外等级增益
- **触发条件**：论道时
- **使用方式**：直接随机数
- **影响**：气运提高 → Math.random()倾向小值 → 额外等级增益倾向更小（此处对玩家不利）

---

### 27. 随机偏移（浮动数字显示）
**位置**：[game.js:16352](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L16352)
```javascript
const randomOffset = Math.floor(Math.random() * 30) - 15;
```
- **用途**：浮动数字显示的水平偏移（-15到15像素）
- **触发条件**：显示浮动数字时
- **使用方式**：直接随机数
- **影响**：气运提高 → 偏移倾向负值 → 浮动数字显示位置偏左

---

## 三、随机索引选择类

### 28. 怪物索引选择（fallback）
**位置**：[game.js:9389](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L9389)
```javascript
const monsterIndex = Math.floor(Math.random() * fallbackMonsters.length);
```
- **用途**：选择fallback怪物
- **触发条件**：地图数据异常时
- **使用方式**：直接随机数
- **影响**：气运提高 → 怪物索引倾向更小 → 怪物等级倾向更低

---

### 29. 怪物索引选择
**位置**：[game.js:9393](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L9393)
```javascript
const monsterIndex = Math.floor(Math.random() * mapMonsters.length);
```
- **用途**：选择怪物
- **触发条件**：生成怪物时
- **使用方式**：直接随机数
- **影响**：气运提高 → 怪物索引倾向更小 → 怪物等级倾向更低

---

### 30. 技能选择（传承）
**位置**：[game.js:13569](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L13569)
```javascript
const skillToInherit = inheritableSkills[Math.floor(Math.random() * inheritableSkills.length)];
```
- **用途**：选择要传承的技能
- **触发条件**：技能传承时
- **使用方式**：直接随机数
- **影响**：气运提高 → 倾向选择数组前面的技能

---

### 31. 目标单位选择（传承）
**位置**：[game.js:13570](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L13570)
```javascript
const targetUnit = aliveAllies[Math.floor(Math.random() * aliveAllies.length)];
```
- **用途**：选择技能传承的目标单位
- **触发条件**：技能传承时
- **使用方式**：直接随机数
- **影响**：气运提高 → 倾向选择数组前面的单位

---

### 32. 攻击目标选择
**位置**：[game.js:14692](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L14692)
```javascript
const target = targets[Math.floor(Math.random() * targets.length)];
```
- **用途**：选择攻击目标
- **触发条件**：执行攻击时
- **使用方式**：直接随机数
- **影响**：气运提高 → 倾向选择数组前面的目标

---

### 33. 随机索引选择（通用）
**位置**：[game.js:3011](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L3011)
```javascript
const randomIndex = Math.floor(Math.random() * list.length);
```
- **用途**：通用随机索引选择
- **触发条件**：需要随机选择列表元素时
- **使用方式**：直接随机数
- **影响**：气运提高 → 倾向选择数组前面的元素

---

### 34. 随机索引选择（另一处）
**位置**：[game.js:18791](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L18791)
```javascript
const randomIndex = Math.floor(Math.random() * list.length);
```
- **用途**：通用随机索引选择（另一处）
- **触发条件**：需要随机选择列表元素时
- **使用方式**：直接随机数
- **影响**：气运提高 → 倾向选择数组前面的元素

---

## 四、特殊用途类

### 35. 随机数组生成
**位置**：[game.js:9495](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L9495)
```javascript
const randomArray = Array.from({length: 1000}, () => Math.random());
```
- **用途**：预生成1000个随机数用于战斗模拟
- **触发条件**：战斗预计算时
- **使用方式**：直接随机数
- **影响**：气运提高 → 所有随机数倾向小值 → 战斗模拟结果倾向有利

---

### 36. 获取下一个随机数
**位置**：[game.js:9622](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L9622)
```javascript
return Math.random();
```
- **用途**：当预生成的随机数用完时，生成新的随机数
- **触发条件**：战斗模拟中随机数耗尽时
- **使用方式**：直接随机数
- **影响**：气运提高 → 随机数倾向小值

---

### 37. 波动选择（多处）
**位置**：
- [game.js:13981](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L13981)
- [game.js:14921](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L14921)
- [game.js:15511](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L15511)
- [game.js:15587](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L15587)
- [game.js:15630](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L15630)
- [game.js:15674](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L15674)

```javascript
const fluctuation = fluctuationLevels[Math.floor(Math.random() * fluctuationLevels.length)];
```
- **用途**：单位属性波动系数
- **触发条件**：生成单位时
- **使用方式**：直接随机数选择波动档位
- **影响**：气运提高 → 波动系数倾向更小 → 单位属性倾向更低

---

### 38. randomStep函数
**位置**：[game.js:99](file:///c:/Users/zhuyue/Desktop/xiuxianguaji/app/src/main/assets/game.js#L99)
```javascript
function randomStep(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}
```
- **用途**：生成指定范围内的随机整数
- **触发条件**：多处调用
- **使用方式**：直接随机数
- **影响**：气运提高 → 返回值倾向更小 → 倾向返回最小值附近

---

## 总结

### 气运对游戏的影响总结：

**有利影响**（气运提高，玩家受益）：
1. ✅ 掉落概率提高（时间结晶、灵石、仙石、法则碎片、仙草）
2. ✅ 突破成功率提高
3. ✅ 炼丹成功率提高
4. ✅ 灵药/仙草阶位倾向更高
5. ✅ 怪物属性倾向更低
6. ✅ 怪物技能数量倾向更少
7. ✅ 敌人数量倾向更少
8. ✅ 玩家更容易出手，敌人更难出手
9. ✅ 技能传承概率提高
10. ✅ 玩家技能数值倾向更优

**不利影响**（气运提高，玩家受损）：
1. ❌ 论道额外等级增益倾向更小

**中性影响**：
1. ➖ 随机索引选择倾向数组前面（取决于数组排序）
2. ➖ 浮动数字显示位置偏左（纯视觉效果）

---

**文档生成时间**：2026-05-16
**文件路径**：`c:\Users\zhuyue\Desktop\xiuxianguaji\app\src\main\assets\game.js`
