package goldbigdragon.github.io.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public ArrayList<String> readFile(File file, String charName) throws IOException {
        ArrayList<String> readed = new ArrayList<>();
        if(file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charName);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String readedLine;
            while((readedLine=bufferedReader.readLine())!=null)
                readed.add(readedLine);
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        }
        return readed;
    }

    public ArrayList<String> readFile(File file, String charName, int startLine, int endLine) throws IOException {
        ArrayList<String> readed = new ArrayList<>();
        if(file.exists()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charName);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String readedLine;
            int nowLine = 0;
            while((readedLine=bufferedReader.readLine())!=null) {
                if(nowLine >= startLine) {
                    if(nowLine < endLine) {
                        readed.add(readedLine);
                    } else {
                        break;
                    }
                }
                nowLine++;
            }
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        }
        return readed;
    }

    public String readResourceFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream in = getClass().getResourceAsStream("/resources/" + path);
        InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String readedLine;
        while((readedLine=bufferedReader.readLine())!=null)
            sb.append(readedLine);
        bufferedReader.close();
        inputStreamReader.close();
        in.close();
        return sb.toString();
    }

    public void writeFile(File file, List<String> lines, String charName) throws IOException {
        if(!file.exists()) {
            String dirPath = file.getAbsolutePath().replace("/", "\\");
//            System.out.println(dirPath);
//            System.out.println(dirPath.substring(0, dirPath.lastIndexOf("\\")));
            dirPath = dirPath.substring(0, dirPath.lastIndexOf("\\"));
            File dir = new File(dirPath);
            dir.mkdirs();
            file.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, charName);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        for(String line : lines) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        outputStreamWriter.flush();
        fileOutputStream.flush();
        outputStreamWriter.close();
        bufferedWriter.close();
        fileOutputStream.close();
    }

    public void appendFile(File file, List<String> lines) throws IOException {
        List<String> prev = new ArrayList<>();
        if(!file.exists()) {
            file.mkdirs();
            file.createNewFile();
        } else {
            prev = readFile(file, "UTF8");
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF8");
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        if(!prev.isEmpty()) {
            for(String line : prev) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
        }
        for(String line : lines) {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
        }
        bufferedWriter.flush();
        outputStreamWriter.flush();
        fileOutputStream.flush();
        outputStreamWriter.close();
        bufferedWriter.close();
        fileOutputStream.close();
    }
}
