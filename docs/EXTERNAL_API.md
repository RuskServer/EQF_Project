# 外部API利用ガイド

Echoes Quest Framework (EQF) では、外部プラグインから独自の **Action**（アクション）や **Trigger**（トリガー）を登録して拡張することが可能です。

## 1. 依存関係の設定

独自の拡張を作成するには、`EQF-Project` をプロジェクトの依存関係に追加する必要があります。

### Maven (`pom.xml`)
```xml
<dependency>
    <groupId>com.lunar_prototype</groupId>
    <artifactId>eqf</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

## 2. カスタムアクションの作成

### ステップ1: EQFAction の実装
`EQFAction` インターフェースを実装して、アクション実行時のロジックを定義します。

```java
public class MyCustomAction implements EQFAction {
    private final String message;

    public MyCustomAction(Map<String, Object> params) {
        this.message = (String) params.getOrDefault("message", "Default Message");
    }

    @Override
    public ActionResult execute(ActionContext context) {
        context.getPlayer().sendMessage("Custom Action: " + message);
        return ActionResult.success();
    }
}
```

### ステップ2: Factory の実装
`EQFActionFactory` を実装して、設定ファイル（YAML）のパラメータからアクションのインスタンスを生成するロジックを定義します。

```java
public class MyCustomActionFactory implements EQFActionFactory {
    @Override
    public EQFAction create(Map<String, Object> params) {
        return new MyCustomAction(params);
    }
}
```

### ステップ3: 登録
プラグインの `onEnable` 等で `EQFPlugin` を通じて登録します。

```java
@Override
public void onEnable() {
    EQFPlugin.getInstance().registerAction("my_custom_action", new MyCustomActionFactory());
}
```

これで、クエストの YAML ファイル内で以下のように使用できるようになります。

```yaml
stages:
  stage1:
    actions:
      - my_custom_action:
          message: "Hello from external plugin!"
```

## 3. カスタムトリガーの作成

トリガーもアクションと同様の手順で登録可能です。

### ステップ1: EQFTrigger の実装
```java
public class MyCustomTrigger implements EQFTrigger<MyCustomEvent> {
    // 実装内容は既存の KillTrigger 等を参考にしてください
}
```

### ステップ2: 登録
```java
EQFPlugin.getInstance().registerTrigger("my_custom_trigger", new MyCustomTriggerFactory());
```

## 4. 注意事項
- **IDの重複**: 既に登録されている ID（例: `message`, `give_item` 等）で登録しようとすると `IllegalArgumentException` が発生します。
- **読み込み順序**: EQF が有効になった後に登録を行う必要があるため、`plugin.yml` に `depend: [EQF-Project]` または `softdepend: [EQF-Project]` を記述することを推奨します。
