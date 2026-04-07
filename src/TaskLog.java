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

        System.out.println("taskId : " + taskId + " , date : " + date + " , minutes : " + minutes + " , memo : " + memo);
    }

    public int getTaskId() {return this.taskId;}
    public String getDate(){return this.date;}
    public int getMInutes() {return this.minutes;}
    public String getMemo(){return this.memo;}

    public void setTaskId(int taskId){this.taskId = taskId;}
    public void setDate(String date){this.date = date;}
    public void setMinutes(int minutes){this.minutes = minutes;}
    public void setMemo(String memo){this.memo = memo;}
}
