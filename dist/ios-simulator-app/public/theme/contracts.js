/**
 * 天机阁 · 修行契模板（Fork 时可改为江湖任务、星际委托等）
 */
window.GAME_CONTRACTS = {
  dailyPool: [
    { id: 'kill_300', type: 'battleKills', target: 300, title: '斩妖三百', desc: '探索战斗中累计击杀 300 个敌人' },
    { id: 'kill_1000', type: 'battleKills', target: 1000, title: '千妖尽灭', desc: '探索战斗中累计击杀 1000 个敌人' },
    { id: 'breakthrough_try', type: 'breakthroughTry', target: 1, title: '破境一试', desc: '尝试境界突破 1 次' },
    { id: 'alchemy_1', type: 'alchemySuccess', target: 1, title: '丹成一颗', desc: '成功炼丹 1 次' },
    { id: 'alchemy_3', type: 'alchemySuccess', target: 3, title: '三炉丹香', desc: '成功炼丹 3 次' },
    { id: 'fanxu_1', type: 'fanxuClear', target: 1, title: '返虚一层', desc: '返虚路通关 1 层' },
    { id: 'map_battle_50', type: 'battleWins', target: 50, title: '历练五十', desc: '探索战斗胜利 50 次' }
  ],
  weeklyPool: [
    { id: 'fanxu_10', type: 'fanxuWeekly', target: 10, title: '返虚十层', desc: '本周返虚路累计通关 10 层' },
    { id: 'kill_50000', type: 'battleKills', target: 50000, title: '万妖辟易', desc: '本周探索累计击杀 50000' },
    { id: 'alchemy_20', type: 'alchemySuccess', target: 20, title: '丹道精进', desc: '本周成功炼丹 20 次' }
  ],
  dailyReward: 10,
  weeklyReward: 50,
  shop: [
    { id: 'title_wanderer', cost: 30, name: '称号·云游修士', type: 'title', value: '云游修士' },
    { id: 'title_seeker', cost: 80, name: '称号·求道者', type: 'title', value: '求道者' },
    { id: 'benyuan_5', cost: 100, name: '世界本源 ×5', type: 'benyuan', value: 5 },
    { id: 'spirit_1000', cost: 40, name: '灵石 ×1000', type: 'spiritStone', value: 1000 },
    { id: 'law_10', cost: 60, name: '法则碎片 ×10', type: 'lawFragments', value: 10 }
  ]
};
