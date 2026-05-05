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
    
    // TaskLogデータを保存する変数
    private List<TaskLog> tLList = new ArrayList<>();
    //Taskデータを保存するリスト
    private List<Task> tList = new ArrayList<>();

    // リスト保存とCSV保存
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

    // CSVファイルを読み込み、データをListに保存
    public void loadTaskLogCSV(){
        this.tLList = tLRepository.loadTaskLogs();
    }

    // タスクログ一覧取得
    public List<TaskLog> getTaskLogs(){
        return tLList;
    }
    
    // タスク別時間集計
    public Map<String,Integer> sumByTaskLogs(){        
        Map<String,Integer> result = new HashMap<>();

        for(TaskLog tL : tLList){
            int taskId = tL.getTaskId();
            String task = null;
            tList = tService.getTasks();
            // tLListとtListのtaskIdが一致する場合、taskにタスク名を代入
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

    // 期間指定ログ一覧
    public List<TaskLog> periodTaskLogs(String from,String to){
        // 開始日または終了日が未入力の場合、それぞれ最大値または最小値を代入
        if(from == ""){
            from = "0000-00-00";
        }
        if(to == ""){
            to = "9999-99-99";
        }
        List<TaskLog> periodTL = new ArrayList<>();
        // 日付がfrom以降かつto以前であるデータをperiodTLListに保存する
        for(TaskLog tL: tLList){
            if((tL.getDate().compareTo(from) >= 0) 
                & (tL.getDate().compareTo(to) <= 0)){
                periodTL.add(tL);                
            }
        }
        return periodTL;
    }

    // 期間指定集計
    public Map<String,Integer> sumByPeriodTaskLogs(List<TaskLog> tLList){
        Map<String,Integer> result = new HashMap<>();

        for(TaskLog tL : tLList){
            int taskId = tL.getTaskId();
            String task = null;
            // tLListとtListのtaskIdが一致する場合、taskにタスク名を代入
            for(Task t : tList){      
                System.out.println(t);  
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

    // 検索
    public List<TaskLog> searchByTaskName(String keyword){
        List<TaskLog> sumByPeriodTL = new ArrayList<>();
        tList = tService.getTasks();
        int taskId = 0;
        // keywordに該当するtaskIdを抽出する
        int count = 0;
        for(Task t : tList){
            if(t.getTask().equals(keyword)){
                taskId = t.getTaskId();
                break;
            }else{
                if(count == (tList.size() - 1)){
                    System.out.println("キーワードに該当するログは存在しません。");
                }
            }
            count++;
        }
        // taskIdが一致するレコードを抽出する
        for(TaskLog tL : tLList){
            if(tL.getTaskId() == taskId){
                sumByPeriodTL.add(tL);
            }
        }
        return sumByPeriodTL;
    }

}
