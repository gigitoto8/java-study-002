package app.service;

import app.model.Task;
import app.model.TaskLog;
import app.repository.TaskLogRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskLogService {

    private TaskLogRepository tLRepository;
    private TaskService tService;
    //外からTaskServiceとTaskLogRepositoryを受け取る
    public TaskLogService(TaskService tService,TaskLogRepository tLRepository){
        this.tService = tService;
        this.tLRepository = tLRepository;
    }

    //TaskLogデータを保存するリスト
    private List<TaskLog> tLList = new ArrayList<>();

    //リスト保存とCSV保存
    //実際にTaskIdが使われているかチェックできた場合、保存を実行。
    public void addTaskLog(TaskLog taskLog){
        if(tService.existById(taskLog.getTaskId())){
            tLList.add(taskLog);
            tLRepository.saveTaskLog(taskLog);
        }else{
            System.out.println("taskId \"" + taskLog.getTaskId() + "\" は存在しません");
        }
    }

    public void addTaskLog(int taskId,String date,int minutes,String memo){
            TaskLog tl = new TaskLog(taskId,date,minutes,memo);
            addTaskLog(tl); //オーバーロード
    }

    //CSVファイルを読み込み、データをListに保存
    public void loadTaskLogCSV(){
        this.tLList = tLRepository.loadTaskLogs();
    }

    //一覧表示
    public List<TaskLog> getTaskLogs(){
        return tLList;
    }

    //タスク別時間集計
    public Map<String,Integer> sumByTaskLogs(){        
        Map<String,Integer> result = new HashMap<>();

        for(TaskLog tL : tLList){
            int taskId = tL.getTaskId();
            String task = null;
            List<Task> tList = tService.getTasks();
            //tLListとtListのtaskIdが一致する場合、taskにタスク名を代入
            for(Task t : tList){        
                if(taskId  == t.getTaskId()){
                    task = t.getTask();
                    break;
                }
            }
            int minutes = tL.getMInutes();

            if(result.containsKey(task)){
                result.put(task,result.get(task) + minutes);
            }else{
                result.put(task,minutes);
            }
        }
        return result;
    }

}
