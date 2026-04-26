package app.service;

import app.model.Task;
import app.repository.TaskRepository;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    
    private TaskRepository tRepository;
    //外からRepositoryを受け取る
    public TaskService(TaskRepository tRepository){
        this.tRepository = tRepository;
    }

    //Taskデータを保存するリスト
    private List<Task> tList = new ArrayList<>();

    //リスト保存とCSV保存
    public void addTask(Task task){
        tList.add(task);
        tRepository.saveTask(task); // ← ここで保存
    }

    public void addTask(String task,String category){
        //インスタンス作成時、taskIdが自動生成される
        Task t = new Task(task, category);
        addTask(t); // オーバーロード
    }

    //CSVファイルを読み込み、データをListに保存
    public void loadTaskCSV(){
        tList = tRepository.loadTasks();
        int maxId = 0;
        //使用済みtaskIdを取得
        for(Task t : tList){
            if(t.getTaskId() > maxId){
                maxId = t.getTaskId();
            }
        }
        Task.setCount(maxId);
    }

    //一覧表示
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
