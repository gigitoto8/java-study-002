package app;

import java.util.Scanner;
import java.lang.Integer;
import java.time.LocalDate;
import java.time.DateTimeException; 

public class InputValidator{

    private Scanner sc;
    String input;

    public InputValidator(Scanner sc){
        this.sc = sc;
    }

    // 文字列チェック、requiredの値は、必須入力ならtrue、任意入力ならfalse
    public String inputString(boolean required){
        while(true){
            input = sc.nextLine();
            if(required){
                // 空文字チェック
                if(input.trim().isEmpty()){
                    System.out.println("空入力は無効です。");
                }else{
                    return input;
                }
            }else{
                return input;
            }
        }
    }
    
    // 数値チェック
    public int inputInt(boolean required){
        while(true){
            input = sc.nextLine();
            if(required){
                if(input.trim().isEmpty()){
                    System.out.println("空入力は無効です。");
                    continue;
                }
            }
            
            try{
                return Integer.parseInt(input);
            }catch(NumberFormatException e){
                System.out.println("整数以外が入力されています。");
            }
        }
    }
    
    // 日付チェック 
    public LocalDate inputLocalDate(boolean required){
        while(true){
            input = sc.nextLine();
            if(required){
                if(input.trim().isEmpty()){
                    System.out.println("空入力は無効です。");
                    continue;
                }
            }else{
                if(input.trim().isEmpty()){
                    return null;
                }
            }
            
            try{
                return LocalDate.parse(input);
            }catch(DateTimeException e){
                System.out.println("入力が不正です");
            }
        }
    }
}