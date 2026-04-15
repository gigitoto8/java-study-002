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
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    
    private static final String TASK_FILE_PATH = "app/data/task_record_001.csv";

    File file = new File(TASK_FILE_PATH);
    //ファイル有無確認
    boolean notExistFile = !file.exists();

    public void saveTask(Task task){

        try (BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                    new FileOutputStream(TASK_FILE_PATH, true),
                    StandardCharsets.UTF_8))) {
            
            //CSVファイルが存在しない場合、またはファイルの中身が空である場合、ヘッダを１行目に挿入する。
            if(notExistFile | file.length() == 0){
                bw.write("task_id,task,subject\n");
            }
            
            bw.write(task.toCsv() + "\n");
            System.out.println("保存しました");
        } catch (IOException e) {
            System.out.println("ファイル書き込みエラー");
            e.printStackTrace();
        }
    }

        //ファイル読み込み
    public List<Task> loadTasks(){

        //リスト（読み込みデータを入れる箱）準備
        List<Task> list = new ArrayList<>(); 

        //CSVファイルが存在しない場合、空のリストを戻しメソッドを修了させる
        if(notExistFile){
            System.out.println("no data\n");
            return list;
        }
        try(BufferedReader br = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(TASK_FILE_PATH),
                StandardCharsets.UTF_8))){
            
                    
            String line;

            //ファイルの終わりまで一行ずつ読む。
            while((line = br.readLine()) != null){

                //1行目（カラム名）を無視
                if(line.startsWith("task_id")){
                    continue;
                }

                //","区切りで文字列を分解
                String[] data = line.split(",");

                int taskId = Integer.parseInt(data[0]);
                String task = data[1];
                String category = data[2];

                //オブジェクト化
                Task record = new Task(taskId, task, category);
                //listに追加
                list.add(record);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return list;
    }

}
