package app;

import app.model.Task;
import app.repository.TaskRepository;
import app.service.TaskLogService;
import app.service.TaskService;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main{
    
    public static void main(String[] args) {
        String task;
        String category;
        String date;
        int minutes;
        String memo;
        int taskId;

        //入力関係
        Scanner sc = new Scanner(System.in,StandardCharsets.UTF_8);
        
        //TaskRepositoryクラス用
        TaskRepository tRepository = new TaskRepository();
        // TaskRepositoryをTaskServiceに渡すことで、
        // タスク追加時に「メモリへの登録」と「CSVへの保存」を一貫して行えるようにする
        TaskService tService = new TaskService(tRepository);
        // TaskServiceをTaskLogServiceに渡すことで、
        // TaskLog登録時にtaskIdの存在チェックを行う（同一のTaskデータを参照するため）
        TaskLogService tLService = new TaskLogService(tService);
        
        System.out.println("----------------------------------------");
        
        tService.loadTaskCSV();

        System.out.print("task ? : ");
        task = sc.nextLine();
        System.out.print("category ? : ");
        category = sc.nextLine();
        Task task001 = new Task(task, category);

        //taskリストに加えると同時に、CSVファイルに保存
        tService.addTask(task001);
        /*
        */
        //↓動作確認で入力時間を省くため、オーバーロードで対処
        tService.addTask("買い物","家事");
        tService.addTask("書類作成","仕事");
        //taskリスト一覧表示およびCSV保存
        tService.getTasks();

        
        /*
        System.out.print("date ? : ");
        date = sc.nextLine();
        System.out.print("minutes ? : ");
        minutes = sc.nextInt();
        sc.nextLine();
        System.out.print("memo ? : ");
        memo = sc.nextLine();
        System.out.print("ID ? : ");
        taskId = sc.nextInt();
        
        TaskLog taskLog001 = new TaskLog(taskId ,date ,minutes, memo);
        //tasklogリストに加える
        tLService.addTaskLog(taskLog001);
        //↓動作確認で入力時間を省くため、オーバーロードで対処
        tLService.addTaskLog(2,"2026-01-31",60,"洗濯");
        tLService.addTaskLog(3,"2026-02-15",120,"帳簿");        
        tLService.addTaskLog(4,"2020-10-15",300,"買い物");        
        //tasklogリスト一覧表示
        for(TaskLog t : tLService.getTaskLogs()){
            System.out.println(t);
        }        
        */
    }
}
