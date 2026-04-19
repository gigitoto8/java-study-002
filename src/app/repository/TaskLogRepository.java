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
import java.util.ArrayList;
import java.util.List;

public class TaskLogRepository {
    
    private static final String TASKLOG_FILE_PATH = "app/data/tasklog_record_001.csv";

    //File file = new File ・・・は
    //メソッド実行毎にクラス生成する
    
    //ファイル書き込み
    public void saveTaskLog(TaskLog taskLog){
        
        //ファイル指定
        File file = new File(TASKLOG_FILE_PATH);
        //ファイル有無確認
        boolean notExistFile = !file.exists();
        
        try(BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(TASKLOG_FILE_PATH,true), 
                StandardCharsets.UTF_8))){
                    
            //CSVファイルが存在しない場合、またはファイルの中身が空である場合、ヘッダを１行目に挿入する。
            if(notExistFile || file.length() == 0){
                bw.write("task_id,date,minutes,memo\n");
            }
            //一行のデータを書き込む
            bw.write(taskLog.toCsv() + "\n");
            System.out.println("保存しました");
        }catch(IOException e){
            System.out.println("ファイル書き込みエラー");
            e.printStackTrace();
        }
    }
            
    //ファイル読み込み
    public List<TaskLog> loadTaskLogs(){

        //ファイル指定
        File file = new File(TASKLOG_FILE_PATH);
        //ファイル有無確認
        boolean notExistFile = !file.exists();
            
        //リスト（読み込みデータを入れる箱）準備
        List<TaskLog> list = new ArrayList<>();
        //CSVファイルが存在しない場合、空のリストを戻しメソッドを終了させる
        if(notExistFile){
            System.out.println("no data\n");
            return list;
        }

        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(TASKLOG_FILE_PATH),
                StandardCharsets.UTF_8)))
        {            
            //一行のデータを一時的に入れる変数を準備
            String line;
            while ((line = br.readLine()) != null) {                 
                //1行目（カラム名）を無視
                if(line.startsWith("task_id")){
                    continue;
                }
                //","区切りで文字列を分解
                String[] data = line.split(",");
                int taskId = Integer.parseInt(data[0]);
                String date = data[1];
                int minutes = Integer.parseInt(data[2]);
                //dataの要素数が3以上（data[3]が入力済）であるか否か
                String memo = ((data.length > 3) ? data[3] : "");
                //String memo = data[3];
                TaskLog record = new TaskLog(taskId, date, minutes, memo);
                //一行のデータをリストに加える
                list.add(record);
            }
        }catch(IOException e){
            System.out.println("ファイル読み込みエラー");
            e.printStackTrace();
        }
    return list;
    }
}
