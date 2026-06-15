package app.service;

import app.model.Task;
import app.model.TaskLog;
import app.repository.TaskLogRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskLogService {
    
    private TaskLogRepository taskLogRepository;
    private TaskService taskService;
    //外からTaskServiceとTaskLogRepositoryを受け取る
    public TaskLogService(TaskService taskService,TaskLogRepository taskLogRepository){
        this.taskService = taskService;
        this.taskLogRepository = taskLogRepository;
    }
    
    // TaskLogデータを保存する変数
    private List<TaskLog> taskLogList = new ArrayList<>();
    
    // CSVファイルを読み込み、データをListに保存
    // 併せて、使用済みtaskLogIdを取得
    public void loadTaskLogCSV(){
        this.taskLogList = taskLogRepository.loadTaskLogs();
        int maxId = 0;
        for(TaskLog tL : taskLogList){
            if(tL.getTaskLogId() > maxId){
                maxId = tL.getTaskLogId();
            }
        }
        TaskLog.setTaskLogIdCount(maxId);
    }

    // 実際にTaskIdが使われているかチェックできた場合、保存を実行。
    // リスト保存とCSV保存を実行
    public void addTaskLog(TaskLog taskLog){
        if(taskService.existById(taskLog.getTaskId())){
            taskLogList.add(taskLog);
            taskLogRepository.saveTaskLog(taskLog);
        }else{
            System.out.println("taskId \"" + taskLog.getTaskId() + "\" は存在しません");
        }
    }

    // オーバーロード
    public void addTaskLog(int taskId,LocalDate date,int minutes,String memo,LocalDateTime createdAt,LocalDateTime updatedAt){
        if(taskService.existById(taskId)){
            TaskLog tl = new TaskLog(taskId,date,minutes,memo,createdAt,updatedAt);
            addTaskLog(tl); //オーバーロード
        }else{
            System.out.println("taskId \"" + taskId + "\" は存在しませ-ん");
        }
    }

    // タスクログ一覧取得（タスク側で論理削除済IDに係るデータを除外）
    public List<TaskLog> getTaskLogs(){
        List<TaskLog> activeTaskLogs = new ArrayList<>();
        int targetId = 0;
        for(TaskLog sTL : taskLogList){
            // TaskLogインスタンスに係るtaskIdを取得
            targetId = sTL.getTaskId();
            // taskIdに係るTaskインスタンスを取得
            // ※注意　findByIdはこのクラスではなく、TaskServiceのメソッド
            Task t = taskService.findById(targetId);
            // 論理削除済みのtaskIdの場合、findByIdの戻り値はnullとなる
            // Taskインスタンスが戻った（nullでない）場合、インスタンスをリストに含める。
            if(t != null){
                activeTaskLogs.add(sTL);
            }
        }
        return  activeTaskLogs;
    }

    // taskLogIdの存在を判定
    public boolean existById(int taskLogId){        
        for(TaskLog tL : taskLogList){
            if(tL.getTaskLogId() == taskLogId){
                return true;
            }
        }
        return false;
    }

    // taskLogIdを渡して、該当するTaskLogを渡す
    public TaskLog findById(int taskLogId){
        for (TaskLog tL : taskLogList) {
            if(tL.getTaskLogId() == taskLogId){
                return tL;
            }
        }
        return null;
    }
    
    // 5:タスク別時間集計
    public Map<String,Integer> sumByTaskLogs(){        
        // 戻り値用変数、ID（String型）と実施時間で構成
        Map<String,Integer> result = new HashMap<>();
        // Taskデータ参照用Map変数、IDとタスク名で構成
        Map<Integer,String> taskMap = new HashMap<>();
        List<Task> tempTList = taskService.getTasks();
        //taskMapにTaskデータを保存
        for(Task t : tempTList){
            taskMap.put(t.getTaskId(),t.getTaskName());
        }

        List<TaskLog> showTLList = getTaskLogs();
        // 
        for(TaskLog tL : showTLList){
            String tempTName = taskMap.get(tL.getTaskId());
            // ↓taskが見つからない場合は下記の処理。nullがmapに入ってしまうのは好ましくない
            if(tempTName == null){
                continue;
            }
            int tempMinutes = tL.getMinutes();
            // resultに既にIDが含まれるか否か
            if(result.containsKey(tempTName)){
                result.put(tempTName,result.get(tempTName) + tempMinutes);
            }else{
                result.put(tempTName,tempMinutes);
            }
        }
        return result;
    }

    // 6:期間指定ログ一覧 ※※※後で、Main側の処理を統一させる必要がある。※※※
    public List<TaskLog> periodTaskLogs(LocalDate from,LocalDate to){
        // 戻り値用変数
        List<TaskLog> periodTL = new ArrayList<>();
        // 開始日または終了日が未入力の場合、それぞれ最大値または最小値を代入
        if(from == null){
            from = LocalDate.of(0000,01,01);
        }
        if(to == null){
            to = LocalDate.of(9999,12,31);
        }
        List<TaskLog> showTLList = getTaskLogs();
        // 日付がfrom以降かつto以前であるデータをperiodTLListに保存する
        for(TaskLog tL: showTLList){
            if((tL.getDate().compareTo(from) >= 0) 
                && (tL.getDate().compareTo(to) <= 0)){
                periodTL.add(tL);                
            }
        }
        return periodTL;
    }

    // 7:期間指定集計
    public Map<String,Integer> sumByPeriodTaskLogs(List<TaskLog> taskLogList){
        // 戻り値として使用するMap変数、ID（String型）と実施時間で構成
        Map<String,Integer> result = new HashMap<>();
        // Taskデータ参照用Map変数、IDとタスク名で構成
        Map<Integer,String> taskMap = new HashMap<>();
        List<Task> tList = taskService.getTasks();
        //taskMapにTaskデータを保存
        for(Task t : tList){
            taskMap.put(t.getTaskId(),t.getTaskName());
        }

        for(TaskLog tL : taskLogList){
            // TaskデータのtaskIdと一致する場合、紐づいたtask名を取得する
            String task = taskMap.get(tL.getTaskId());
            // ↓taskが見つからない場合は下記の処理。nullがmapに入ってしまうのは好ましくない
            if(task == null){
                continue;
            }
            int minutes = tL.getMinutes();
            
            if(result.containsKey(task)){
                result.put(task,result.get(task) + minutes);
            }else{
                result.put(task,minutes);
            }
        }
        return result;
    }

    // 8:検索
    public List<TaskLog> searchByTaskName(String keyword){
        List<TaskLog> matchedTaskLogs = new ArrayList<>();
        List<Task> tList = taskService.getTasks();
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
        for(TaskLog tL : taskLogList){
            if(taskIds.contains(tL.getTaskId())){
                matchedTaskLogs.add(tL);
            }
        }
        return matchedTaskLogs;
    }

    // 9:ソート
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

    // 12:リスト内のIDに該当するdateを変更する。
    public TaskLog updateDate(int taskLogId,LocalDate date){
        TaskLog targetTaskLog = findById(taskLogId);
        // 指定したtaskLogIdが存在しない場合はnullを返す
        if(targetTaskLog == null){            
            return null;
        }
        targetTaskLog.setDate(date);
        targetTaskLog.setUpdatedAt(LocalDateTime.now());
        taskLogRepository.saveAll(taskLogList);
        return targetTaskLog;
    }  
    
    // 13:リスト内のIDに該当するminutesを変更する。
    public TaskLog updateMinutes(int taskLogId,int value){
        TaskLog targetTaskLog = findById(taskLogId);
        // 指定したtaskLogIdが存在しない場合はnullを返す
        if(targetTaskLog == null){            
            return null;
        }
        targetTaskLog.setMinutes(value);
        targetTaskLog.setUpdatedAt(LocalDateTime.now());
        taskLogRepository.saveAll(taskLogList);
        return targetTaskLog;
    }  
    
    // 14:リスト内のIDに該当するmemoを変更する。
    public TaskLog updateMemo(int taskLogId,String text){
        TaskLog targetTaskLog = findById(taskLogId);
        // 指定したtaskLogIdが存在しない場合はnullを返す
        if(targetTaskLog == null){            
            return null;
        }
        targetTaskLog.setMemo(text);
        targetTaskLog.setUpdatedAt(LocalDateTime.now());
        taskLogRepository.saveAll(taskLogList);
        return targetTaskLog;
    }  
    
    // 16:リスト内のIDに該当するインスタンスを削除する。戻り値はMainに表示させる内容として利用
    public TaskLog deleteTaskLog(int taskLogId){
        TaskLog targetTaskLog = findById(taskLogId);
        taskLogList.remove(findById(taskLogId));
        taskLogRepository.saveAll(taskLogList);
        return targetTaskLog;
    }
}
