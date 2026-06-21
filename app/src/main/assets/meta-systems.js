/**
 * 文字修仙 — 元系统：纪事、道果图鉴、天机阁
 * 依赖 game.js 加载后执行
 */
(function () {
    'use strict';

    const MAX_CHRONICLE = 20;

    const ACHIEVEMENT_DEFS = [
        { id: 'realm_1', cat: '境界', name: '初窥门径', desc: '突破至筑基', check: p => p.realmIndex >= 1 },
        { id: 'realm_2', cat: '境界', name: '金丹在腹', desc: '突破至金丹', check: p => p.realmIndex >= 2 },
        { id: 'realm_3', cat: '境界', name: '元婴出窍', desc: '突破至元婴', check: p => p.realmIndex >= 3 },
        { id: 'realm_4', cat: '境界', name: '化神归真', desc: '突破至化神', check: p => p.realmIndex >= 4 },
        { id: 'map_10', cat: '探索', name: '十重天外', desc: '解锁地图 10', check: p => (p.unlockedMaps || []).filter(m => m <= 99).length >= 10 },
        { id: 'map_30', cat: '探索', name: '踏遍三十天', desc: '解锁地图 30', check: p => (p.unlockedMaps || []).filter(m => m <= 99).length >= 30 },
        { id: 'map_50', cat: '探索', name: '五十重境', desc: '解锁地图 50', check: p => (p.unlockedMaps || []).filter(m => m <= 99).length >= 50 },
        { id: 'void_unlock', cat: '探索', name: '虚空初探', desc: '解锁无尽虚空', check: p => (p.unlockedMaps || []).includes(101) },
        { id: 'void_10', cat: '探索', name: '深入虚空', desc: '无尽虚空深度达 10', check: p => (p.maxVoidVictoryLevel || 0) >= 10 },
        { id: 'kill_1k', cat: '战斗', name: '千人斩', desc: '累计击杀 1000', check: (_, pr) => (pr.battleKills || 0) >= 1000 },
        { id: 'kill_10k', cat: '战斗', name: '万妖斩', desc: '累计击杀 10000', check: (_, pr) => (pr.battleKills || 0) >= 10000 },
        { id: 'kill_100k', cat: '战斗', name: '屠魔百万', desc: '累计击杀 100000', check: (_, pr) => (pr.battleKills || 0) >= 100000 },
        { id: 'field_5', cat: '洞府', name: '灵田初成', desc: '灵田 5 级', check: p => (p.spiritFieldLevel || 0) >= 5 },
        { id: 'field_10', cat: '洞府', name: '灵田丰收', desc: '灵田 10 级', check: p => (p.spiritFieldLevel || 0) >= 10 },
        { id: 'gather_10', cat: '洞府', name: '聚灵大成', desc: '聚灵阵 10 级', check: p => (p.gatheringArrayLevel || 0) >= 10 },
        { id: 'clone_1', cat: '化身', name: '身外化身', desc: '创建第 1 个化身', check: () => getCharacterList().filter(c => c).length >= 2 },
        { id: 'clone_absorb', cat: '化身', name: '归元合一', desc: '吸收过 1 个化身', check: (_, pr) => (pr.cloneAbsorbCount || 0) >= 1 },
        { id: 'shengling', cat: '内天地', name: '开天辟地', desc: '灵族数量大于 0', check: p => typeof $gtBig === 'function' && $gtBig(String(p.shengLing || 0), '0') },
        { id: 'fanxu_10', cat: '返虚', name: '虚境初行', desc: '返虚路 10 层', check: () => getFanXuLuFloor() >= 10 },
        { id: 'fanxu_50', cat: '返虚', name: '虚境行者', desc: '返虚路 50 层', check: () => getFanXuLuFloor() >= 50 },
        { id: 'fanxu_100', cat: '返虚', name: '返虚百层', desc: '返虚路 100 层', check: () => getFanXuLuFloor() >= 100 },
        { id: 'alchemy_10', cat: '炼丹', name: '丹道入门', desc: '成功炼丹 10 次', check: (_, pr) => (pr.alchemySuccess || 0) >= 10 },
        { id: 'alchemy_100', cat: '炼丹', name: '丹道宗师', desc: '成功炼丹 100 次', check: (_, pr) => (pr.alchemySuccess || 0) >= 100 },
        { id: 'law_100', cat: '法则', name: '法则百级', desc: '任一法则等级达 100', check: p => {
            const laws = p.laws || {};
            return Object.values(laws).some(v => Number(v) >= 100);
        }},
        { id: 'inner_50', cat: '修炼', name: '五十重境', desc: '内部等级 50', check: p => (p.innerLevel || 0) >= 50 },
        { id: 'inner_100', cat: '修炼', name: '百重天', desc: '内部等级 100', check: p => (p.innerLevel || 0) >= 100 },
        { id: 'offline_7d', cat: '隐藏', name: '气运之子', desc: '单次离线超过 7 天', check: (_, pr) => (pr.maxOfflineOnce || 0) >= 7 * 86400 },
        { id: 'contract_10', cat: '天机', name: '履约十契', desc: '完成 10 个修行契', check: (_, pr) => (pr.contractsCompleted || 0) >= 10 },
        { id: 'chronicle_50', cat: '纪事', name: '卷帙五十', desc: '纪事条目达 50 条', check: m => (m.chronicle.entries || []).length >= 50 },
        { id: 'title_3', cat: '天机', name: '名号三重', desc: '拥有 3 个称号', check: m => (m.chronicle.titles || []).length >= 3 },
        { id: 'win_500', cat: '战斗', name: '百战不殆', desc: '探索胜利 500 次', check: (_, pr) => (pr.battleWins || 0) >= 500 }
    ];

    function getMainCharId() {
        const list = getCharacterList();
        return list[0] ? list[0].id : null;
    }

    function readMainSave() {
        const id = getMainCharId();
        if (!id) return null;
        const raw = localStorage.getItem(getCharacterSaveKey(id));
        if (!raw) return null;
        try { return JSON.parse(raw); } catch (e) { return null; }
    }

    function writeMainSave(mutator) {
        const id = getMainCharId();
        if (!id) return;
        const data = readMainSave() || {};
        mutator(data);
        localStorage.setItem(getCharacterSaveKey(id), JSON.stringify(data));
        if (typeof player !== 'undefined' && getCurrentCharId() === id) {
            if (data.metaChronicle) player.metaChronicle = data.metaChronicle;
            if (data.metaAchievements) player.metaAchievements = data.metaAchievements;
            if (data.metaTianjige) player.metaTianjige = data.metaTianjige;
        }
    }

    function defaultMeta() {
        return {
            chronicle: { entries: [], titles: [] },
            achievements: { unlocked: [] },
            progress: {
                battleKills: 0, battleWins: 0, alchemySuccess: 0,
                breakthroughTry: 0, fanxuWeekly: 0, fanxuClear: 0,
                contractsCompleted: 0, cloneAbsorbCount: 0, maxOfflineOnce: 0
            },
            tianjige: {
                points: 0,
                dailyDate: '',
                weeklyKey: '',
                daily: [],
                weekly: null,
                dailyClaimed: [],
                weeklyClaimed: false
            }
        };
    }

    function getMeta() {
        const data = readMainSave();
        if (!data) return defaultMeta();
        if (!data.metaChronicle) data.metaChronicle = { entries: [], titles: [] };
        if (!data.metaAchievements) data.metaAchievements = { unlocked: [] };
        if (!data.metaProgress) data.metaProgress = defaultMeta().progress;
        if (!data.metaTianjige) data.metaTianjige = defaultMeta().tianjige;
        return {
            chronicle: data.metaChronicle,
            achievements: data.metaAchievements,
            progress: data.metaProgress,
            tianjige: data.metaTianjige
        };
    }

    function saveMeta(meta) {
        writeMainSave(data => {
            data.metaChronicle = meta.chronicle;
            data.metaAchievements = meta.achievements;
            data.metaProgress = meta.progress;
            data.metaTianjige = meta.tianjige;
        });
    }

    function formatChronicleTime(ts) {
        const d = new Date(ts);
        return `${d.getMonth() + 1}/${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
    }

    function appendChronicle(text, type) {
        if (!text) return;
        const meta = getMeta();
        meta.chronicle.entries.unshift({
            id: Date.now() + '_' + Math.randomNeutral().toString(36).slice(2, 6),
            time: Date.now(),
            text: String(text),
            type: type || 'info'
        });
        if (meta.chronicle.entries.length > MAX_CHRONICLE) {
            meta.chronicle.entries.length = MAX_CHRONICLE;
        }
        saveMeta(meta);
        updateChronicleUI();
    }

    function addTitle(title) {
        if (!title) return;
        const meta = getMeta();
        if (!meta.chronicle.titles.includes(title)) {
            meta.chronicle.titles.push(title);
            saveMeta(meta);
        }
    }

    function trackProgress(key, delta) {
        const meta = getMeta();
        meta.progress[key] = (meta.progress[key] || 0) + (delta || 1);
        saveMeta(meta);
        refreshContracts(key, delta || 1);
        checkAchievements();
    }

    function checkAchievements() {
        const meta = getMeta();
        const p = typeof player !== 'undefined' ? player : {};
        let changed = false;
        ACHIEVEMENT_DEFS.forEach(def => {
            if (meta.achievements.unlocked.includes(def.id)) return;
            try {
                if (def.check(p, meta.progress, meta)) {
                    meta.achievements.unlocked.push(def.id);
                    appendChronicle(`【道果】达成「${def.name}」：${def.desc}`, 'achievement');
                    changed = true;
                }
            } catch (e) { /* ignore */ }
        });
        if (changed) saveMeta(meta);
    }

    function getWeekKey() {
        const d = new Date();
        const onejan = new Date(d.getFullYear(), 0, 1);
        const week = Math.ceil((((d - onejan) / 86400000) + onejan.getDay() + 1) / 7);
        return `${d.getFullYear()}-W${week}`;
    }

    function pickRandom(arr, n) {
        const copy = arr.slice();
        const out = [];
        while (out.length < n && copy.length) {
            const i = Math.floor(Math.randomNeutral() * copy.length);
            out.push(copy.splice(i, 1)[0]);
        }
        return out;
    }

    function ensureTianjigeContracts() {
        const meta = getMeta();
        const tj = meta.tianjige;
        const today = new Date().toDateString();
        const weekKey = getWeekKey();
        const contracts = window.GAME_CONTRACTS || { dailyPool: [], weeklyPool: [], dailyReward: 10, weeklyReward: 50 };

        if (tj.dailyDate !== today) {
            tj.dailyDate = today;
            tj.daily = pickRandom(contracts.dailyPool || [], 3).map(c => ({ ...c, progress: 0, done: false }));
            tj.dailyClaimed = [];
        }
        if (tj.weeklyKey !== weekKey) {
            tj.weeklyKey = weekKey;
            const w = pickRandom(contracts.weeklyPool || [], 1)[0];
            tj.weekly = w ? { ...w, progress: 0, done: false } : null;
            tj.weeklyClaimed = false;
            meta.progress.fanxuWeekly = 0;
        }
        saveMeta(meta);
        return meta;
    }

    function refreshContracts(progressKey, delta) {
        const meta = ensureTianjigeContracts();
        const tj = meta.tianjige;
        let changed = false;

        tj.daily.forEach(c => {
            if (c.done || c.type !== progressKey) return;
            c.progress = (c.progress || 0) + delta;
            if (c.progress >= c.target) {
                c.progress = c.target;
                c.done = true;
                appendChronicle(`【天机阁】日契完成：${c.title}`, 'contract');
            }
            changed = true;
        });

        if (tj.weekly && !tj.weekly.done && tj.weekly.type === progressKey) {
            tj.weekly.progress = (tj.weekly.progress || 0) + delta;
            if (tj.weekly.progress >= tj.weekly.target) {
                tj.weekly.progress = tj.weekly.target;
                tj.weekly.done = true;
                appendChronicle(`【天机阁】周契完成：${tj.weekly.title}`, 'contract');
            }
            changed = true;
        }

        if (progressKey === 'fanxuClear') {
            meta.progress.fanxuWeekly = (meta.progress.fanxuWeekly || 0) + delta;
            if (tj.weekly && tj.weekly.type === 'fanxuWeekly' && !tj.weekly.done) {
                tj.weekly.progress = meta.progress.fanxuWeekly;
                if (tj.weekly.progress >= tj.weekly.target) {
                    tj.weekly.progress = tj.weekly.target;
                    tj.weekly.done = true;
                    appendChronicle(`【天机阁】周契完成：${tj.weekly.title}`, 'contract');
                }
            }
        }

        if (changed) saveMeta(meta);
    }

    function claimContract(id, isWeekly) {
        const meta = ensureTianjigeContracts();
        const tj = meta.tianjige;
        const contracts = window.GAME_CONTRACTS || { dailyReward: 10, weeklyReward: 50 };

        if (isWeekly) {
            if (!tj.weekly || !tj.weekly.done || tj.weeklyClaimed) return;
            if (tj.weekly.id !== id) return;
            tj.weeklyClaimed = true;
            tj.points = (tj.points || 0) + (contracts.weeklyReward || 50);
            meta.progress.contractsCompleted = (meta.progress.contractsCompleted || 0) + 1;
            appendChronicle(`【天机阁】领取周契奖励，天机点 +${contracts.weeklyReward || 50}`, 'contract');
        } else {
            const c = tj.daily.find(x => x.id === id);
            if (!c || !c.done || tj.dailyClaimed.includes(id)) return;
            tj.dailyClaimed.push(id);
            tj.points = (tj.points || 0) + (contracts.dailyReward || 10);
            meta.progress.contractsCompleted = (meta.progress.contractsCompleted || 0) + 1;
            appendChronicle(`【天机阁】领取日契「${c.title}」，天机点 +${contracts.dailyReward || 10}`, 'contract');
        }
        saveMeta(meta);
        checkAchievements();
    }

    function buyShopItem(shopId) {
        const meta = getMeta();
        const shop = (window.GAME_CONTRACTS && window.GAME_CONTRACTS.shop) || [];
        const item = shop.find(s => s.id === shopId);
        if (!item) return;
        if ((meta.tianjige.points || 0) < item.cost) {
            showMsgModal('天机点不足', `需要 ${item.cost} 天机点，当前 ${meta.tianjige.points || 0}。`);
            return;
        }
        meta.tianjige.points -= item.cost;
        if (item.type === 'title') {
            addTitle(item.value);
            appendChronicle(`【天机阁】兑换称号「${item.value}」`, 'shop');
        } else if (item.type === 'benyuan') {
            addFirstCharacterBenYuan(item.value);
            appendChronicle(`【天机阁】兑换世界本源 ×${item.value}`, 'shop');
        } else if (item.type === 'spiritStone' && typeof player !== 'undefined') {
            player.spiritStone = $addBig(player.spiritStone || '0', String(item.value));
            saveGameData();
            appendChronicle(`【天机阁】兑换灵石 ×${item.value}`, 'shop');
        } else if (item.type === 'lawFragments' && typeof player !== 'undefined') {
            player.lawFragments = $addBig(player.lawFragments || '0', String(item.value));
            saveGameData();
            appendChronicle(`【天机阁】兑换法则碎片 ×${item.value}`, 'shop');
        }
        saveMeta(meta);
        showTianjigeModal();
    }

    function updateChronicleUI() {
        const panel = document.getElementById('chronicle-panel');
        if (!panel) return;
        const meta = getMeta();
        const entries = meta.chronicle.entries || [];
        const titles = meta.chronicle.titles || [];
        let html = '<div class="text-line" style="text-align:center;color:#ff0;cursor:pointer;" onclick="MetaSystems.toggleChronicle()">❖修仙纪事❖</div>';
        if (MetaSystems._chronicleCollapsed) {
            panel.innerHTML = html + '<div class="text-line" style="text-align:center;color:#888;font-size:11px;">点击展开</div>';
            return;
        }
        if (titles.length) {
            html += `<div class="text-line" style="font-size:11px;color:#afa;">称号：${titles.slice(-3).join(' · ')}</div>`;
        }
        if (!entries.length) {
            html += '<div class="text-line" style="color:#888;font-size:11px;">暂无纪事，修行路上自有记载。</div>';
        } else {
            entries.slice(0, 8).forEach(e => {
                html += `<div class="text-line" style="font-size:11px;color:#ccc;">[${formatChronicleTime(e.time)}] ${escapeHtml(e.text)}</div>`;
            });
        }
        panel.innerHTML = html;
    }

    function showDaoguoModal() {
        const meta = getMeta();
        const unlocked = new Set(meta.achievements.unlocked || []);
        const byCat = {};
        ACHIEVEMENT_DEFS.forEach(def => {
            if (!byCat[def.cat]) byCat[def.cat] = [];
            byCat[def.cat].push(def);
        });
        let html = '<span style="color:#ff0;">【 道果图鉴 】</span><br><br>';
        html += `<span style="color:#888;">已达成 ${unlocked.size} / ${ACHIEVEMENT_DEFS.length}</span><br><br>`;
        Object.keys(byCat).forEach(cat => {
            html += `<span style="color:#afa;">◆ ${cat}</span><br>`;
            byCat[cat].forEach(def => {
                const mark = unlocked.has(def.id) ? '<span style="color:#0f0;">[✓]</span>' : '[ ]';
                html += `${mark} ${def.name} <span style="color:#888;font-size:12px;">— ${def.desc}</span><br>`;
            });
            html += '<br>';
        });
        html += '<button onclick="closeModal()" class="sbtn">关闭</button>';
        showModal(html, true);
    }

    function showTianjigeModal() {
        const meta = ensureTianjigeContracts();
        const tj = meta.tianjige;
        const contracts = window.GAME_CONTRACTS || { dailyReward: 10, weeklyReward: 50, shop: [] };
        const omens = (window.GAME_THEME && window.GAME_THEME.tianjigeOmens) || ['微雨'];
        const omen = omens[Math.floor(Math.randomNeutral() * omens.length)];

        let html = '<span style="color:#ff0;">【 天机阁 · 修行契 】</span><br><br>';
        html += `<span style="color:#888;">今日天象：${omen} · 天机点 ${tj.points || 0}</span><br><br>`;

        html += '<span style="color:#afa;">日契</span><br>';
        (tj.daily || []).forEach(c => {
            const prog = `${Math.min(c.progress || 0, c.target)}/${c.target}`;
            const status = c.done ? (tj.dailyClaimed.includes(c.id) ? '已领取' : `<button onclick="MetaSystems.claimContract('${c.id}',false)" class="sbtn-sm">领取(+${contracts.dailyReward})</button>`) : '进行中';
            html += `· ${c.title} (${prog}) ${status}<br>`;
        });

        html += '<br><span style="color:#afa;">周契</span><br>';
        if (tj.weekly) {
            const w = tj.weekly;
            const prog = `${Math.min(w.progress || 0, w.target)}/${w.target}`;
            const status = w.done ? (tj.weeklyClaimed ? '已领取' : `<button onclick="MetaSystems.claimContract('${w.id}',true)" class="sbtn-sm">领取(+${contracts.weeklyReward})</button>`) : '进行中';
            html += `· ${w.title} (${prog}) ${status}<br>`;
        } else {
            html += '<span style="color:#888;">暂无</span><br>';
        }

        html += '<br><span style="color:#afa;">天机宝阁</span><br>';
        (contracts.shop || []).forEach(s => {
            html += `<button onclick="MetaSystems.buyShop('${s.id}')" class="sbtn-sm" style="margin:2px;">${s.name} (${s.cost}点)</button> `;
        });

        html += '<br><br><button onclick="closeModal()" class="sbtn">关闭</button>';
        showModal(html, true);
    }

    function applyThemeLabels() {
        const t = window.GAME_THEME;
        if (!t) return;
        if (t.productName) document.title = t.productName;
        if (t.tabs) {
            document.querySelectorAll('.tab-bar .tab').forEach(tab => {
                const key = tab.dataset.tab;
                if (key && t.tabs[key]) tab.textContent = t.tabs[key];
            });
        }
    }

    function maybeRandomEncounter() {
        const pool = window.GAME_THEME && window.GAME_THEME.chroniclePool;
        if (!pool || !pool.length) return;
        if (Math.randomNeutral() > 0.02) return;
        const text = pool[Math.floor(Math.randomNeutral() * pool.length)];
        appendChronicle('【奇遇】' + text, 'encounter');
        if (typeof player !== 'undefined' && Math.randomNeutral() < 0.5) {
            player.spiritStone = $addBig(player.spiritStone || '0', '100');
            saveGameData();
        }
    }

    const MetaSystems = {
        _chronicleCollapsed: false,
        init() {
            applyThemeLabels();
            ensureTianjigeContracts();
            if (typeof player !== 'undefined') {
                const meta = getMeta();
                player.metaChronicle = meta.chronicle;
                player.metaAchievements = meta.achievements;
                player.metaProgress = meta.progress;
                player.metaTianjige = meta.tianjige;
            }
            updateChronicleUI();
            checkAchievements();
        },
        toggleChronicle() {
            MetaSystems._chronicleCollapsed = !MetaSystems._chronicleCollapsed;
            updateChronicleUI();
        },
        appendChronicle,
        addTitle,
        trackProgress,
        checkAchievements,
        updateChronicleUI,
        showDaoguoModal,
        showTianjigeModal,
        claimContract,
        buyShop: buyShopItem,
        onBreakthroughSuccess(realmIndex) {
            const realms = (window.GAME_THEME && window.GAME_THEME.realms) || ['练气', '筑基', '金丹', '元婴', '化神'];
            const name = realms[realmIndex] || '未知';
            appendChronicle(`雷劫过，入${name}境。`, 'breakthrough');
            addTitle(name + '修士');
            checkAchievements();
        },
        onBreakthroughAttempt() {
            trackProgress('breakthroughTry', 1);
        },
        onMapUnlock(mapNum) {
            if (mapNum <= 99) {
                const mapName = (typeof mapNames !== 'undefined' && mapNames[mapNum - 1]) ? mapNames[mapNum - 1] : `第${mapNum}重天`;
                appendChronicle(`踏入${mapName}，前路渐开。`, 'map');
            } else if (mapNum === 101) {
                appendChronicle('无尽虚空之门已开，深境待探。', 'map');
            }
            checkAchievements();
            maybeRandomEncounter();
        },
        onBattleWin(kills) {
            trackProgress('battleKills', kills || 1);
            trackProgress('battleWins', 1);
            maybeRandomEncounter();
        },
        onAlchemySuccess() {
            trackProgress('alchemySuccess', 1);
            appendChronicle('丹香烟起，一炉功成。', 'alchemy');
            checkAchievements();
        },
        onFanXuClear(floor) {
            appendChronicle(`返虚路第 ${floor} 层，气运凝实。`, 'fanxu');
            trackProgress('fanxuClear', 1);
            checkAchievements();
        },
        onOfflineReturn(seconds) {
            if (seconds < 3600) return;
            const meta = getMeta();
            if (seconds > (meta.progress.maxOfflineOnce || 0)) {
                meta.progress.maxOfflineOnce = seconds;
                saveMeta(meta);
            }
            const hours = Math.floor(seconds / 3600);
            appendChronicle(`闭关 ${hours} 时辰，再入尘世。`, 'offline');
            checkAchievements();
        },
        onCloneCreated() {
            appendChronicle('身外化身已立，平行世界线开启。', 'clone');
            checkAchievements();
        },
        onCloneAbsorbed() {
            trackProgress('cloneAbsorbCount', 1);
            appendChronicle('化身归元，本质更凝。', 'clone');
            checkAchievements();
        },
        onYunLingXianYuan() {
            appendChronicle('韵灵吸收道则，三维永增。', 'yunling');
        },
        exportChronicleText() {
            const meta = getMeta();
            let text = '=== 修仙纪事 ===\n\n';
            (meta.chronicle.entries || []).slice().reverse().forEach(e => {
                text += `[${formatChronicleTime(e.time)}] ${e.text}\n`;
            });
            text += '\n=== 称号 ===\n';
            text += (meta.chronicle.titles || []).join('、') || '无';
            return text;
        }
    };

    window.MetaSystems = MetaSystems;
})();
