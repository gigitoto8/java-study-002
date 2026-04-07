import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main{

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in,StandardCharsets.UTF_8);

        System.out.print("task? : ");
        String task = sc.nextLine();
        System.out.print("category ? : ");
        String category = sc.nextLine();
        
        Task java = new Task(task, category);
        
        System.out.print("date? : ");
        String date = sc.nextLine();
        System.out.print("minutes ? : ");
        int minutes = sc.nextInt();
        sc.nextLine();
        System.out.print("memo? : ");
        String memo = sc.nextLine();

        TaskLog log001 = new TaskLog(java.getTaskId(),date ,minutes, memo);
    }
}

/*
Task shopping = new Task("shopping", "housework");
TaskLogic log002 = new TaskLogic(shopping.getTaskId(),"2026-04-01" , 30, "shoppping");
Task document = new Task("document", "work");
TaskLogic log003 = new TaskLogic(document.getTaskId(),"2026-03-15" , 90, "document");
*/
