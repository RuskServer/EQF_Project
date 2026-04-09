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

| アクション名 | パラメータ | 説明 |
| :--- | :--- | :--- |
| `message` | `value` (文字列) | プレイヤーにメッセージを送信する。 |
| `next` | `value` (文字列) | 指定したIDのステージへ進行させる。 |
| `wait` | `value` (秒数) | 次のアクションの実行を待機させる。 |
| `give_item` | `material`, `amount` | アイテムを与える。 |
| `spawnmob` | `type`, `amount`, `location` | 指定座標にモブをスポーンさせる。 |
| `command` | `value` (文字列) | コンソールからコマンドを実行する。(`%player%` 使用可) |
| `complete` | なし | クエストを完了状態にする。 |
| `dialogue` | `npc_name`, `lines` | 会話形式のメッセージを表示する。 |
| `choose` | `title`, `options` | 選択肢をGUIで表示する（各選択肢に `actions` を設定可）。 |
| `parallel` | `actions` | 複数のアクションを並列に実行する。 |

---

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
