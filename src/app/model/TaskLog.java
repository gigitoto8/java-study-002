package app.model;

public class TaskLog {
    
    private int taskId;
    private String date;
    private int minutes;
    private String memo;

    public TaskLog(int taskId,String date,int minutes,String memo){
        this.taskId = taskId;
        this.date = date;
        this.minutes = minutes;
        this.memo = memo;
    }

    public int getTaskId() {return this.taskId;}
    public String getDate(){return this.date;}
    public int getMInutes() {return this.minutes;}
    public String getMemo(){return this.memo;}

    public void setTaskId(int taskId){this.taskId = taskId;}
    public void setDate(String date){this.date = date;}
    public void setMinutes(int minutes){this.minutes = minutes;}
    public void setMemo(String memo){this.memo = memo;}

    //形式を定めてCSVに保存する
    public String toCsv(){
        return taskId + "," + date + "," + minutes + "," + memo;
    }


    @Override
    public String toString(){
        //フォーマット
        return String.format(
            "taskId : %d , date : %s , minutes : %d , memo : %s",
            taskId , date , minutes , memo
        );
    }
}
