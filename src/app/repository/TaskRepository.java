package app.repository;

import app.model.Task;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class TaskRepository {
    
    private static final String TASK_FILE_PATH = "app/data/task_record_001.csv";

    public void saveTask(Task task){

        File file = new File(TASK_FILE_PATH);
        //ファイル有無確認
        boolean notExistFile = !file.exists();
        
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



}
