package app.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskLog {
    
    private int taskLogId;
    private int taskId;
    private LocalDate date;
    private int minutes;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private static int counter = 0;

    // formatterを共通化
    private static final DateTimeFormatter DATETIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

   // コンストラクタ、コンソール入力データ保存時
    public TaskLog(int taskId,LocalDate date,int minutes,String memo,LocalDateTime createdAt,LocalDateTime updatedAt){
        //インスタンス作成時、taskLogIdが自動生成される
        counter++;
        this.taskLogId = counter;
        this.taskId = taskId;
        this.date = date;
        this.minutes = minutes;
        this.memo = memo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // コンストラクタ、CSVデータ読込時
    public TaskLog(int taskLogId,int taskId,LocalDate date,int minutes,String memo,LocalDateTime createdAt,LocalDateTime updatedAt){
        this.taskLogId = taskLogId;
        this.taskId = taskId;
        this.date = date;
        this.minutes = minutes;
        this.memo = memo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getTaskLogId() {return this.taskLogId;}
    public int getTaskId() {return this.taskId;}
    public LocalDate getDate(){return this.date;}
    public int getMinutes() {return this.minutes;}
    public String getMemo(){return this.memo;}
    public LocalDateTime getCreatedAt(){return this.createdAt;}
    public LocalDateTime getUpdatedAt(){return this.updatedAt;}

    public void setTaskId(int taskId){this.taskId = taskId;}
    public void setDate(LocalDate date){this.date = date;}
    public void setMinutes(int minutes){this.minutes = minutes;}
    public void setMemo(String memo){this.memo = memo;}
    public void setCreatedAt(LocalDateTime createdAt){this.createdAt = createdAt;}
    public void setUpdatedAt(LocalDateTime updatedAt){this.updatedAt = updatedAt;}
    // ※外部からID変更させないため、Id設定メソッド（setTaskLogId）は定義しない
    // CSV読込時に読み込んだtaskLogIdをcounterに設定する
    public static void setCountTaskId(int c){counter = c;}

    // 形式を定めてCSVに保存する
    public String toCsv(){
        return taskLogId + "," + taskId + "," + date + "," + minutes + "," + memo + ","
                 + createdAt.format(DATETIME_FORMATTER) + "," + updatedAt.format(DATETIME_FORMATTER);
    }


    @Override
    public String toString(){
        // フォーマット
        return String.format(
            "taskLogId : %d , taskId : %d , date : %s , minutes : %d , memo : %s , createdAt : %s , updatedAt : %s",
            taskLogId, taskId , date , minutes , memo,
            createdAt.format(DATETIME_FORMATTER),
            updatedAt.format(DATETIME_FORMATTER)
        );
    }
}
