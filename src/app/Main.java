package app;

import app.model.Task;
import app.model.TaskLog;
import app.repository.TaskLogRepository;
import app.repository.TaskRepository;
import app.service.TaskLogService;
import app.service.TaskService;
import app.InputValidator;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main{

    private static TaskService taskService;
    private static TaskLogService taskLogService;
    
    public static void main(String[] args) {
        // 初期化ブロック
        init();
        // 処理ブロック
        run();
    }
    
    static void init(){
        // Repository生成（CSVファイルへの読み書きを担当）
        TaskRepository taskRepository = new TaskRepository();
        TaskLogRepository taskLogRepository = new TaskLogRepository();
        // TaskRepositoryを渡して依存関係を構築
        taskService = new TaskService(taskRepository);
        // TaskLogRepositoryを渡して依存関係を構築
        // taskId存在チェックのため、TaskServiceも必要
        taskLogService = new TaskLogService(taskService,taskLogRepository);
        // CSVからデータ読み込み、Listに変換
        taskService.loadTaskCSV();
        taskLogService.loadTaskLogCSV();
    }
    
    static void run(){
        //入力関係
        Scanner sc = new Scanner(System.in,StandardCharsets.UTF_8);
        //InputValidatorクラス用
        InputValidator iv = new InputValidator(sc);
        
        System.out.println("\n----------------------------------------");
        System.out.println("-----タスク管理アプリ----");
        System.out.println("----------------------------------------\n\n");

        while (true) { 
            System.out.println("----------------------------------------");
            
            System.out.println("実行したい機能の数値を入力してください");
            System.out.println("""
                    -   1 : タスク登録 \n-   2 : ログ登録 
                    -   3 : タスク一覧表示 \n-   4 : ログ一覧表示 
                    -   5 : タスク別時間集計 \n-   6 : 期間指定
                    -   7 : 期間指定タスク別時間集計 \n-   8 : タスク検索
                    -   9 : 集計結果ソート \n-  10 : Task タスク名変更
                    -  11 : Task カテゴリー変更 \n-  12 : TaskLog ログ日付変更
                    -  13 : TaskLog 実施時間変更 \n-  14 : TaskLog メモ変更
                    -  15 : タスク削除 \n-  16 : ログ削除
                    -  99 : 終了
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
                    Task tInput = inputTask(iv);
                    taskService.addTask(tInput);
                    // ↓サンプル入力の手間を省くため、オーバーロードで対処
                    taskService.addTask("買い物","家事",
                                    LocalDateTime.parse("2005-04-01T12:00:00"),
                                    LocalDateTime.parse("2005-08-01T12:00:00"),
                                    null);
                    taskService.addTask("書類作成","仕事",
                                    LocalDateTime.parse("2015-01-01T12:00:00"),
                                    LocalDateTime.parse("2015-04-01T12:00:00"),
                                    null);    
                    break;
                                
                // 2 : ログ登録
                case 2:
                    TaskLog tLInput = inputTaskLog(iv);
                    taskLogService.addTaskLog(tLInput);
                    // ↓サンプル入力の手間を省くため、オーバーロードで対処
                    taskLogService.addTaskLog(2,LocalDate.parse("2026-01-31"),60,"洗濯",
                                        LocalDateTime.parse("2022-01-31T15:00:30"),
                                        LocalDateTime.parse("2022-04-30T15:00:30")
                    );
                    taskLogService.addTaskLog(3,LocalDate.parse("2026-02-15"),120,"帳簿",
                    LocalDateTime.parse("2022-02-28T15:00:30"),
                    LocalDateTime.parse("2022-05-31T15:00:30"));        
                break;

                // 3 : タスク一覧表示
                case 3:
                    List<Task> tasks = taskService.getTasks();
                    System.out.println("\n--------task--------------------");
                    for(Task t : tasks){
                        System.out.println(t);
                    }
                    System.out.println("--------task--------------------\n");
                    break;
                    
                // 4 : ログ一覧表示
                case 4:
                    List<TaskLog> taskLogs = taskLogService.getTaskLogs();
                    System.out.println("\n--------tasklog--------------------");
                    for(TaskLog t : taskLogs){
                        System.out.println(t);
                    }
                    System.out.println("--------tasklog--------------------\n");
                break;
                    
                // 5 : タスク別時間集計
                case 5:
                    Map<String,Integer> totalMinutesByTask = taskLogService.sumByTaskLogs();
                    System.out.println("\n--------result--------------------");
                    for(Map.Entry<String,Integer> entry : totalMinutesByTask.entrySet()){
                        System.out.println("taskName : " + entry.getKey() + " / total : " + entry.getValue());
                    }
                    System.out.println("----------------------------\n");
                break;
                
                // 6 : 期間指定ログ表示
                case 6:
                    taskLogs = inputPeriod(iv);
                    System.out.println("\n----------------------------");
                    for(TaskLog t : taskLogs){
                        System.out.println(t);
                    }
                    System.out.println("----------------------------\n");
                break;

                // 7 : 期間指定タスク別時間集計
                case 7:
                    taskLogs = inputPeriod(iv);
                    Map<String,Integer> totalMinutesByTaskInPeriod = taskLogService.sumByPeriodTaskLogs(taskLogs);
                    System.out.println("\n----------------------------");
                    for(Map.Entry<String,Integer> entry : totalMinutesByTaskInPeriod.entrySet()){
                        System.out.println("taskName : " + entry.getKey() + " / total : " + entry.getValue());
                    }
                    System.out.println("----------------------------\n");
                break;

                // 8 : タスク検索
                case 8:
                    taskLogs = inputKeyword(iv);
                    System.out.println("\n----------------------------");
                    for(TaskLog tl : taskLogs){
                        //String task = taskService.findById(tl.getTaskId());
                        String taskName = (taskService.findById(tl.getTaskId())).getTaskName();
                        System.out.print("taskName : " + taskName + " , ");
                        System.out.println(tl);
                    }
                    System.out.println("----------------------------\n");
                break;
                    
                // 9 : 集計結果ソート
                case 9 :
                    List<Map.Entry<String,Integer>> sortedTotals = inputSelect(iv);

                    for(Map.Entry<String,Integer> entry : sortedTotals){
                        System.out.println("task : " + entry.getKey() + " , minute : " + entry.getValue());
                    }
                break;

                //10 更新（Task:taskName）
                case 10:
                    String column = "タスク名";   
                    int targetId = inputUpdateId(column, iv);
                    String text = inputText(column,iv,true);
                    if(taskService.existById(targetId)){
                        taskService.updateTaskName(targetId,text);
                        System.out.println("\n--------task--------------------");
                        System.out.println("以下の通り変更しました");
                        System.out.println(taskService.findById(targetId));
                        System.out.println("--------task--------------------\n");
                    }else{
                        System.out.println("CSVファイルまたは所定のIDが存在しません");
                    }
                    break;

                //11 更新（Task:category）
                case 11:
                    column = "カテゴリー";   
                    targetId = inputUpdateId(column, iv);
                    text = inputText(column,iv,true);
                    if(taskService.existById(targetId)){
                        taskService.updateCategory(targetId,text);
                        System.out.println("\n--------task--------------------");
                        System.out.println("以下の通り変更しました");
                        System.out.println(taskService.findById(targetId));
                        System.out.println("--------task--------------------\n");
                    }else{
                        System.out.println("CSVファイルまたは所定のIDが存在しません");
                    }
                    break;

                //12 更新（TaskLog:date）
                case 12:
                    column = "ログ日時";   
                    targetId = inputUpdateId(column, iv);
                    LocalDate date = inputLocalDate(column,iv);
                    // 論理削除済みのtaskIdの場合、updateDateの戻り値はnull
                    if(taskLogService.updateDate(targetId,date) != null){
                        System.out.println("\n--------taskLog--------------------");
                        System.out.println("以下の通り変更しました");
                        System.out.println(taskLogService.findById(targetId));
                        System.out.println("--------taskLog--------------------\n");
                    }else{
                        System.out.println("CSVファイルまたは所定のIDが存在しません");
                    }
                    break;

                //13 更新（TaskLog:minutes）
                case 13:
                    column = "実施時間";   
                    targetId = inputUpdateId(column, iv);
                    int value = inputValue(column,iv);
                    if(taskLogService.existById(targetId)){
                        taskLogService.updateMinutes(targetId,value);
                        System.out.println("\n--------taskLog--------------------");
                        System.out.println("以下の通り変更しました");
                        System.out.println(taskLogService.findById(targetId));
                        System.out.println("--------taskLog--------------------\n");
                    }else{
                        System.out.println("CSVファイルまたは所定のIDが存在しません");
                    }
                    break;

                //14 更新（TaskLog:memo）
                case 14:
                    column = "メモ";   
                    targetId = inputUpdateId(column, iv);
                    text = inputText(column,iv,false);
                    if(taskLogService.existById(targetId)){
                        taskLogService.updateMemo(targetId,text);
                        System.out.println("\n--------taskLog--------------------");
                        System.out.println("以下の通り変更しました");
                        System.out.println(taskLogService.findById(targetId));
                        System.out.println("--------taskLog--------------------\n");
                    }else{
                        System.out.println("CSVファイルまたは所定のIDが存在しません");
                    }
                    break;

                //15 Task削除
                case 15:
                    targetId = inputDeleteId(iv);
                    if(taskService.existById(targetId)){
                        System.out.println("\n--------task--------------------");
                        System.out.println(taskService.deleteTask(targetId));
                        System.out.println("以上のタスクを削除しました");
                        System.out.println("--------task--------------------\n");
                    }else{
                        System.out.println("CSVファイルまたは所定のIDが存在しません");
                    }
                    break;

                //16 TaskLog削除
                case 16:
                    targetId = inputDeleteId(iv);
                    if(taskLogService.existById(targetId)){
                        System.out.println("\n--------taskLog--------------------");
                        System.out.println(taskLogService.deleteTaskLog(targetId));
                        System.out.println("以上のログを削除しました");
                        System.out.println("--------taskLog--------------------\n");
                    }else{
                        System.out.println("CSVファイルまたは所定のIDが存在しません");
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
    static Task inputTask(InputValidator iv){
        System.out.print("task ? : ");
        String taskName = iv.inputString(true);
        System.out.print("category ? : ");
        String category = iv.inputString(true);        
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        LocalDateTime deletedAt = null;
        return new Task(taskName,category,createdAt,updatedAt,deletedAt);
    }
    
    // タスクログ　コンソール登録データ入力
    static TaskLog inputTaskLog(InputValidator iv){
        System.out.print("date ? : ");
        System.out.println("※入力形式は、\"****-**-**\"　としてください");
        LocalDate date = iv.inputLocalDate(true);
        System.out.print("minutes ? : ");
        int minutes = iv.inputInt(true);
        System.out.print("memo ? : ");
        String memo = iv.inputString(false);
        System.out.print("ID ? : ");
        int taskId = iv.inputInt(true);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        return new TaskLog(taskId,date,minutes,memo,createdAt,updatedAt);
    }

    // 期間指定入力
    static List<TaskLog> inputPeriod(InputValidator iv){
        System.out.println("指定した期間のログ一覧を表示します");
        System.out.println("開始日を指定してください");
        System.out.println("※指定しない場合は入力せず、enterキーを押してください");
        System.out.print("start date ? : ");
        LocalDate from = iv.inputLocalDate(false);
        System.out.println("終了日を指定してください");
        System.out.println("※指定しない場合は入力せず、enterキーを押してください");
        System.out.print("end date ? : ");
        LocalDate to = iv.inputLocalDate(false);
        return taskLogService.periodTaskLogs(from,to);
    }
    
    // タスク検索
    static List<TaskLog> inputKeyword(InputValidator iv){
        System.out.println("検索したいタスク名を入力してください : ");
        String keyword = iv.inputString(true);    
        return taskLogService.searchByTaskName(keyword);
    }
    
    // ソート形式選択→
    static List<Map.Entry<String,Integer>> inputSelect(InputValidator iv){
        boolean desc = false;
        
        while(true){

            System.out.println("集計時間をソートします : ");
            System.out.println("順番を選択してください : ");
            System.out.println(" 0 : 昇順 , 1 : 降順");

            int choice = iv.inputInt(true);
            
            switch (choice) {
                // 0 : 昇順
                case 0:
                    System.out.println("昇順を選択");
                    desc = false;        
                    return taskLogService.sortBySumByTime(desc);
                    // 1 : 降順
                case 1:
                    System.out.println("降順を選択");
                    desc = true;                    
                    return taskLogService.sortBySumByTime(desc);
                default:
                    System.out.println("\"0\"か\"1\"のいずれかを入力してください");
                break;
            }
        }
    }

    // 変更ID入力
    static int inputUpdateId(String column,InputValidator iv){
        System.out.println(column + "を変更したいIDを入力してください。");
        System.out.println("Id? : ");
        int targetId = iv.inputInt(true);
        return targetId;
    }

    // 変更後文字列入力
    static String inputText(String column,InputValidator iv,boolean required){
        String text = "";
        System.out.println("変更後の" + column + "を入力してください。");
        System.out.println("変更後" + column + " : ");
        if(required){
            text = iv.inputString(true);
        }else{
            text = iv.inputString(false);
        }
        return text;
    }
    
    // 変更後数値入力
    static int inputValue(String column,InputValidator iv){
        int value = 0;
        System.out.println("変更後の" + column + "を入力してください。");
        System.out.println("変更後" + column + " : ");
        value = iv.inputInt(true);
        return value;
    }
    
    // 変更後日付入力
    static LocalDate inputLocalDate(String column,InputValidator iv){
        System.out.println("変更後の" + column + "を入力してください。");
        System.out.println("変更後" + column + " : ");

        LocalDate date = iv.inputLocalDate(true);

        return date;
    }
    
    // 削除ID入力
    static int inputDeleteId(InputValidator iv){
        System.out.println("削除したいタスクまたはログのIDを入力してください。");
        System.out.println("Id? : ");
        int id = iv.inputInt(true);   
        return id;
    }
}
