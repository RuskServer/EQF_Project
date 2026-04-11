# クエスト制作ガイド (YAML)

Echoes Quest Framework (EQF) では、YAMLファイルを使用して柔軟なクエストを作成できます。クエストファイルは `plugins/EQF-Project/quests/` ディレクトリ内に配置してください。

## 1. クエストの基本構造

クエストは `id`, `title`, `description`, および複数の `stages` で構成されます。

```yaml
id: "sample_quest"
title: "はじめての冒険"
description: "村の長老に話しかけて、冒険の準備をしよう。"
stages:
  stage_1:
    # ステージ1の設定
  stage_2:
    # ステージ2の設定
```

---

## 2. ステージ (Stages) の設定

各ステージは、**Trigger（開始条件）** と **Actions（実行内容）** を持ちます。

| 項目 | 説明 |
| :--- | :--- |
| `trigger` | ステージを開始（または進行）させるためのトリガー。 |
| `actions` | トリガーが達成された直後に実行されるアクションのリスト。 |

### 設定例
```yaml
stages:
  start_stage:
    trigger:
      interact:
        material: "GOLD_BLOCK"
    actions:
      - message: "金ブロックを触ったな！クエスト開始だ。"
      - next: "stage_2"
```

---

## 3. 利用可能なトリガー (Triggers)

### 3.1 コアトリガー
- **`interact`**: 特定のブロックをクリックする。
  - `material`: ブロックの種類 (例: `DIAMOND_ORE`)
- **`kill`**: 特定のモブを倒す。
  - `entity_type`: エンティティの種類 (例: `ZOMBIE`)
  - `amount`: 必要討伐数
- **`location`**: 指定した座標の半径内に入る。
  - `world`: ワールド名 (デフォルト: `world`)
  - `x`, `y`, `z`: 座標
  - `radius`: 判定半径 (デフォルト: `3.0`)

### 3.2 外部プラグイン連携トリガー
- **`npc_interact` (Citizens)**: 特定のNPCに話しかける。
  - `id`: NPCのID
- **`area_enter` (WorldGuard)**: 特定のリージョンに入る。
  - `region`: リージョン名
- **`mythicmob_kill` (MythicMobs)**: 特定のMythicMobを倒す。
  - `mob_type`: MythicMobの内部名
  - `amount`: 必要数

---

## 4. 利用可能なアクション (Actions)

アクションはリスト形式 (`-`) で記述され、上から順に実行されます。

| アクション名         | パラメータ | 説明 |
|:---------------| :--- | :--- |
| `message`      | `value` (文字列) | プレイヤーにメッセージを送信する。 |
| `next`         | `value` (文字列) | 指定したIDのステージへ進行させる。 |
| `wait`         | `value` (秒数) | 次のアクションの実行を待機させる。 |
| `give_item`    | `material`, `amount` | アイテムを与える。 |
| `spawnmob`     | `type`, `amount`, `location` | 指定座標にモブをスポーンさせる。 |
| `command`      | `value` (文字列) | コンソールからコマンドを実行する。(`%player%` 使用可) |
| `complete`     | なし | クエストを完了状態にする。 |
| `dialogue`     | `npc_name`, `lines` | 会話形式のメッセージを表示する。 |
| `choose`       | `if`, `then`, `else` | 条件に基づいて実行するアクションを分岐させる。 |
| `choice`       | `title`, `options` | 選択肢をGUIで表示する（各選択肢に `actions` を設定可）。 |
| `parallel`     | `actions` | 複数のアクションを並列に実行する。 |
| `waypoint`     | `action`, `id`, `text`, `x`, `y`, `z`, `world` | ウェイポイント（ゴースト・ホログラム）を表示/非表示にする。 |
| `dialogue_npc` | `npc_id`, `lines`, `speed`, `lock_view`, `sound` | NPCとの対話演出（視線固定付き）。 |

---

### 4.2 対話演出アクション (dialogue_npc)
- **`dialogue_npc`**:
  - `npc_id`: CitizensのNPC ID。
  - `lines`: 表示するセリフのリスト。
  - `speed`: 文字送りの速度 (ticks、デフォルト: 2)。
  - `lock_view`: プレイヤーの視線をNPCに固定するか (デフォルト: true)。
  - `sound`: 文字送り時のSE (デフォルト: `BLOCK_NOTE_BLOCK_HARP`)。

#### 設定例
```yaml
actions:
  - dialogue_npc:
      npc_id: 10
      lines:
        - "やあ、よく来たな！"
        - "君に頼みたいことがあるんだ。"
      lock_view: true
      speed: 2
```

