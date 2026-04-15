package app.service;

import app.model.Task;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    
    private List<Task> tList = new ArrayList<>();

    public void addTask(Task task){
        tList.add(task);
    }

    public void addTask(String task,String category){
        Task task001 = new Task(task, category);
        tList.add(task001);
    }

    public List<Task> getTasks(){
        return tList;
    }    

    //taskIdが存在するかチェック
    public boolean existById(int taskId){        
        for(Task t : tList){
            if(t.getTaskId() == taskId){
                return true;
            }
        }
        return false;
    }
}
