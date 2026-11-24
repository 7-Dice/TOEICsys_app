flowchart LR
    Learner[学習者]

    P71[7.1 出力種別検証]
    P72[7.2 練習履歴取得]
    P73[7.3 模試結果取得]
    P74[7.4 CSV生成]
    P75[7.5 ダウンロード応答]

    D3[(D3 練習履歴)]
    D4[(D4 模試結果)]

    Learner -->|type=practice| P71
    Learner -->|type=mock| P71
    P71 -->|practice| P72
    P71 -->|mock| P73
    P71 -->|不正なtype| Learner

    P72 -->|全件取得| D3
    D3 -->|練習履歴全件| P72
    P72 -->|練習履歴| P74

    P73 -->|全件取得| D4
    D4 -->|模試結果全件| P73
    P73 -->|模試結果| P74

    P74 -->|UTF-8 BOM・引用符エスケープ| P75
    P75 -->|text/csvファイル| Learner