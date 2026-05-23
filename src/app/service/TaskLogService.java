package app.service;

import app.model.Task;
import app.model.TaskLog;
import app.repository.TaskLogRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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

    // リスト保存とCSV保存
    // 実際にTaskIdが使われているかチェックできた場合、保存を実行。
    public void addTaskLog(TaskLog taskLog){
        if(tService.existById(taskLog.getTaskId())){
            tLList.add(taskLog);
            tLRepository.saveTaskLog(taskLog);
        }else{
            System.out.println("taskId \"" + taskLog.getTaskId() + "\" は存在しません");
        }
    }

    public void addTaskLog(int taskId,String date,int minutes,String memo,LocalDateTime createdAt,LocalDateTime updatedAt){
            TaskLog tl = new TaskLog(taskId,date,minutes,memo,createdAt,updatedAt);
            addTaskLog(tl); //オーバーロード
    }

    // CSVファイルを読み込み、データをListに保存
    // 併せて、使用済みtaskLogIdを取得
    public void loadTaskLogCSV(){
        this.tLList = tLRepository.loadTaskLogs();
        int MaxId = 0;
        for(TaskLog tL : tLList){
            if(tL.getTaskLogId() > MaxId){
                MaxId = tL.getTaskLogId();
            }
        }
        TaskLog.setCountTaskId(MaxId);
    }

    // タスクログ一覧取得
    public List<TaskLog> getTaskLogs(){
        return  new ArrayList<>(tLList);
    }

    // taskLogIdが存在するかチェック
    public boolean existById(int taskLogId){        
        for(TaskLog tL : tLList){
            if(tL.getTaskLogId() == taskLogId){
                return true;
            }
        }
        return false;
    }

    // taskLogIdを渡して、該当するTaskLogオブジェクトを渡す
    public TaskLog findById(int taskLogId){
        for (TaskLog tL : tLList) {
            if(tL.getTaskLogId() == taskLogId){
                return tL;
            }
        }
        return null;
    }
    
    // タスク別時間集計
    public Map<String,Integer> sumByTaskLogs(){        
        // 戻り値として使用するMap変数
        Map<String,Integer> result = new HashMap<>();
        // Taskデータ参照用Map変数
        Map<Integer,String> taskMap = new HashMap<>();
        List<Task> tList = tService.getTasks();
        //taskMapにTaskデータを保存
        for(Task t : tList){
            taskMap.put(t.getTaskId(),t.getTaskName());
        }

        // 
        for(TaskLog tL : tLList){
            // TaskデータのtaskIdと一致する場合、紐づいたtask名を取得する
            String taskNumById = taskMap.get(tL.getTaskId());
            // ↓taskが見つからない場合は下記の処理。nullがmapに入ってしまうのは好ましくない
            if(taskNumById == null){
                continue;
            }

            int minutes = tL.getMInutes();
            
            if(result.containsKey(taskNumById)){
                result.put(taskNumById,result.get(taskNumById) + minutes);
            }else{
                result.put(taskNumById,minutes);
            }
        }
        return result;
    }

    // 期間指定ログ一覧
    public List<TaskLog> periodTaskLogs(String from,String to){
        // 開始日または終了日が未入力の場合、それぞれ最大値または最小値を代入
        if(from.isEmpty()){
            from = "0000-00-00";
        }
        if(to.isEmpty()){
            to = "9999-99-99";
        }
        List<TaskLog> periodTL = new ArrayList<>();
        // 日付がfrom以降かつto以前であるデータをperiodTLListに保存する
        for(TaskLog tL: tLList){
            if((tL.getDate().compareTo(from) >= 0) 
                && (tL.getDate().compareTo(to) <= 0)){
                periodTL.add(tL);                
            }
        }
        return periodTL;
    }

    // 期間指定集計
    public Map<String,Integer> sumByPeriodTaskLogs(List<TaskLog> tLList){
        // 戻り値として使用するMap変数
        Map<String,Integer> result = new HashMap<>();
        // Taskデータ参照用Map変数
        Map<Integer,String> taskMap = new HashMap<>();
        List<Task> tList = tService.getTasks();
        //taskMapにTaskデータを保存
        for(Task t : tList){
            taskMap.put(t.getTaskId(),t.getTaskName());
        }

        for(TaskLog tL : tLList){
            // TaskデータのtaskIdと一致する場合、紐づいたtask名を取得する
            String task = taskMap.get(tL.getTaskId());
            // ↓taskが見つからない場合は下記の処理。nullがmapに入ってしまうのは好ましくない
            if(task == null){
                continue;
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
        List<Task> tList = tService.getTasks();
        // 検索ワードが含まれるタスクのIDを保存するリスト
        List<Integer> taskIds = new ArrayList<>();
        // keywordに該当するtaskIdを抽出する
        int count = 0;
        for(Task t : tList){
            if(t.getTaskName().contains(keyword)){
                taskIds.add(t.getTaskId());
            }else{
                if(count == (tList.size() - 1)){
                    System.out.println("キーワードに該当するログは存在しません。");
                }
            }
            count++;
        }
        // taskIdが一致するレコードを抽出する
        for(TaskLog tL : tLList){
            if(taskIds.contains(tL.getTaskId())){
                sumByPeriodTL.add(tL);
            }
        }
        return sumByPeriodTL;
    }

    // ソート
    public List<Map.Entry<String,Integer>> sortBySumByTime(boolean desc){
        // 集計データ（全期間）をMAP形式で取得
        Map<String,Integer> result = sumByTaskLogs();
        // MAP形式からLIST形式に変換
        List<Map.Entry<String, Integer>> mapToList = new ArrayList<>(result.entrySet());

        mapToList.sort(new Comparator<Map.Entry<String,Integer>>() {
            @Override
            public int compare(Map.Entry<String,Integer> a,
                                Map.Entry<String,Integer> b) {
                if(desc){
                    return b.getValue() - a.getValue(); // 降順
                }else{
                    return a.getValue() - b.getValue(); // 昇順
                }
            }
        });
        return mapToList;
    }

    // リスト内のIDに該当するdateを変更する。
    public void resetDate(int taskLogId,String text){
        TaskLog targetTaskLog = findById(taskLogId);
        targetTaskLog.setDate(text);
        targetTaskLog.setUpdatedAt(LocalDateTime.now());
        tLRepository.saveAll(tLList);
    }  
    
    // リスト内のIDに該当するdateを変更する。
    public void resetMinutes(int taskLogId,int value){
        TaskLog targetTaskLog = findById(taskLogId);
        targetTaskLog.setMinutes(value);
        targetTaskLog.setUpdatedAt(LocalDateTime.now());
        tLRepository.saveAll(tLList);
    }  
    
    // リスト内のIDに該当するdateを変更する。
    public void resetMemo(int taskLogId,String text){
        TaskLog targetTaskLog = findById(taskLogId);
        targetTaskLog.setMemo(text);
        targetTaskLog.setUpdatedAt(LocalDateTime.now());
        tLRepository.saveAll(tLList);
    }  
    
}
