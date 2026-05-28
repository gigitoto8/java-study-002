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

    // データ追記
    // 実際にTaskIdが使われているかチェックできた場合、保存を実行。
    // リスト保存とCSV保存を実行
    public void addTaskLog(TaskLog taskLog){
        if(tService.existById(taskLog.getTaskId())){
            tLList.add(taskLog);
            tLRepository.saveTaskLog(taskLog);
        }else{
            System.out.println("taskId \"" + taskLog.getTaskId() + "\" は存在しません");
        }
    }

    // データ追記　オーバーロード
    public void addTaskLog(int taskId,String date,int minutes,String memo,LocalDateTime createdAt,LocalDateTime updatedAt){
            TaskLog tl = new TaskLog(taskId,date,minutes,memo,createdAt,updatedAt);
            addTaskLog(tl); //オーバーロード
    }

    // タスクログ一覧取得（タスク側で論理削除済IDに係るデータを除外）
    public List<TaskLog> getTaskLogs(){
        List<TaskLog> result = new ArrayList<>();
        int targetId = 0;
        for(TaskLog sTL : tLList){
            // TaskLogインスタンスに係るtaskIdを取得
            targetId = sTL.getTaskId();
            // taskIdに係るTaskインスタンスを取得
            // ※注意　findByIdはこのクラスではなく、TaskServiceのメソッド
            Task t = tService.findById(targetId);
            // 論理削除済みのtaskIdの場合、findByIdの戻り値はnullとなる
            // Taskインスタンスが戻った（nullでない）場合、インスタンスをリストに含める。
            if(t != null){
                result.add(sTL);
            }
        }
        return  result;
    }

    // taskLogIdが存在を判定
    public boolean existById(int taskLogId){        
        for(TaskLog tL : tLList){
            if(tL.getTaskLogId() == taskLogId){
                return true;
            }
        }
        return false;
    }

    // taskLogIdを渡して、該当するTaskLogデータを渡す
    public TaskLog findById(int taskLogId){
        for (TaskLog tL : tLList) {
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
        List<Task> tempTList = tService.getTasks();
        //taskMapにTaskデータを保存
        for(Task t : tempTList){
            taskMap.put(t.getTaskId(),t.getTaskName());
        }

        List<TaskLog> showTLList = getTaskLogs();
        // 
        for(TaskLog tL : showTLList){
            // TaskデータのtaskIdと紐づくtask名を取得する
            String tempTName = taskMap.get(tL.getTaskId());
            // ↓taskが見つからない場合は下記の処理。nullがmapに入ってしまうのは好ましくない
            if(tempTName == null){
                continue;
            }
            // 実施時間を仮保管
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
    public List<TaskLog> periodTaskLogs(String from,String to){
        // 戻り値用変数
        List<TaskLog> periodTL = new ArrayList<>();
        // 開始日または終了日が未入力の場合、それぞれ最大値または最小値を代入
        if(from.isEmpty()){
            from = "0000-00-00";
        }
        if(to.isEmpty()){
            to = "9999-99-99";
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
    public Map<String,Integer> sumByPeriodTaskLogs(List<TaskLog> tLList){
        // 戻り値として使用するMap変数、ID（String型）と実施時間で構成
        Map<String,Integer> result = new HashMap<>();
        // Taskデータ参照用Map変数、IDとタスク名で構成
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
    public TaskLog resetDate(int taskLogId,String text){
        TaskLog targetTaskLog = findById(taskLogId);
        // taskIdの方が論理削除済みの場合、インスタンスが存在しないのでnullになる。
        // その場合、nullを戻す
        if(targetTaskLog == null){            
            return null;
        }
        targetTaskLog.setDate(text);
        targetTaskLog.setUpdatedAt(LocalDateTime.now());
        tLRepository.saveAll(tLList);
        return targetTaskLog;
    }  
    
    // 13:リスト内のIDに該当するminutesを変更する。
    public TaskLog resetMinutes(int taskLogId,int value){
        TaskLog targetTaskLog = findById(taskLogId);
        // taskIdの方が論理削除済みの場合、インスタンスが存在しないのでnullになる。
        // その場合、nullを戻す
        if(targetTaskLog == null){            
            return null;
        }
        targetTaskLog.setMinutes(value);
        targetTaskLog.setUpdatedAt(LocalDateTime.now());
        tLRepository.saveAll(tLList);
        return targetTaskLog;
    }  
    
    // 14:リスト内のIDに該当するmemoを変更する。
    public TaskLog resetMemo(int taskLogId,String text){
        TaskLog targetTaskLog = findById(taskLogId);
        // taskIdの方が論理削除済みの場合、インスタンスが存在しないのでnullになる。
        // その場合、nullを戻す
        if(targetTaskLog == null){            
            return null;
        }
        targetTaskLog.setMemo(text);
        targetTaskLog.setUpdatedAt(LocalDateTime.now());
        tLRepository.saveAll(tLList);
        return targetTaskLog;
    }  
    
    // 16:リスト内のIDに該当するインスタンスを削除する。戻り値はMainに表示させる内容として利用
    public TaskLog deleteTaskLog(int taskLogId){
        TaskLog targetTaskLog = findById(taskLogId);
        tLList.remove(findById(taskLogId));
        tLRepository.saveAll(tLList);
        return targetTaskLog;
    }
}
