package app.service;

import app.model.Task;
import app.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    
    private TaskRepository tRepository;
    // 外からRepositoryを受け取る
    public TaskService(TaskRepository tRepository){
        this.tRepository = tRepository;
    }

    // Taskデータを保存するリスト
    private List<Task> tList = new ArrayList<>();

    // リスト保存とCSV保存
    public void addTask(Task task){
        tList.add(task);
        tRepository.saveTask(task); // ← ここで保存
    }

    // コンソール入力データ保存時
    public void addTask(String task,String category,LocalDateTime createdAt,LocalDateTime updatedAt,LocalDateTime deletedAt){
        // インスタンス作成時、taskIdが自動生成される
        Task t = new Task(task, category,createdAt,updatedAt,deletedAt);
        addTask(t); // オーバーロード
    }

    // CSVファイルを読み込み、データをListに保存
    // 併せて、使用済みtaskIdを取得
    public void loadTaskCSV(){
        this.tList = tRepository.loadTasks();
        int maxId = 0;
        for(Task t : tList){
            if(t.getTaskId() > maxId){
                maxId = t.getTaskId();
            }
        }
        Task.setCountTaskId(maxId);
    }

    // タスク一覧取得
    public List<Task> getTasks(){
        return new ArrayList<>(tList);
    }

    // taskIdが存在するかチェック
    public boolean existById(int taskId){        
        for(Task t : tList){
            if(t.getTaskId() == taskId){
                return true;
            }
        }
        return false;
    }

    // TaskLog等のtaskIdからtaskNameを返す
    public String findById(int taskId){
        String taskName = null;
        for(Task t : tList){
            if(taskId == t.getTaskId()){
                taskName = t.getTask();
                break;
            }
        }
        return taskName;
    }
}
