# Java + SQL 基礎課題（案件管理CRUD）

## 目的

JavaからDBへ接続し、案件情報の基本的なCRUD処理を実装できるようにする。

来月以降はフロント画面との連携を想定しているため、今回はコンソールアプリとして作成しつつ、後からAPI化しやすい構成を意識する。

---

## 今回作るもの

案件配信アプリのバックエンド練習として、案件情報を管理する簡易アプリを作成する。

まだ、制作段階

---

## 構成イメージ

```mermaid
flowchart TB

  subgraph 今回の2週間で作る範囲
    Main["Main<br/>コンソール画面<br/>入力・表示"]
    Service["ProjectService<br/>入力チェック<br/>業務ルール"]
    Repository["ProjectRepository<br/>SQL実行<br/>CRUD処理"]
    Database["Database<br/>DB接続管理<br/>JDBC"]
    DB["PostgreSQL<br/>projectsテーブル"]
    Model["Project<br/>案件データ"]
  end

  subgraph 来月以降の想定
    Front["フロント画面"]
    API["Controller / API"]
  end

  Main --> Service
  Service --> Repository
  Repository --> Database
  Database --> DB

  Service --> Model
  Repository --> Model
  Main --> Model

  Front -. 来月以降 .-> API
  API -. Mainの代わりに呼び出す .-> Service

  




