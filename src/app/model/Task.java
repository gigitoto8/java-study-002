package app.model;

import java.time.LocalDateTime;

public class Task {
    
    private int taskId;
    private String task;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private LocalDateTime updatedAt;
    private static int counter = 0;

    // コンストラクタ、コンソール入力データ保存時
    public Task(String task,String category){
        // インスタンス作成時、taskIdが自動生成される
        counter++;
        this.taskId = counter;
        this.task = task;
        this.category = category;
    }

    // コンストラクタ、CSVデータ読込時
    public Task(int taskId,String task,String category){
        this.taskId = taskId;
        this.task = task;
        this.category = category;
    }

    public int getTaskId(){return taskId;}
    public String getTask(){return task;}
    public String getCategory(){return category;}
    public void setTask(String task){this.task = task;}
    public void setCategory(String category){this.category = category;}
    // ※外部からID変更させないため、Id設定メソッド（setTaskId）は定義しない
    // CSV読込時に読み込んだtaskIdをcounterに設定する
    public static void setCountTaskId(int c){counter = c;}

    // 形式を定めてCSVに保存する
    public String toCsv(){
        return taskId + "," + task + "," + category;
    }

    @Override
    public String toString(){
        // フォーマット
        return String.format(
            "taskId : %d , task : %s , category : %s",
            taskId, task, category
        );
    }
}
