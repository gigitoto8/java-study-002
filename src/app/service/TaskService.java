package app.service;

import app.model.Task;
import app.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    
    private TaskRepository taskRepository;
    // 外からRepositoryを受け取る
    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    // Taskデータ原本を保存するリスト
    private List<Task> taskList = new ArrayList<>();

    // リスト保存とCSV保存
    public void addTask(Task task){
        taskList.add(task);
        taskRepository.saveTask(task); // ← ここで保存
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
        this.taskList = taskRepository.loadTasks();
        int maxId = 0;
        for(Task t : taskList){
            if(t.getTaskId() > maxId){
                maxId = t.getTaskId();
            }
        }
        Task.setTaskIdCount(maxId);
    }

    // タスク一覧取得()
    public List<Task> getTasks(){
        List<Task> activeTasks = new ArrayList<>();
        for(Task sT : taskList){
            if(sT.getDeletedAt() == null){
                activeTasks.add(sT);
            }
        }
        return activeTasks;
    }

    // taskIdが存在するかチェック
    public boolean existById(int taskId){        
        List<Task> activeTasks = getTasks();
        for(Task t : activeTasks){
            if(t.getTaskId() == taskId){
                return true;
            }
        }
        return false;
    }

    // taskIdを渡して、該当するTaskを返す 
    public Task findById(int taskId){
        List<Task> tempTList = getTasks();
        for(Task t : tempTList){
            if(t.getTaskId() == taskId){
                return t;
            }
        }
        return null;
    }

    // リスト内のIDに該当するtaskNameを変更する。
    public void updateTaskName(int targetId,String text){
        Task targetTask = findById(targetId);
        targetTask.setTaskName(text);
        targetTask.setUpdatedAt(LocalDateTime.now());
        taskRepository.saveAll(taskList);
    }

    // リスト内のIDに該当するcategoryを変更する。
    public void updateCategory(int targetId,String text){
        Task targetTask = findById(targetId);
        targetTask.setCategory(text);
        targetTask.setUpdatedAt(LocalDateTime.now());
        taskRepository.saveAll(taskList);
    }

    // リスト内のIDに該当するインスタンスを削除する。戻り値はMainに表示させる内容として利用
    public Task deleteTask(int taskId){
        Task targetTask = findById(taskId);
        targetTask.setDeletedAt(LocalDateTime.now());
        taskRepository.saveAll(taskList);
        return targetTask;
    }
}
