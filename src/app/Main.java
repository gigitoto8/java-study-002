package app;

import app.model.Task;
import app.model.TaskLog;
import app.repository.TaskLogRepository;
import app.repository.TaskRepository;
import app.service.TaskLogService;
import app.service.TaskService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
                      1 : タスク登録 \n  2 : ログ登録 
                      3 : タスク一覧表示 \n  4 : ログ一覧表示 
                      5 : タスク別時間集計 \n  6 : 期間指定
                      7 : 期間指定タスク別時間集計 \n  8 : タスク検索
                      9 : 集計結果ソート \n 
                     99 : 終了
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
                    tService.addTask("買い物","家事",
                                    LocalDateTime.parse("2005-04-01T12:00:00"),
                                    LocalDateTime.parse("2005-08-01T12:00:00"),
                                    null);
                    tService.addTask("書類作成","仕事",
                                    LocalDateTime.parse("2015-01-01T12:00:00"),
                                    LocalDateTime.parse("2015-04-01T12:00:00"),
                                    null);    
                    break;
                                
                // 2 : ログ登録
                case 2:
                    TaskLog tLInput = inputTaskLog(sc);
                    tLService.addTaskLog(tLInput);
                    // ↓サンプル入力の手間を省くため、オーバーロードで対処
                    tLService.addTaskLog(2,"2026-01-31",60,"洗濯",
                                        LocalDateTime.parse("2022-01-31T15:00:30"),
                                        LocalDateTime.parse("2022-04-30T15:00:30")
                    );
                    tLService.addTaskLog(3,"2026-02-15",120,"帳簿",
                                        LocalDateTime.parse("2022-02-28T15:00:30"),
                                        LocalDateTime.parse("2022-05-31T15:00:30"));        
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
                    Map<String,Integer> mapSB = tLService.sumByTaskLogs();
                    System.out.println("\n--------result--------------------");
                    for(Map.Entry<String,Integer> entry : mapSB.entrySet()){
                        System.out.println("taskName : " + entry.getKey() + " / total : " + entry.getValue());
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
                    for(Map.Entry<String,Integer> entry : mapSBP.entrySet()){
                        System.out.println("taskName : " + entry.getKey() + " / total : " + entry.getValue());
                    }
                    System.out.println("----------------------------\n");
                break;

                // 8 : タスク検索
                case 8:
                    taskLogs = inputKeyword(sc);
                    System.out.println("\n----------------------------");
                    for(TaskLog tl : taskLogs){
                        //String task = tService.findById(tl.getTaskId());
                        String taskName = (tService.findById(tl.getTaskId())).getTaskName();
                        System.out.print("taskName : " + taskName + " , ");
                        System.out.println(tl);
                    }
                    System.out.println("----------------------------\n");
                break;
                    
                // 9 : 集計結果ソート
                case 9 :
                    List<Map.Entry<String,Integer>> mapToList = inputSelect(sc);

                    for(Map.Entry<String,Integer> entry : mapToList){
                        System.out.println("task : " + entry.getKey() + " , minute : " + entry.getValue());
                    }
                break;

                // 99 : 終了
                case 99 :
                    System.out.println("終了します");
                return;

                default:
                    System.out.println("入力が不正です。");
            }
        }
    }

    // タスク　コンソール登録データ入力
    static Task inputTask(Scanner sc){
        System.out.print("task ? : ");
        String taskName = sc.nextLine();
        System.out.print("category ? : ");
        String category = sc.nextLine();        
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime deletedAt = null;
        return new Task(taskName,category,createdAt,updatedAt,deletedAt);
    }
    
    // タスクログ　コンソール登録データ入力
    static TaskLog inputTaskLog(Scanner sc){
        System.out.print("date ? : ");
        System.out.println("※入力形式は、\"****-**-**\"　としてください");
        String date = sc.nextLine();
        System.out.print("minutes ? : ");
        int minutes = sc.nextInt();
        sc.nextLine();
        System.out.print("memo ? : ");
        String memo = sc.nextLine();
        System.out.print("ID ? : ");
        int taskId = sc.nextInt();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        sc.nextLine();
        return new TaskLog(taskId,date,minutes,memo,createdAt,updatedAt);
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
    
    // タスク検索
    static List<TaskLog> inputKeyword(Scanner sc){
        System.out.println("検索したいタスク名を入力してください : ");
        String keyword = sc.nextLine();
        return tLService.searchByTaskName(keyword);
    }
    
    // ソート形式選択→
    static List<Map.Entry<String,Integer>> inputSelect(Scanner sc){
        boolean desc = false;
        
        while(true){

            System.out.println("集計時間をソートします : ");
            System.out.println("順番を選択してください : ");
            System.out.println(" 0 : 昇順 , 1 : 降順");

            int choice = sc.nextInt();
            sc.nextLine();
            
            switch (choice) {
                // 0 : 昇順
                case 0:
                    System.out.println("昇順を選択");
                    desc = false;        
                    return tLService.sortBySumByTime(desc);
                    // 1 : 降順
                case 1:
                    System.out.println("降順を選択");
                    desc = true;                    
                    return tLService.sortBySumByTime(desc);
                default:
                    System.out.println("\"0\"か\"1\"のいずれかを入力してください");
                break;
            }
        }
    }
}
