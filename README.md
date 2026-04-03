# EQF-Project (Easy Quest Framework)

EQF-Projectは、Minecraft (Paper/Spigot) サーバー向けの高度で拡張可能なクエストシステムフレームワークです。

## ⚠️ 重要：免責事項

**本プロジェクトのソースコードは、紛失したバイナリからデコンパイル（JADXを使用）されたものをベースに復元されています。**

*   デコンパイル特有の不自然なロジックや、型推論の失敗による不具合が残っている可能性が非常に高いです。
*   現在、手動での修正およびリファクタリングを進めていますが、本番環境での利用には十分な検証が必要です。
*   オリジナルのソースコードとは一部構造が異なる場合があります。

## 主な機能

### 1. トリガーシステム (Triggers)
様々なアクションをクエストの進行条件として設定可能です。
*   **Core:** エンティティの殺害、ブロックへのインタラクト、特定地点への移動。
*   **Citizens:** NPCへの右クリックによるイベント。
*   **MythicMobs:** 特定のMythicMobの殺害。
*   **WorldGuard:** 特定リージョンへの侵入。
*   **EDF:** ダンジョンリージョンへの侵入。

### 2. アクションシステム (Actions)
クエストの進行中に様々な効果を実行可能です。
*   **Dialogue:** タイピスト風の表示、SE再生、タイトル/アクションバー表示をサポート。
*   **Command:** プレイヤーまたはコンソール権限でのコマンド実行。
*   **Mob Spawn:** 通常のモブやMythicMobの召喚。
*   **Give Item:** アイテムの付与。
*   **Logic:** 並列実行 (Parallel)、待機 (Wait)、分岐 (Choose)、クエスト完了。

### 3. GUIシステム
*   クエストの進捗確認、管理用GUIを搭載。

### 4. 永続化
*   プレイヤーの進行状況は `playerdata/` フォルダ内に JSON 形式で保存されます。

## 開発環境

*   **Language:** Java 21
*   **Platform:** Paper API (1.21.x)
*   **Build Tool:** Maven
*   **Main Dependencies:**
    *   MythicMobs
    *   Citizens
    *   WorldGuard
    *   Deepwither (Internal Library)
    *   EDF-Project (Internal Library)

## ライセンス

本プロジェクトはRuskServer(RS Studio)のプロジェクトとして管理されています。
