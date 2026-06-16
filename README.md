# java-study-002

# Task Management App（CUI）

## 概要
本リポジトリは、JavaSilver取得後の学習およびポートフォリオ作成を目的としている。
学習をにより、オブジェクト指向（クラス設計・責務分離）の理解と、
段階的な開発プロセスを通じた設計力の向上を目指すものとする。

## 開発環境
* Visual Studio Code
* GitHub（リポジトリ : [gigitoto8/java-study-002]  
(https://github.com/gigitoto8/java-study-002.git)）  

## AIツール(ChatGPT)利用方針
* テーマ設定、課題提示、レビューに活用する。
* 実装は原則として自力で行い、設計や考え方の理解を重視する。
* 提示されるコードに依存せず、必要に応じて助言を求める。

---
## アプリ概要
本アプリは、Javaを用いて開発したCUIベースのタスク管理アプリである。  
データはタスクとログから成り、データに対する登録・表示・更新・削除の他、  
検索、集計、ソートなどの機能を実装している。
また、データ永続化のためCSVファイルを利用している。

### ■ 主な機能
* タスクの登録
* タスク一覧表示
* タスクの更新
* タスクの削除
* 検索およびフィルタ機能
* CSVファイルによるデータ永続化

---
### ■ クラス構成
* Main              ：画面表示
* Task / TaskLog    :データ
* Service           ：業務ロジック  
* Repository        ：CSVファイル入出力  
* InputValidator    :入力文字チェック  

---
### データ構造
#### Task　
  行動の種類を管理するマスタデータ
  * taskId（タスクID） int型
  * taskName（タスク名　例：Java学習、買い物、書類作成 など） String型
  * category（カテゴリ　例：学習・仕事・家事 など） String型
  ※以下、STEP6で追加
  * createdAt（作成日時） LocalDateTime型
  * updatedAt（更新日時） LocalDateTime型
  * deletedAt（削除日時） LocalDateTime型
#### TaskLog
  行動の記録データ
  * date（実行日） String型（STEP7でLocalDate型に変更）
  * taskId（タスクID、TaskのTaskIdと関連付ける） int型
  * minutes（実行時間、分単位） int型
  * memo（メモ、任意入力） String型
  ※以下、STEP6で追加
  * taskLogId（タスクログID） int型
  * createdAt（作成日時） LocalDateTime型
  * updatedAt（更新日時） LocalDateTime型
  
---
### ファイル構成
src/  
 └ app/  
     ├ Main.java  
     ├ data  
     │  ├ task_record.csv  
     │  └ tasklog_record.csv  
     ├ model  
     │  ├ Task.java  
     │  └ TaskLog.java  
     ├ service  
     │  ├ TaskService.java  
     │  └ TaskLogService.java  
     ├ repository  
     │  ├ TaskRepository.java  
     │  └ TaskLogRepository.java  
     └ InputValidator.java

---
## 開発ステップ概要

本アプリは以下のステップを経て動作確認と設計の見直しを行い、  
段階的に開発を行う。  
1.基本設計（Task / TaskLogの定義）  
2.基本機能の実装（登録・一覧・記録）  
3.責務分離（Serviceクラスの導入）
4.CSVによるデータ永続化  
5.検索・集計機能の実装  
6.更新・削除機能の追加  
7.設計改善（インターフェース導入、リファクタリング、メソッド名・変数名およびコメントの修正）  

### 備考
本リポジトリは、学習およびポートフォリオを目的として作成している。  
実装の過程においては、試行錯誤や設計の見直しを積極的に行う方針である。

---
## 開発ステップ
### STEP1：基本設計（Task / TaskLogの定義）
以下の通り、TaskおよびTaskLogのデータ構造を設計した。
  
* Task : taskId、taskName、category  
* TaskLog : date、taskId、minutes、memo  

#### 設計上のポイント
- TaskとTaskLogの責務を分離した。
- 両者をtaskIdで関連付ける構成とした。
- IDの整合性維持のため外部からtaskIdを変更不可とした。

---
### STEP2：基本機能の実装（登録・一覧・記録）
TaskおよびTaskLogの基本機能として、登録および一覧表示の実装を行う。

#### 実装内容
- Task登録
- Task一覧表示
- TaskLog登録
- TaskLog一覧表示

#### 設計上のポイント
* 処理の流れとデータ構造の理解を優先するため、ロジックはMainクラスに集約している。
* 本ステップではデータの永続化は行わず、メモリ上で管理する構成とした。

---
### ■ STEP3：責務分離（Serviceクラスの導入）

本ステップでは、処理の責務を分離するためにServiceクラスを導入し、
データ管理およびロジックをMainクラスから分離した。

---
#### 実装内容

* TaskServiceの作成（Taskの管理）
* TaskLogServiceの作成（TaskLogの管理）
* 各ServiceにListを保持し、データを一元管理
* MainクラスからServiceを呼び出す構成へ変更

---

#### 設計上のポイント

* TaskおよびTaskLogの管理責務を各Serviceに委譲する。
* データ（List）はServiceクラス内で保持し、Mainでは管理しない構造とする。
* TaskLog登録時にTask（taskId）の存在チェックを行い、データの整合性を担保する。
* TaskLogServiceにTaskServiceを渡すことで、同一データを参照する構成とする。

---
### ■ STEP4：CSVによるデータ永続化

本ステップでは、これまでメモリ（List）上で管理していたデータを、
CSVファイルとして保存・読み込みできるように実装した。
また、CSVファイル操作を担当するRepositoryを導入し、ServiceからRepositoryを呼び出す構成とした。

#### 実装内容

* TaskRepositoryの作成
* TaskLogRepositoryの作成
* Task / TaskLog のCSV保存機能
* CSVファイルからのデータ読み込み機能
* アプリ起動時のデータ復元処理

#### 設計上のポイント

* **依存性注入（DI）**を採用し、ServiceにRepositoryを外部から渡す構造とした
* データ追加時にCSVへ即時追記を行い、データ消失を防止
* CSV読み込み時、Taskの最大IDを取得し、次回採番と整合性を保つよう補正
* TaskLog登録時にTaskの存在チェックを行い、不正データの登録を防止
* CSVファイルが存在しない場合、ファイル生成に加えてヘッダを1行目に挿入

---

### Mainクラスの構造整理

本アプリでは、Mainクラスの責務を整理するため、処理を以下の単位で分割した。

#### 構成

* `init()`

  * RepositoryとServiceの生成
  * CSVからのデータ読み込み（初期化処理）

* `run()`

  * ユーザー入力を受け付けるメインループ
  * 機能選択メニューの表示および分岐処理

※以下、機能に応じたメソッドを順次追加
* `inputTask()` / `inputTaskLog()`

  * 入力処理の分離
  * 可読性および再利用性の向上



#### 設計上のポイント

* 初期化処理と業務処理を分離し、処理の見通しを向上
* 入力処理をメソッド化することで、Mainの肥大化を抑制
* Mainは制御（フロー管理）に専念し、ロジックはServiceへ委譲

---

## ■ STEP5：検索・集計・ソート機能の実装

本ステップでは、登録したタスクログデータをもとに、
検索・期間指定・集計・ソートといった分析機能を実装した。

---

### ■ 実装機能

#### ① タスク別時間集計

* 全ログを対象に、タスクごとの合計時間を算出する
* `Map<タスク名, 合計時間>` の形式で集計

---

#### ② 期間指定ログ表示

* 開始日・終了日を指定し、その期間内のログを抽出する
* 日付は `yyyy-MM-dd` 形式の文字列として比較処理を実装

---

#### ③ 期間指定タスク別時間集計

* 期間指定ログを対象に、タスク別の合計時間を算出
* 一度抽出したログリストを再利用し、処理の分離を意識

---

#### ④ タスク名検索

* タスク名をキーワードとしてログを検索
* 該当する `taskId` をもとに、関連ログを抽出
* 表示時にタスク名とログ情報を組み合わせて出力

---

#### ⑤ 集計結果のソート

* タスク別集計結果をソートして表示
* 昇順・降順を選択可能
* `List<Map.Entry<...>>` に変換し、Comparatorで並び替えを実装

---

### ■ 設計上のポイント

* データ取得（Service）と表示（Main）の責務を分離
* Serviceはデータの加工・取得に専念し、表示処理はMain側で実装
* 既存メソッドの再利用（期間抽出 → 集計）を意識

---

# STEP6 CRUD機能実装

TaskおよびTaskLogに対する更新・削除機能を実装し、CRUD機能を完成させた。  
また、更新・削除処理に伴い、各モデルにメンバ変数を追加した。

---

## 実装内容

### 1. TaskLogIdの導入

従来はtaskIdを利用していたが、ログにtaskIdが重複するため、個別のログを特定できるようTaskLogにtaskLogIdを導入した。

---

### 2. 日時カラムの追加

TaskおよびTaskLogに以下の日時情報を追加した。

* createdAt（作成日時）
* updatedAt（更新日時）
* deletedAt（削除日時）※Taskのみ

作成時・更新時・削除時に日時を自動設定する仕組みを実装した。

---

### 3. 更新機能の実装

#### Task

更新対象

* taskName
* category

#### TaskLog

更新対象

* date
* minutes
* memo

更新時は各IDから対象レコードを検索し、値変更後にupdatedAtを更新する。

また、CSVファイルへ全件上書き保存を行う。

---

### 4. 削除機能の実装

#### Task

論理削除を採用。  
レコード自体は削除せず、deletedAtへ削除日時を設定する。

#### TaskLog

物理削除を採用。  
対象データをリストから削除し、CSVへ再保存する。

---

### 5. CSV全件上書き(saveAll)

更新・削除機能実装に伴い、Repository層へsaveAllメソッドを追加した。  
  
処理手順  

1. メモリ上のListを更新
2. CSVファイルを上書きモードで開く
3. ヘッダ行を書き込む
4. List内の全データを書き込む

これにより、更新処理および削除処理後の状態をCSVへ反映できるようになった。

---

### 6. 論理削除対応

Task一覧取得時にdeletedAtが設定されているデータを除外するよう修正。
また、TaskLog一覧取得時には、論理削除済Taskに紐づくログを表示対象外とした。
これにより、論理削除済データを通常利用時に参照できないようにした。

---



