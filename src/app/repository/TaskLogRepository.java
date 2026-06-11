package app.repository;

import app.model.TaskLog;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskLogRepository {
    
    // CSVファイル名および保存先
    private static final String TASKLOG_FILE_PATH = "app/data/tasklog_record_003.csv";
    // formatterを共通化
    private static final DateTimeFormatter DATETIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    // File file = new File ・・・は
    // メソッド実行毎にクラス生成する
    // アプリ起動時のファイル有無
    boolean existFile = false;
    
    // CSVファイルファイル書き込み
    public void saveTaskLog(TaskLog taskLog){
        
        // ファイル指定
        File file = new File(TASKLOG_FILE_PATH);
        // ファイル有無確認
        existFile = file.exists();
        
        try(BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(TASKLOG_FILE_PATH,true),   // true:追記モード
                StandardCharsets.UTF_8))){
                    
            // CSVファイルが存在しない場合、またはファイルの中身が空である場合、ヘッダを１行目に挿入する。
            if(!existFile || file.length() == 0){
                bw.write("taskLogId,taskId,date,minutes,memo,createdAt,updatedAt\n");
                existFile = true;
            }
            // 一行のデータを書き込む
            bw.write(taskLog.toCsv() + "\n");
            System.out.println("保存しました");
        }catch(IOException e){
            System.out.println("ファイル書き込みエラー");
            e.printStackTrace();
        }
    }
            
    // CSVファイル読み込み
    public List<TaskLog> loadTaskLogs(){

        // ファイル指定
        File file = new File(TASKLOG_FILE_PATH);
        // ファイル有無確認
        existFile = file.exists();
            
        // リスト（読み込みデータを入れる箱）準備
        List<TaskLog> list = new ArrayList<>();
        // CSVファイルが存在しない場合、空のリストを戻しメソッドを終了させる
        if(!existFile){
            System.out.println("no TaskLog data\n");
            return list;
        }

        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(TASKLOG_FILE_PATH),
                StandardCharsets.UTF_8)))
        {            
            // 一行のデータを一時的に入れる変数を準備
            String line;
            while ((line = br.readLine()) != null) {                 
                // 1行目（カラム名）を無視
                if(line.startsWith("taskLogId")){
                    continue;
                }
                // ","区切りで文字列を分解
                String[] data = line.split(",");
                int taskLogId = Integer.parseInt(data[0]);
                int taskId = Integer.parseInt(data[1]);
                LocalDate date = LocalDate.parse(data[2]);
                int minutes = Integer.parseInt(data[3]);
                String memo = data[4];
                // dataの要素数が4よりも大きいか否か
                // String memo = ((data.length > 4) ? data[4] : "");

                LocalDateTime createdAt = LocalDateTime.parse(data[5], DATETIME_FORMATTER);
                LocalDateTime updatedAt = LocalDateTime.parse(data[6], DATETIME_FORMATTER);
                TaskLog record = new TaskLog(taskLogId,taskId, date, minutes, memo,createdAt,updatedAt);
                // 一行のデータをリストに加える
                list.add(record);
            }
        }catch(IOException e){
            System.out.println("ファイル読み込みエラー");
            e.printStackTrace();
        }
    return list;
    }

    // CSVファイルを上書き更新
    public void saveAll(List<TaskLog> tLList){
        
        // ファイル指定
        File file = new File(TASKLOG_FILE_PATH);
        // ファイル有無確認
        existFile = file.exists();
            
        // ファイルが無い場合、
        if(!existFile){
            System.out.println("CSVファイルが存在しません。");
            return;
        }
        
        try(BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(TASKLOG_FILE_PATH,false),      // false:上書きモード
                StandardCharsets.UTF_8))){
                    
            bw.write("taskLogId,taskId,date,minutes,memo,createdAt,updatedAt\n");

            for(TaskLog tL : tLList){
                // 一行のデータを書き込む
                bw.write(tL.toCsv() + "\n");
            }
            System.out.println("更新しました");
        }catch(IOException e){
            System.out.println("ファイル書き込みエラー");
            e.printStackTrace();
        }
    }
}
