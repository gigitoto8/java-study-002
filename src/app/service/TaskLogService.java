package app.service;

import app.model.TaskLog;
import app.repository.TaskLogRepository;
import java.util.ArrayList;
import java.util.List;

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

    //実際にTaskIdが使われているかチェックできた場合、リスト保存とCSV保存
    public void addTaskLog(TaskLog taskLog){
        if(tService.existById(taskLog.getTaskId())){
            tLList.add(taskLog);
            tLRepository.saveTaskLog(taskLog);
        }else{
            System.out.println("taskId \"" + taskLog.getTaskId() + "\" は存在しません");
        }
    }

    //オーバーロード
    public void addTaskLog(int taskId,String date,int minutes,String memo){
            TaskLog tl = new TaskLog(taskId,date,minutes,memo);
            addTaskLog(tl); //オーバーロード
    }

    //CSVファイルを読み込み、データをListに保存
    public void loadTaskLogCSV(){
        this.tLList = tLRepository.loadTaskLogs();
    }

    //一覧表示
    public void getTaskLogs(){
        System.out.println("\n--------tasklog-----------------");
        for(TaskLog tL : tLList){
            System.out.println(tL);
        }
        System.out.println("--------tasklog-----------------\n");
    }
}
