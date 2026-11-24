# シーケンス図：結果閲覧・CSV出力

## 1. 対象

練習履歴・模試結果の一覧および詳細を画面表示する処理と、対象データ全件をCSVで返す処理を示します。

## 2. シーケンス図

```mermaid
sequenceDiagram
    actor Learner as 学習者
    participant Browser as ブラウザー
    participant Servlet as 結果Servlet / ResultsCsvServlet
    participant DAO as LearningHistoryDao または MockExamDao
    participant DB as MySQL
    participant JSP as 結果JSP

    alt 結果一覧または詳細の閲覧
        Learner->>Browser: 結果ページを開く
        Browser->>Servlet: GET PracticeResultsServlet または MockExamResultsServlet
        Servlet->>DAO: findRecent(100)
        DAO->>DB: 結果をID降順で検索
        DB-->>DAO: 結果一覧
        DAO-->>Servlet: 結果一覧Bean
        opt historyIdまたはmockIdが指定されている
            Servlet->>DAO: findById(id)
            DAO->>DB: 対象結果を検索
            DB-->>DAO: 詳細結果
            DAO-->>Servlet: 詳細Bean
        end
        Servlet->>JSP: forward（詳細・一覧）
        JSP-->>Browser: 結果画面
    else CSV出力
        Learner->>Browser: CSVダウンロードを選択
        Browser->>Servlet: GET ResultsCsvServlet?type=practice|mock
        Servlet->>DAO: findAll()
        DAO->>DB: 結果全件を検索
        DB-->>DAO: 全件結果
        DAO-->>Servlet: 結果Bean一覧
        Servlet->>Servlet: UTF-8 BOM付きCSVを生成
        Servlet-->>Browser: text/csvダウンロード
    end
```

## 3. 実装上の注意

- 画面表示の一覧は直近最大100件です。ID指定時は一覧に対象詳細も表示します。
- CSV出力は `findAll()` による全件出力で、期間指定・ページングはありません。
- CSVはUTF-8 BOM付きです。セル値は引用符で囲み、引用符をエスケープします。
- `practice` と `mock` 以外の `type` はHTTP 400です。
- CSV項目・文字コード・エスケープ仕様は `04_詳細設計書.md` を参照してください。