### 4.3 条件分岐と選択肢 (choose / choice)

#### 1. 条件分岐 (`choose`)
特定の条件（プレイヤーの持ち物、ステータス、進行度など）に基づいて、実行するアクションを分岐させます。

- `if`: 評価する条件式（詳細は後述）。
- `then`: 条件が **真 (true)** の場合に実行するアクション（リスト形式または単体のアクションID）。
- `else`: 条件が **偽 (false)** の場合に実行するアクション。

**設定例：**
```yaml
actions:
  - choose:
      if: "player.has_item:DIAMOND:1"
      then:
        - message: "ダイヤモンドを持っているな！合格だ。"
        - next: "success_stage"
      else:
        - message: "ダイヤモンドを持っていないようだ。出直してこい。"
```

#### 利用可能な条件式 (Conditions)
`choose` アクションの `if` パラメータで使用できる構文です。

| 構文 | 説明 | 例 |
| :--- | :--- | :--- |
| `player.has_item:ID:数量` | 指定したアイテムを所持しているか。 | `player.has_item:BREAD:3` |
| `player.stat:種別:値` | 指定したステータスが一定以上か。 | `player.stat:STRENGTH:10` |
| `&&` | かつ (AND) | `cond1 && cond2` |
| `||` | または (OR) | `cond1 || cond2` |

- **ステータス種別**: `STRENGTH`, `INTELLIGENCE`, `DEXTERITY` など (DeepWitherプラグインに依存)。
- **アイテムID**: Bukkitの [Material](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html) 名を指定してください。

#### 2. 選択肢 GUI (`choice`)
プレイヤーに複数の選択肢をGUIで提示し、選んだ内容に応じて異なるアクションを実行させます。

- `title`: GUIのタイトル (デフォルト: "選択してください")。
- `options`: 選択肢の設定（キーが選択肢名、値がアクションのリスト）。

**設定例：**
```yaml
actions:
  - choice:
      title: "どの道を進む？"
      options:
        "勇者の道":
          - message: "茨の道を選んだか…。幸運を。"
          - next: "hero_route"
        "賢者の道":
          - message: "知識こそが力だ。図書館へ向かえ。"
          - next: "sage_route"
        "逃げる":
          - message: "またいつでも戻ってくるといい。"
```

### 4.4 ウェイポイントアクションの詳細
- **`waypoint`**:
  - `action`: `show` または `hide` (デフォルト: `show`)
  - `id`: ウェイポイントを識別するユニークな名前 (例: `target_npc`)
  - `text`: 表示するテキスト (MiniMessage形式、例: `<yellow>目的地の村長</yellow>`)
  - `x`, `y`, `z`: 表示座標
  - `world`: ワールド名 (省略時は現在のワールド)

#### 設定例
```yaml
actions:
  - waypoint:
      action: "show"
      id: "objective"
      text: "<green>▼ 目的地</green>"
      x: 100.5
      y: 64.0
      z: 200.5
```

## 5. 実践的な例：簡単な「おつかい」クエスト

```yaml
id: "bread_delivery"
title: "パンの配達"
description: "村長からパンを受け取り、森の小屋に届けてください。"

stages:
  stage_1:
    # クエスト開始トリガー：村長(NPC ID: 5)に話しかける
    trigger:
      npc_interact:
        id: 5
    actions:
      - dialogue:
          npc_name: "村長"
          lines:
            - "おお、冒険者よ。このパンを森の小屋まで届けてくれんか？"
            - "最近、狼が出没するから気をつけるのじゃぞ。"
      - give_item:
          material: "BREAD"
          amount: 3
      - next: "stage_2"

  stage_2:
    # 特定のエリア(森の小屋)に入ると進行
    trigger:
      area_enter:
        region: "forest_cabin"
    actions:
      - message: "&a小屋に到着した！"
      - wait: 2
      - dialogue:
          npc_name: "小屋の主人"
          lines:
            - "ああ、村長からのパンだね。ありがとう！"
      - complete: {}
```

---

## 6. ヒントとテクニック

### メッセージの装飾
`&` 記号を使用したカラーコード（例: `&a` で緑色）が使用可能です。

### 並列実行 (`parallel`)
メッセージを出しながら同時にアイテムを渡したい場合などに便利です。
```yaml
actions:
  - parallel:
      - message: "アイテムを受け取った！"
      - give_item: { material: "IRON_INGOT", amount: 5 }
```
