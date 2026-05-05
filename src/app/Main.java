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
    private static List<TaskLog> taskLogs;
    private static List<Task> tasks;
    
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
        // 
        tasks = tService.getTasks();
        taskLogs = tLService.getTaskLogs();
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
                     5 : タスク別時間集計 \n 6 : 期間指定
                     7 : 期間指定タスク別時間集計 \n 8 : タスク検索
                     9 : 終了
                    """);

            // 整数以外を入力した場合の処理
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
                    tLService.addTaskLog(2,"2026-01-31",60,"洗濯");
                    tLService.addTaskLog(3,"2026-02-15",120,"帳簿");        
                break;

                // 3 : タスク一覧表示
                case 3:
                    tasks = tService.getTasks();
                    System.out.println("\n--------task--------------------");
                    for(Task t : tasks){
                        System.out.println(t);
                    }
                    System.out.println("--------task--------------------\n");
                break;
                    
                // 4 : ログ一覧表示
                case 4:
                    taskLogs = tLService.getTaskLogs();
                    System.out.println("\n--------tasklog--------------------");
                    for(TaskLog t : taskLogs){
                        System.out.println(t);
                    }
                    System.out.println("--------tasklog--------------------\n");
                break;
                    
                // 5 : タスク別時間集計
                case 5:
                    Map<String,Integer> mapSB = tLService.sumByTaskLogs();
                    System.out.println("\n--------result--------------------");
                    for(String key : mapSB.keySet()){
                        System.out.println("task : " + key + " / total : " + mapSB.get(key));
                    }
                    System.out.println("----------------------------\n");
                break;
                
                // 6 : 期間指定ログ表示
                case 6:
                    taskLogs = inputPeriod(sc);
                    System.out.println("\n----------------------------");
                    for(TaskLog t : taskLogs){
                        System.out.println(t);
                    }
                    System.out.println("----------------------------\n");
                break;

                // 7 : 期間指定タスク別時間集計
                case 7:
                    taskLogs = inputPeriod(sc);
                    Map<String,Integer> mapSBP = tLService.sumByPeriodTaskLogs(taskLogs);
                    System.out.println("\n----------------------------");
                    for(String key : mapSBP.keySet()){
                        System.out.println("task : " + key + " / total : " + mapSBP.get(key));
                    }
                    System.out.println("----------------------------\n");
                break;

                // 8 : タスク検索
                case 8:
                    taskLogs = inputKeyword(sc);
                    String task = "";
                    System.out.println("\n----------------------------");
                    for(TaskLog tl : taskLogs){
                        //System.out.println(tl);
                        task = tService.findById(tl.getTaskId());
                        System.out.print("taskName : " + task + " , ");
                        System.out.println(tl);
                    }
                    System.out.println("----------------------------\n");
                break;
                    
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
        System.out.print("※入力形式は、\"****-**-**\"　としてください");
        String date = sc.nextLine();
        System.out.print("minutes ? : ");
        int minutes = sc.nextInt();
        sc.nextLine();
        System.out.print("memo ? : ");
        String memo = sc.nextLine();
        System.out.print("ID ? : ");
        int taskId = sc.nextInt();
        sc.nextLine();
        return new TaskLog(taskId,date,minutes,memo);
    }

    // 期間指定入力
    static List<TaskLog> inputPeriod(Scanner sc){
        System.out.println("指定した期間のログ一覧を表示します");
        System.out.println("開始日を指定してください");
        System.out.println("※指定しない場合は入力せず、enterキーを押してください");
        System.out.print("start date ? : ");
        String from = sc.nextLine();
        System.out.println("終了日を指定してください");
        System.out.println("※指定しない場合は入力せず、enterキーを押してください");
        System.out.print("end date ? : ");
        String to = sc.nextLine();
        return tLService.periodTaskLogs(from,to);
    }
    
    static List<TaskLog> inputKeyword(Scanner sc){
        System.out.println("検索したいタスク名を入力してください : ");
        String keyword = sc.nextLine();
        return tLService.searchByTaskName(keyword);
    }

}
