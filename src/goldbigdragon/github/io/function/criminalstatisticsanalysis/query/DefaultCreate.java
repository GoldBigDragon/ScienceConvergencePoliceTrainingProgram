package goldbigdragon.github.io.function.criminalstatisticsanalysis.query;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.DataAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.ModelAPI;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.ModelCreate;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.model.object.ModelObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.DataObject;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DefaultCreate {
    private List<String> pointList = new ArrayList<>();

    public void createModelDb(){
        String database = Main.dbDirectory + "/data/model/default";
        File original = new File(database + ".db");
        original.delete();

        pointList.add("치안 점수");
        pointList.add("취약 점수");
        pointList.add("범죄 점수");
        List<ModelObject> modelObjects = new ArrayList<>();
        modelObjects.add(new ModelObject(getPoint(new double[]{1, -1, -1}),"경찰 정보", "교통 단속 카메라"));
        modelObjects.add(new ModelObject(getPoint(new double[]{3, -1, -1}),"경찰 정보", "CCTV"));
        modelObjects.add(new ModelObject(getPoint(new double[]{3, 0, 0}),"경찰 정보", "순찰 차량"));
        modelObjects.add(new ModelObject(getPoint(new double[]{2, 0, 0}),"경찰 정보", "순찰 바이크"));
        modelObjects.add(new ModelObject(getPoint(new double[]{1, 0, -1}),"경찰 정보", "경찰 인력"));
        modelObjects.add(new ModelObject(getPoint(new double[]{1, -1, -1}),"경찰 정보", "긴급 신고 버튼"));
        modelObjects.add(new ModelObject(getPoint(new double[]{3, -1, -1}),"경찰 정보", "드론"));
        modelObjects.add(new ModelObject(getPoint(new double[]{5, -2, -1}),"경찰 정보", "헬리콥터"));

        modelObjects.add(new ModelObject(getPoint(new double[]{0, 10, 0}),"범죄 정보", "살인"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 8, 0}),"범죄 정보", "강도"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 5, 0}),"범죄 정보", "절도"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 4, 0}),"범죄 정보", "폭력"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 10, 0}),"범죄 정보", "방화"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 4, 0}),"범죄 정보", "마약"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 3, 0}),"범죄 정보", "도박"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 10, 0}),"범죄 정보", "성폭력"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 6, 0}),"범죄 정보", "약취/유인"));


        modelObjects.add(new ModelObject(getPoint(new double[]{0, 1, 3}),"조명 정보", "어두움"));
        modelObjects.add(new ModelObject(getPoint(new double[]{1, -1, -1}),"조명 정보", "밝음"));

        modelObjects.add(new ModelObject(getPoint(new double[]{0, 1, 2}),"거주 정보", "깨진 창문"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 1, 1}),"거주 정보", "1인 가구"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 0, -1}),"거주 정보", "다인 가구"));

        modelObjects.add(new ModelObject(getPoint(new double[]{0, 0, 2}),"상점 정보", "높은 매출"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 0, 0}),"상점 정보", "낮은 매출"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 2, 10}),"상점 정보", "유흥"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 0, 0}),"상점 정보", "한식"));

        modelObjects.add(new ModelObject(getPoint(new double[]{0, 1, 5}),"지역 정보", "주택가"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 0, -1}),"지역 정보", "공업단지"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 0, -10}),"지역 정보", "논/밭"));

        modelObjects.add(new ModelObject(getPoint(new double[]{0, 0, 3}),"교통 정보", "사고 다발지역"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 0, 2}),"교통 정보", "상습 결빙구간"));

        modelObjects.add(new ModelObject(getPoint(new double[]{0, 2, 3}),"민간 정보", "높은 유동인구"));
        modelObjects.add(new ModelObject(getPoint(new double[]{0, 0, 1}),"민간 정보", "낮은 유동인구"));

        modelObjects.add(new ModelObject(getPoint(new double[]{0, 0, 2}),"기타 정보", "통신 신호가 약함"));

        createDirectory(Main.dbDirectory + "/data/model");
        new ModelCreate(database, pointList, modelObjects).close();
        MainController.modelDb = new ModelAPI(database+".db");
    }

    private Map<String, Double> getPoint(double[] pointArray) {
        Map<String, Double> pointMap = new HashMap<>();
        for(int count = 0; count < pointList.size(); count++) {
            if(count >= pointArray.length)
                pointMap.put(pointList.get(count), 0d);
            else
                pointMap.put(pointList.get(count), pointArray[count]);
        }
        return pointMap;
    }

    public void createDataDb(){
        String database = Main.dbDirectory + "/data/data/default";
        File original = new File(database + ".db");
        original.delete();
        List<DataObject> dataObjects = new ArrayList<>();
        dataObjects.add(new DataObject("경찰 정보", "교통 단속 카메라", "1999-04-23 13:35:22", "2020-04-23 13:35:22", 2, 35.18004967420183D, 129.0768319261628D, 35.18004967420183D, 129.0768319261628D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"CVT-04876","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1264665021"}));
        dataObjects.add(new DataObject("경찰 정보", "교통 단속 카메라", "1999-04-23 13:35:22", "2020-04-23 13:35:22", 3, 35.18073214258812D, 129.0722393411303D, 35.18073214258812D, 129.0722393411303D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"CVT-04876","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1264665021"}));
        dataObjects.add(new DataObject("경찰 정보", "교통 단속 카메라", "1999-04-23 13:35:22", "2020-04-23 13:35:22", 1, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"AET-06816","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1263665021"}));

        dataObjects.add(new DataObject("경찰 정보", "CCTV", "1999-04-23 13:35:22", "2020-04-23 13:35:22", 11, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"E0C4A8F6","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1264665021"}));
        dataObjects.add(new DataObject("경찰 정보", "CCTV", "1999-04-23 13:35:22", "2020-04-23 13:35:22", 3, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"T0442","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1264665021"}));
        dataObjects.add(new DataObject("경찰 정보", "CCTV", "1999-04-23 13:35:22", "2020-04-23 13:35:22", 7, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"A7104","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1264965021"}));


        dataObjects.add(new DataObject("경찰 정보", "순찰 차량", "2006-04-02 13:35:22", "2020-04-23 13:35:22", 8, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"AVANTE","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1260965021"}));
        dataObjects.add(new DataObject("경찰 정보", "순찰 차량", "2008-04-01 13:35:22", "2020-04-23 13:35:22", 2, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"GRAND STAREX","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1260965021"}));
        dataObjects.add(new DataObject("경찰 정보", "순찰 차량", "2009-04-09 13:35:22", "2020-04-23 13:35:22", 1, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"TM170","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1260965021"}));

        dataObjects.add(new DataObject("경찰 정보", "순찰 바이크", "2009-04-23 13:35:22", "2020-04-23 13:35:22", 10, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"FLHTP","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1260965021"}));

        dataObjects.add(new DataObject("경찰 정보", "경찰 인력", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 32, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"업무"+Main.SPLIT_SYMBOL+"생활안전","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1561165021"}));
        dataObjects.add(new DataObject("경찰 정보", "경찰 인력", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 42, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"업무"+Main.SPLIT_SYMBOL+"경비","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1561165021"}));
        dataObjects.add(new DataObject("경찰 정보", "경찰 인력", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 52, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"업무"+Main.SPLIT_SYMBOL+"수사","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1561165021"}));
        dataObjects.add(new DataObject("경찰 정보", "경찰 인력", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 52, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"업무"+Main.SPLIT_SYMBOL+"형사","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1561165021"}));
        dataObjects.add(new DataObject("경찰 정보", "경찰 인력", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 12, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"업무"+Main.SPLIT_SYMBOL+"사이버안전","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1561165021"}));
        dataObjects.add(new DataObject("경찰 정보", "경찰 인력", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 42, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"업무"+Main.SPLIT_SYMBOL+"교통","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1561165021"}));
        dataObjects.add(new DataObject("경찰 정보", "긴급 신고 버튼", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 1, 35.18478775393567D, 129.09030006181146D, 35.18478775393567D, 129.09030006181146D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"DW-7754","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1460965021"}));
        dataObjects.add(new DataObject("경찰 정보", "드론", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 2, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"SPPC-5424","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1560965021"}));
        dataObjects.add(new DataObject("경찰 정보", "헬리콥터", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 2, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"Bell-206L3","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1560965021"}));
        dataObjects.add(new DataObject("경찰 정보", "헬리콥터", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 1, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"H-500D","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1560965021"}));
        dataObjects.add(new DataObject("경찰 정보", "헬리콥터", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 1, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"모델"+Main.SPLIT_SYMBOL+"Kazan MI-172","LONG"+Main.SPLIT_SYMBOL+"배치일"+Main.SPLIT_SYMBOL+"1560965021"}));
        dataObjects.add(new DataObject("경찰 정보", "드론", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 7, 35.17899306118523D, 129.07433547433112D, 35.17899306118523D, 129.07433547433112D, new String[]{}));

        dataObjects.add(new DataObject("범죄 정보", "살인", "2015-06-04 18:12:33", "2015-06-04 18:28:24", 1, 35.18771190378241D, 129.0827240145926D, 35.18771190378241D, 129.0827240145926D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"가해자 성별"+Main.SPLIT_SYMBOL+"남","INTEGER"+Main.SPLIT_SYMBOL+"가해자 연령"+Main.SPLIT_SYMBOL+"52", "TEXT"+Main.SPLIT_SYMBOL+"피해자 성별"+Main.SPLIT_SYMBOL+"남","INTEGER"+Main.SPLIT_SYMBOL+"피해자 연령"+Main.SPLIT_SYMBOL+"57"}));

        dataObjects.add(new DataObject("범죄 정보", "폭력", "2015-02-13 12:42:12", "2015-02-13 12:56:34", 1, 35.18771051451517D, 129.0825428586543D, 35.18771051451517D, 129.0825428586543D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"가해자 성별"+Main.SPLIT_SYMBOL+"남","INTEGER"+Main.SPLIT_SYMBOL+"가해자 연령"+Main.SPLIT_SYMBOL+"48", "TEXT"+Main.SPLIT_SYMBOL+"피해자 성별"+Main.SPLIT_SYMBOL+"남","INTEGER"+Main.SPLIT_SYMBOL+"피해자 연령"+Main.SPLIT_SYMBOL+"44"}));

        dataObjects.add(new DataObject("범죄 정보", "강도", "2016-09-21 20:17:56", "2016-09-21 20:36:04", 1, 35.18647633191788D, 129.08827419061436D, 35.18647633191788D, 129.08827419061436D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"가해자 성별"+Main.SPLIT_SYMBOL+"남","INTEGER"+Main.SPLIT_SYMBOL+"가해자 연령"+Main.SPLIT_SYMBOL+"42", "TEXT"+Main.SPLIT_SYMBOL+"피해자 성별"+Main.SPLIT_SYMBOL+"여","INTEGER"+Main.SPLIT_SYMBOL+"피해자 연령"+Main.SPLIT_SYMBOL+"27", "TEXT"+Main.SPLIT_SYMBOL+"주거지"+Main.SPLIT_SYMBOL+"빌라","INTEGER"+Main.SPLIT_SYMBOL+"층수"+Main.SPLIT_SYMBOL+"2"}));

        dataObjects.add(new DataObject("범죄 정보", "절도", "2014-03-08 08:34:11", "2014-03-08 08:38:24", 1, 35.186939496782756D, 129.08676025829942D, 35.186939496782756D, 129.08676025829942D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"가해자 성별"+Main.SPLIT_SYMBOL+"남","INTEGER"+Main.SPLIT_SYMBOL+"가해자 연령"+Main.SPLIT_SYMBOL+"54", "TEXT"+Main.SPLIT_SYMBOL+"피해자 성별"+Main.SPLIT_SYMBOL+"남","INTEGER"+Main.SPLIT_SYMBOL+"피해자 연령"+Main.SPLIT_SYMBOL+"46", "TEXT"+Main.SPLIT_SYMBOL+"대상"+Main.SPLIT_SYMBOL+"차량","TEXT"+Main.SPLIT_SYMBOL+"타입"+Main.SPLIT_SYMBOL+"AVANTE","TEXT"+Main.SPLIT_SYMBOL+"차량 번호"+Main.SPLIT_SYMBOL+"14가5728"}));

        dataObjects.add(new DataObject("범죄 정보", "도박", "2018-06-01 02:12:54", "2020-08-21 12:32:12", 1, 35.18683064391607D, 129.08850005210203D, 35.18683064391607D, 129.08850005210203D, new String[]{}));

        dataObjects.add(new DataObject("조명 정보", "어두움", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 1, 35.18485331158434D, 129.08989560717868D, 35.18485331158434D, 129.08989560717868D, new String[]{}));

        dataObjects.add(new DataObject("거주 정보", "깨진 창문", "1997-04-23 13:35:22", "2020-04-23 13:35:22", 3, 35.173818264226945D, 129.07853366188294D, 35.173818264226945D, 129.07853366188294D, new String[]{}));

        dataObjects.add(new DataObject("거주 정보", "1인 가구", "2019-11-27 05:31:24", "2020-04-23 13:35:22", 3, 35.185224306036105D, 129.0977534048324D, 35.185224306036105D, 129.0977534048324D, new String[]{"TEXT"+Main.SPLIT_SYMBOL+"거주자 성별"+Main.SPLIT_SYMBOL+"남", "INTEGER"+Main.SPLIT_SYMBOL+"거주자 연령"+Main.SPLIT_SYMBOL+"21"}));

        createDirectory(Main.dbDirectory + "/data/data");
        DataAPI dapi = new DataAPI(database);
        dapi.createGeneralTable();
        dapi.createAdditionalDataTable(dataObjects);
        dapi.insertData(dataObjects);
        dapi.close();
        MainController.dataDb = new DataAPI(database+".db");
    }

    private String getDataSet(String type, String name, String value){
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        sb.append(Main.SPLIT_SYMBOL);
        sb.append(name);
        sb.append(Main.SPLIT_SYMBOL);
        sb.append(value);
        return sb.toString();
    }

    private void createDirectory(String path) {
        File d = new File(path);
        d.mkdirs();
    }
}