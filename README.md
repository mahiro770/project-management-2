# 案件配信ツール

案件情報を管理する簡易Webアプリ。案件の一覧・検索・絞り込み・登録・削除・応募管理・お気に入り登録ができる。

## 構成

```
.
├── frontend/               # 画面(素のHTML/CSS/JS、ビルド不要)
│   ├── index.html          # 案件一覧
│   ├── detail.html         # 案件詳細
│   ├── add.html             # 案件追加
│   ├── css/style.css
│   └── js/
│       ├── main.js
│       ├── detail.js
│       ├── add.js
│       └── api/projectApi.js
└── project-management/     # バックエンドAPI(Java + PostgreSQL)
    ├── pom.xml
    └── src/main/java/com/github/mahiro/projectmanager/
```

## 技術スタック

- バックエンド: Java 17, `com.sun.net.httpserver.HttpServer`(フレームワーク不使用), PostgreSQL(JDBC), org.json
- フロントエンド: 素のHTML / CSS / JavaScript(ESモジュール、ビルドツール不使用)

## セットアップ

### 1. PostgreSQL

- `localhost:5432` の `postgres` データベースに `projects` テーブルが存在すること
- 接続情報(ユーザー/パスワードとも `postgres`)は [`Database.java`](project-management/src/main/java/com/github/mahiro/projectmanager/Database.java) に直書きされている

### 2. バックエンドAPIサーバー

```bash
cd project-management
mvn exec:java -Dexec.mainClass=com.github.mahiro.projectmanager.ApiMain
```
起動すると `http://localhost:8080/api/projects` で待ち受ける。

### 3. フロントエンド

静的ファイルなので、ESモジュールを使う都合上 `file://` ではなく簡易HTTPサーバー経由で開く。

```bash
cd frontend
npx http-server -p 5500 -c-1
```
ブラウザで `http://localhost:5500/index.html` を開く。

## API

| メソッド | パス | 内容 |
|---|---|---|
| GET | `/api/projects` | 一覧取得 |
| GET | `/api/projects/{id}` | 詳細取得 |
| POST | `/api/projects` | 新規登録 |
| PUT | `/api/projects/{id}` | 更新 |
| PUT | `/api/projects/{id}/applied` | 応募済みフラグ更新 |
| PUT | `/api/projects/{id}/category` | カテゴリ更新 |
| DELETE | `/api/projects/{id}` | 削除 |

## 主な機能

- 案件一覧のキーワード検索・地域タブ・カテゴリ(複数選択可)による絞り込み
- お気に入り登録(ブラウザのlocalStorageに保存)
- 応募済み管理
- 案件の新規登録・削除(削除は確認ダイアログあり)
