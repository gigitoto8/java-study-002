package app.repository;

import app.model.Task;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    
    // CSVファイル名および保存先
    private static final String TASK_FILE_PATH = "app/data/task_record_003.csv";
    // formatterを共通化
    private static final DateTimeFormatter DATETIME_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    // アプリ起動時のファイル有無
    boolean existFile = false;

    // CSVファイルに追記
    public void saveTask(Task task){
        
        File file = new File(TASK_FILE_PATH);
        // ファイル有無確認       
        existFile = file.exists();
        
        try (BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(TASK_FILE_PATH, true),     // true:追記モード
                StandardCharsets.UTF_8))) {
                    
                    // CSVファイルが存在しない場合、またはファイルの中身が空である場合、ヘッダを１行目に挿入する。
            if(!existFile || file.length() == 0){
                bw.write("task_id,taskName,category,createdAt,updatedAt,deletedAt\n");
                existFile = true;
            }
            
            bw.write(task.toCsv() + "\n");
            System.out.println("保存しました");
        } catch (IOException e) {
            System.out.println("ファイル書き込みエラー");
            e.printStackTrace();
        }
    }
    
    // CSVファイル読み込み
    public List<Task> loadTasks(){

        // ファイル指定
        File file = new File(TASK_FILE_PATH);
        // ファイル有無確認
        existFile = file.exists();
        
        // リスト（読み込みデータを入れる箱）準備
        List<Task> list = new ArrayList<>(); 
        
        // CSVファイルが存在しない場合、空のリストを戻しメソッドを終了させる
        if(!existFile){
            System.out.println("no Task data\n");
            return list;
        }
        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(TASK_FILE_PATH),
                StandardCharsets.UTF_8))){            
                    
            String line;
            // ファイルの終わりまで一行ずつ読む。
            while((line = br.readLine()) != null){
                // 1行目（カラム名）を無視
                if(line.startsWith("task_id")){
                    continue;
                }
                // ","区切りで文字列を分解
                String[] data = line.split(",");
                int taskId = Integer.parseInt(data[0]);
                String task = data[1];
                String category = data[2];
                LocalDateTime createdAt = LocalDateTime.parse(data[3],DATETIME_FORMATTER);
                LocalDateTime updatedAt = LocalDateTime.parse(data[4],DATETIME_FORMATTER);
                // deleteAtがnullである場合はnullを、そうでない場合は日付データを代入する
                LocalDateTime deletedAt = data[5].equals("null") ? 
                                                null : LocalDateTime.parse(data[5],DATETIME_FORMATTER);
                // オブジェクト化
                Task record = new Task(taskId, task, category,createdAt,updatedAt,deletedAt);
                // listに追加
                list.add(record);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return list;
    }

    // CSVファイル上書き更新
    public void saveAll(List<Task> tList){
        
        // ファイル指定
        File file = new File(TASK_FILE_PATH);
        // ファイル有無確認
        existFile = file.exists();
            
        // ファイルが無い場合、
        if(!existFile){
            System.out.println("CSVファイルが存在しません。");
            return;
        }
        
        try (BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(TASK_FILE_PATH, false),    // false:上書きモード
                StandardCharsets.UTF_8))) {

            bw.write("task_id,taskName,category,createdAt,updatedAt,deletedAt\n");
            
            for(Task t : tList){
                bw.write(t.toCsv() + "\n");
            }
            System.out.println("上書きしました");
        } catch (IOException e) {
            System.out.println("ファイル書き込みエラー");
            e.printStackTrace();
        }
    }
}
