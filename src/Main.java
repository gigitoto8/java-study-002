import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main{
    
    public static void main(String[] args) {
        String task;
        String category;

        Scanner sc = new Scanner(System.in,StandardCharsets.UTF_8);
        
        TaskService tService = new TaskService();
        TaskLogService tLService = new TaskLogService();

        System.out.println("\n");
        System.out.println("----------------------------------------");

        System.out.print("task? : ");
        task = sc.nextLine();
        System.out.print("category ? : ");
        category = sc.nextLine();
        Task task001 = new Task(task, category);
        //リストに加える
        tService.addTask(task001);
        System.out.print("task? : ");
        task = sc.nextLine();
        System.out.print("category ? : ");
        category = sc.nextLine();
        Task task002 = new Task(task, category);
        //リストに加える
        tService.addTask(task002);
        //↓動作確認で入力時間を省くため、オーバーロードで対処
        tService.addTask("買い物","家事");
        tService.addTask("書類作成","仕事");        
        //リスト一覧表示
        for(Task t : tService.getTasks()){
            System.out.println(t);
        }        
    }
}

/*
System.out.print("date? : ");
String date = sc.nextLine();
System.out.print("minutes ? : ");
int minutes = sc.nextInt();
sc.nextLine();
System.out.print("memo? : ");
String memo = sc.nextLine();

TaskLog log001 = new TaskLog(task001.getTaskId(),date ,minutes, memo);
*/