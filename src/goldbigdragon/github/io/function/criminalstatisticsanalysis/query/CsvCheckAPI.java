package goldbigdragon.github.io.function.criminalstatisticsanalysis.query;

import goldbigdragon.github.io.enums.DatabaseType;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.AdditionalDataColumnObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.AdditionalDataTableObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.DataTableObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.object.ModelObject;
import goldbigdragon.github.io.util.FileUtil;

import java.io.*;
import java.util.*;

public class CsvCheckAPI {

    private String path;

    public CsvCheckAPI(String path){
        File file = new File(path);
        File directory = new File(file.getParent()+File.separator+file.getName());
        directory.mkdirs();
        directory.mkdir();
        this.path = directory.getAbsolutePath()+File.separator;
    }

    public DatabaseType check(){
        FileUtil fu = new FileUtil();
        File f = new File(path);
        String[] header = null;
        try {
            header = fu.readFile(f, "MS949", 0, 1).get(0).split(",");
        } catch(IOException e){
            e.printStackTrace();
        }
        if(header.length > 1 && header[0].equals("카테고리") && header[1].equals("엘리먼트")) {
            return DatabaseType.CRIMINAL_STATISTICS_ANALYSIS_MODEL;
        } else if (header.length > 7 && header[0].equals("엘리먼트") && header[1].equals("시작 시간") && header[2].equals("종료 시간")
                && header[3].equals("개수") && header[4].equals("시작 위도") && header[5].equals("시작 경도")
                && header[6].equals("종료 위도") && header[7].equals("종료 경도")) {
            return DatabaseType.CRIMINAL_STATISTICS_ANALYSIS_DATA;
        }
        return null;
    }

    public void createModelCsv(){
        File file = new File(path+"model.csv");
        List<String> pointList = MainController.modelDb.getPoint();
        List<ModelObject> defaultPoint = MainController.modelDb.getAll(pointList);

        List<String> lines = new ArrayList<>();
        StringBuilder head = new StringBuilder();
        head.append("카테고리,엘리먼트");
        for(String pointName : pointList) {
            head.append(","+pointName);
        }
        lines.add(head.toString());

        StringBuilder line;
        Map<String, Double> pointMap;
        for(ModelObject mo : defaultPoint) {
            pointMap = mo.getPointMap();
            line = new StringBuilder();
            line.append(mo.getMain());
            line.append(",");
            line.append(mo.getSub());
            for(String key : pointMap.keySet()) {
                line.append(",");
                line.append(pointMap.get(key));
            }
            lines.add(line.toString());
        }

        FileUtil fu = new FileUtil();
        if (file != null) {
            try {
                fu.writeFile(file, lines, "MS949");
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void createDataCsv(){
        List<String> categoryList = MainController.dataDb.getCategory();
        File file;
        BufferedWriter writer = null;
        int pageCount;
        List<AdditionalDataColumnObject> adcoList;
        List<DataTableObject> dtoList;
        List<AdditionalDataTableObject> adtoList;
        for(String category : categoryList) {
            file = new File(path+category+".csv");
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "MS949"));
                adcoList = MainController.dataDb.getAdditionalDataColumns(category);
                StringBuilder headSb = new StringBuilder();
                headSb.append("엘리먼트,시작 시간,종료 시간,개수,시작 위도,시작 경도,종료 위도,종료 경도");
                for(AdditionalDataColumnObject adco : adcoList) {
                    headSb.append("," + adco.type + "::" + adco.name);
                }
                writer.write(headSb.toString() + "\r\n");
                pageCount = 0;
                for(;;) {
                    dtoList = MainController.dataDb.getDatas(category, null, "엘리먼트", null, 1000, pageCount);
                    if(dtoList.isEmpty()) {
                        break;
                    } else {
                        for(DataTableObject dto : dtoList) {
                            adtoList = MainController.dataDb.getAdditionalData(Integer.parseInt(dto.getNum().getValue()), category);
                            StringBuilder sb = new StringBuilder();
                            sb.append(dto.getElement().getValue());
                            sb.append(",");
                            sb.append(dto.getStartTime());
                            sb.append(",");
                            sb.append(dto.getEndTime());
                            sb.append(",");
                            sb.append(dto.getAmount().getValue());
                            sb.append(",");
                            sb.append(dto.getStartLatitude());
                            sb.append(",");
                            sb.append(dto.getStartLongitude());
                            sb.append(",");
                            sb.append(dto.getEndLatitude());
                            sb.append(",");
                            sb.append(dto.getEndLongitude());
                            for(AdditionalDataTableObject adto : adtoList) {
                                sb.append(",");
                                sb.append(adto.getValue().getValue());
                            }
                            sb.append("\r\n");
                            writer.write(sb.toString());
                        }
                        pageCount++;
                    }
                }
                writer.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
