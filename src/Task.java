public class Task {
    
    private int taskId;
    private String task;
    private String category;

    public Task(String task,String category){
        taskId++;
        this.task = task;
        this.category = category;
        System.out.println("taskId : " + taskId + " , task : " + task + " , category : " + category);
    }
    public int getTaskId(){return taskId;}
    public String getTask(){return task;}
    public String getCategory(){return category;}
    public void setTaskId(int taskId){this.taskId = taskId;}
    public void setTask(String task){this.task = task;}
    public void setCategory(String category){this.category = category;}
}
