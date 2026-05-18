package app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    
    private int taskId;
    private String taskName;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;
    private static int counter = 0;

    // formatterを共通化
    private static final DateTimeFormatter DATETIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    // コンストラクタ、コンソール入力データ登録時
    public Task(String taskName,String category,LocalDateTime createdAt,LocalDateTime updatedAt,LocalDateTime deletedAt){
        // インスタンス作成時、taskIdが自動生成される
        counter++;
        this.taskId = counter;
        this.taskName = taskName;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // コンストラクタ、CSVデータ読込時
    public Task(int taskId,String taskName,String category,LocalDateTime createdAt,LocalDateTime updatedAt,LocalDateTime deletedAt){
        this.taskId = taskId;
        this.taskName = taskName;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public int getTaskId(){return taskId;}
    public String getTaskName(){return taskName;}
    public String getCategory(){return category;}
    public LocalDateTime getCreatedAt(){return createdAt;}
    public LocalDateTime getUpdatedAt(){return updatedAt;}
    public LocalDateTime getDeletedAt(){return deletedAt;}
    public void setTaskName(String taskName){this.taskName = taskName;}
    public void setCategory(String category){this.category = category;}
    public void setCreatedAt(LocalDateTime createdAt){this.createdAt = createdAt;}
    public void setUpdatedAt(LocalDateTime updatedAt){this.updatedAt = updatedAt;}
    public void setDeletedAt(LocalDateTime deletedAt){this.deletedAt = deletedAt;}
    // ※外部からID変更させないため、Id設定メソッド（setTaskId）は定義しない
    // CSV読込時に読み込んだtaskIdをcounterに設定する
    public static void setCountTaskId(int c){counter = c;}

    // 形式を定めてCSVに保存する
    public String toCsv(){
        return taskId + "," + taskName + "," + category + "," 
                + createdAt.format(DATETIME_FORMATTER) + "," 
                + updatedAt.format(DATETIME_FORMATTER) + "," 
                // 新規登録時はnullを、論理削除済で日付が入っている場合はString形式に変換したものを返す
                + (deletedAt == null ? "null" : deletedAt.format(DATETIME_FORMATTER));
    }

    // 表示フォーマット
    @Override
    public String toString(){
        // フォーマット
        return String.format(
            "taskId : %d , taskName : %s , category : %s , createdAt : %s , updatedAt : %s , deletedAt : %s",
            taskId, taskName, category, 
            createdAt.format(DATETIME_FORMATTER),
            updatedAt.format(DATETIME_FORMATTER),
            // deleteAtがnullである場合はnullを、そうでない場合は日付データを代入する
            deletedAt == null ? "null" : deletedAt.format(DATETIME_FORMATTER)
        );
    }
}
