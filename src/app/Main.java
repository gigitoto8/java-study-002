package app;

import app.model.Task;
import app.model.TaskLog;
import app.repository.TaskLogRepository;
import app.repository.TaskRepository;
import app.service.TaskLogService;
import app.service.TaskService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main{

    private static TaskService tService;
    private static TaskLogService tLService;
    
    public static void main(String[] args) {
        // 初期化ブロック
        init();
        // 処理ブロック
        run();
    }
    
    static void init(){
        // Repository生成（CSVファイルへの読み書きを担当）
        TaskRepository tRepository = new TaskRepository();
        TaskLogRepository tLRepository = new TaskLogRepository();
        // service生成
        // TaskRepositoryを渡して依存関係を構築
        tService = new TaskService(tRepository);
        // TaskLogRepositoryを渡して依存関係を構築
        // taskId存在チェックのため、TaskServiceも必要
        tLService = new TaskLogService(tService,tLRepository);
        // CSVからデータ読み込み、Listに復元
        tService.loadTaskCSV();
        tLService.loadTaskLogCSV();
    }
    
    static void run(){
        
        System.out.println("\n----------------------------------------");
        System.out.println("-----タスク管理アプリ----");
        System.out.println("----------------------------------------\n\n");

        Scanner sc = new Scanner(System.in,StandardCharsets.UTF_8);
        while (true) { 
            System.out.println("----------------------------------------");
            
            System.out.println("実行したい機能を選択してください");
            System.out.println("""
                     1 : タスク登録 \n 2 : ログ登録 
                     3 : タスク一覧表示 \n 4 : ログ一覧表示 
                     5 : タスク別時間集計 \n 6 : 期間指定集計
                     7 : 検索 \n 8 : 
                     9 : 終了
                    """);

            //整数以外を入力した場合の処理
            if(!sc.hasNextInt()){
                System.out.println("数値を入力してください");
                sc.nextLine();
                continue;
            }
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                // 1 : タスク登録
                case 1:
                    Task tInput = inputTask(sc);
                    tService.addTask(tInput);
                    // ↓サンプル入力の手間を省くため、オーバーロードで対処
                    tService.addTask("買い物","家事");
                    tService.addTask("書類作成","仕事");                    
                    break;
                                
                // 2 : ログ登録
                case 2:
                    TaskLog tLInput = inputTaskLog(sc);
                    tLService.addTaskLog(tLInput);
                    // ↓サンプル入力の手間を省くため、オーバーロードで対処
                    tLService.addTaskLog(2,"2026/01/31",60,"洗濯");
                    tLService.addTaskLog(3,"2026/02/15",120,"帳簿");        
                    tLService.addTaskLog(4,"2020/10/15",300,"買い物");        
                break;

                // 3 : タスク一覧表示
                case 3:
                    List<Task> tasks = tService.getTasks();
                    System.out.println("\n--------task--------------------");
                    for(Task t : tasks){
                        System.out.println(t);
                    }
                    System.out.println("--------task--------------------\n");
                break;
                    
                // 4 : ログ一覧表示
                case 4:
                    List<TaskLog> taskLogs = tLService.getTaskLogs();
                    System.out.println("\n--------tasklog--------------------");
                    for(TaskLog t : taskLogs){
                        System.out.println(t);
                    }
                    System.out.println("--------tasklog--------------------\n");
                break;
                
                // 5 : タスク別時間集計
                case 5:
                    Map<String,Integer> map = tLService.sumByTaskLogs();
                    for(String key : map.keySet()){
                        System.out.println("task : " + key + " / total : " + map.get(key));
                    }
                break;
                
                /*
                // 4 : ログ一覧表示
                case 6:
                    tLService.getTaskLogs();
                break;
                    
                // 4 : ログ一覧表示
                case 7:
                    tLService.getTaskLogs();
                break;
                */
                        
                // 9 : 終了
                case 9 :
                    System.out.println("終了します");
                return;

                default:
                    System.out.println("入力が不正です。");
            }            
        }
    }

    static Task inputTask(Scanner sc){
        System.out.print("task ? : ");
        String task = sc.nextLine();
        System.out.print("category ? : ");
        String category = sc.nextLine();            
        return new Task(task,category);
    }
    
    static TaskLog inputTaskLog(Scanner sc){
        System.out.print("date ? : ");
        String date = sc.nextLine();
        System.out.print("minutes ?  ");
        System.out.print("****/  ");
        int minutes = sc.nextInt();
        sc.nextLine();
        System.out.print("memo ? : ");
        String memo = sc.nextLine();
        System.out.print("ID ? : ");
        int taskId = sc.nextInt();
        sc.nextLine();
        return new TaskLog(taskId,date,minutes,memo);
    }
}
