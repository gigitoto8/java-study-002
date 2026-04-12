public class Task {
    
    private int taskId;
    private String task;
    private String category;
    private static int counter = 0;

    public Task(String task,String category){
        counter++;
        this.taskId = counter;
        this.task = task;
        this.category = category;
        System.out.println("taskId : " + taskId + " , task : " + task + " , category : " + category);
    }
    public int getTaskId(){return taskId;}
    public String getTask(){return task;}
    public String getCategory(){return category;}
    public void setTask(String task){this.task = task;}
    public void setCategory(String category){this.category = category;}
    //※外部からID変更させないため、Id設定メソッド（setIdTask）は定義しない
}
