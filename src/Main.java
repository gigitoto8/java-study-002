import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main{

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in,StandardCharsets.UTF_8);

        System.out.print("task? : ");
        String task = sc.nextLine();
        System.out.print("category ? : ");
        String category = sc.nextLine();
        Task task001 = new Task(task, category);

        System.out.print("task? : ");
        task = sc.nextLine();
        System.out.print("category ? : ");
        category = sc.nextLine();
        Task task002 = new Task(task, category);
        
        System.out.print("date? : ");
        String date = sc.nextLine();
        System.out.print("minutes ? : ");
        int minutes = sc.nextInt();
        sc.nextLine();
        System.out.print("memo? : ");
        String memo = sc.nextLine();
        TaskLog log001 = new TaskLog(task001.getTaskId(),date ,minutes, memo);
        
        System.out.print("date? : ");
        date = sc.nextLine();
        System.out.print("minutes ? : ");
        minutes = sc.nextInt();
        sc.nextLine();
        System.out.print("memo? : ");
        memo = sc.nextLine();

        TaskLog log002 = new TaskLog(task002.getTaskId(),date ,minutes, memo);
    }
}