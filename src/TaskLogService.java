import java.util.ArrayList;
import java.util.List;

public class TaskLogService {

    private TaskService tService;
    public TaskLogService(TaskService tService){
        this.tService = tService;
    }
    
    private List<TaskLog> tLList = new ArrayList<>();

    //ログ追加。実際にTaskIdが使われているかチェックする
    public void addTaskLog(TaskLog taskLog){
        if(tService.existById(taskLog.getTaskId())){
            tLList.add(taskLog);
        }else{
            System.out.println("taskId \"" + taskLog.getTaskId() + "\" は存在しません");
        }
    }

    //
    public void addTaskLog(int taskId,String date,int minutes,String memo){
        if(tService.existById(taskId)){
            TaskLog taskLog001 = new TaskLog(taskId,date,minutes,memo);
            tLList.add(taskLog001);
        }else{
            System.out.println("taskId \"" + taskId + "\" は存在しません");
        }
    }

    public List<TaskLog> getTaskLogs(){
        return tLList;
    }    
}
