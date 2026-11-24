flowchart LR
    Learner[学習者]

    System[TOEIC練習・模試システム]

    DB[(MySQLデータベース)]

    Learner -->|練習問題要求| System
    Learner -->|パート選択| System
    Learner -->|練習回答| System
    Learner -->|模試開始要求| System
    Learner -->|模試回答| System
    Learner -->|結果閲覧要求| System
    Learner -->|CSV出力要求| System

    System -->|練習問題・本文・選択肢| Learner
    System -->|採点結果・履歴| Learner
    System -->|模試画面・模試タイマー| Learner
    System -->|模試集計結果| Learner
    System -->|結果一覧・詳細| Learner
    System -->|CSVファイル| Learner

    System -->|設問・本文・履歴検索| DB
    System -->|模試結果登録| DB
    DB -->|設問・本文・履歴・模試結果| System