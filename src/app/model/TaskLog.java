package app.model;

import java.time.LocalDateTime;

public class TaskLog {
    
    private int taskLogId;
    private int taskId;
    private String date;
    private int minutes;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private static int counter = 0;

   // コンストラクタ、コンソール入力データ保存時
    public TaskLog(int taskId,String date,int minutes,String memo){
        //インスタンス作成時、taskLogIdが自動生成される
        counter++;
        this.taskLogId = counter;
        this.taskId = taskId;
        this.date = date;
        this.minutes = minutes;
        this.memo = memo;
    }

    // コンストラクタ、CSVデータ読込時
    public TaskLog(int taskLogId,int taskId,String date,int minutes,String memo){
        this.taskLogId = taskLogId;
        this.taskId = taskId;
        this.date = date;
        this.minutes = minutes;
        this.memo = memo;
    }

    public int getTaskLogId() {return this.taskLogId;}
    public int getTaskId() {return this.taskId;}
    public String getDate(){return this.date;}
    public int getMInutes() {return this.minutes;}
    public String getMemo(){return this.memo;}

    public void setTaskId(int taskId){this.taskId = taskId;}
    public void setDate(String date){this.date = date;}
    public void setMinutes(int minutes){this.minutes = minutes;}
    public void setMemo(String memo){this.memo = memo;}
    // ※外部からID変更させないため、Id設定メソッド（setTaskLogId）は定義しない
    // CSV読込時に読み込んだtaskLogIdをcounterに設定する
    public static void setCountTaskId(int c){counter = c;}

    // 形式を定めてCSVに保存する
    public String toCsv(){
        return taskLogId + "," + taskId + "," + date + "," + minutes + "," + memo;
    }


    @Override
    public String toString(){
        // フォーマット
        return String.format(
            "taskLogId : %d , taskId : %d , date : %s , minutes : %d , memo : %s",
            taskLogId, taskId , date , minutes , memo
        );
    }
}
