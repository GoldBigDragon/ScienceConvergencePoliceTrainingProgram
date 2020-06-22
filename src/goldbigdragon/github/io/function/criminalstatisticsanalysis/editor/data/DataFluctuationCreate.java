package goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.enums.AddEventType;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.main.controller.MainController;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.DataFluctuationObject;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.editor.data.object.DataObject;
import goldbigdragon.github.io.util.DateUtil;
import goldbigdragon.github.io.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

public class DataFluctuationCreate {

    RandomUtil ru;

    public void createFluctuationData(DataFluctuationObject dfo) {
        ru = new RandomUtil();
        int dataAmount;
        dataAmount = ru.random(dfo.getMinEventAmount(), dfo.getMaxEventAmount());
        if(dfo.getAddEventType() == AddEventType.AREA) {
            List<DataObject> dataObjectList = new ArrayList<>();
            for(int count = 0; count < dataAmount; count++) {
                dataObjectList.add(createData(dfo));
                if(dataObjectList.size() > 1000) {
                    MainController.dataDb.insertData(dataObjectList);
                    dataObjectList.clear();
                }
            }
            if(!dataObjectList.isEmpty()) {
                MainController.dataDb.insertData(dataObjectList);
            }
        } else {
            String[] splitted = dfo.getTempString().split("::");
            String element = splitted[1];
            if(element.equals("NULL")) {
                element = null;
            }
            double minRange = dfo.getMinStartLatitude();
            double maxRange = dfo.getMinStartLongitude();
            if(dfo.getAddEventType() == AddEventType.ENVIROMENT_ADD) {
                List<String> locations = MainController.dataDb.getNearEnviroment(splitted[0], element, minRange, maxRange, dataAmount);
                if(!locations.isEmpty()) {
                    while(true) {
                        if(dataAmount <= locations.size()) {
                            break;
                        }
                        locations.addAll(MainController.dataDb.getNearEnviroment(splitted[0], element, minRange, maxRange, dataAmount - locations.size()));
                    }
                    List<DataObject> dataObjectList = new ArrayList<>();
                    DataObject dobject;
                    double lati;
                    double longi;
                    for(int count = 0; count < locations.size(); count++) {
                        dobject = createData(dfo);
                        lati = Double.parseDouble(locations.get(count).split(Main.SPLIT_SYMBOL)[0]);
                        longi = Double.parseDouble(locations.get(count).split(Main.SPLIT_SYMBOL)[1]);
                        dobject.setLatitude(lati, lati);
                        dobject.setLongitude(longi, longi);
                        dataObjectList.add(dobject);
                        if(dataObjectList.size() > 1000) {
                            MainController.dataDb.insertData(dataObjectList);
                            dataObjectList.clear();
                        }
                    }
                    if(!dataObjectList.isEmpty()) {
                        MainController.dataDb.insertData(dataObjectList);
                    }
                }
            } else if(dfo.getAddEventType() == AddEventType.ENVIROMENT_REMOVE) {
                List<String> targetLocs = MainController.dataDb.getTargetLoc(splitted[0], splitted[1]);
                List<String> deleteTargets = new ArrayList<>();
                for(String targetloc : targetLocs) {
                    splitted = targetloc.split(Main.SPLIT_SYMBOL);
                    deleteTargets.addAll(MainController.dataDb.getRemoveTarget(dfo.getMain(), dfo.getSub(), minRange, maxRange, Double.parseDouble(splitted[0]), Double.parseDouble(splitted[1])));
                }
                for(String deleteTarget : deleteTargets) {
                    splitted = deleteTarget.split(Main.SPLIT_SYMBOL);
                    MainController.dataDb.deleteData(Integer.parseInt(splitted[0]), splitted[1]);
                }
                MainController.dataDb.vaccum();
            }
        }
    }

    private DataObject createData(DataFluctuationObject dfo) {
        String main = dfo.getMain();
        String sub = dfo.getSub();
        DateUtil du = new DateUtil();
        String startTime;
        String endTime;
        long startEpoch = ru.random(du.stringToEpoch(dfo.getMinStartTime()), du.stringToEpoch(dfo.getMaxStartTime()));
        if(dfo.getShortTime()) {
            startTime = du.epochToString(startEpoch);
            endTime = du.epochToString(ru.random(startEpoch, startEpoch+3600));
        } else {
            long endEpoch = ru.random(startEpoch, du.stringToEpoch(dfo.getMaxEndTime()));
            if(endEpoch < du.stringToEpoch(dfo.getMinEndTime())) {
                endEpoch = du.stringToEpoch(dfo.getMinEndTime());
            }
            startTime = du.epochToString(startEpoch);
            endTime = du.epochToString(endEpoch);
        }
        int amount = ru.random(dfo.getMinAmount(), dfo.getMaxAmount());

        double startLatitude = ru.random(dfo.getMinStartLatitude(), dfo.getMaxStartLatitude());
        double endLatitude = ru.random(dfo.getMinEndLatitude(), dfo.getMaxEndLatitude());
        double startLongitude = ru.random(dfo.getMinStartLongitude(), dfo.getMaxStartLongitude());
        double endLongitude = ru.random(dfo.getMinEndLongitude(), dfo.getMaxEndLongitude());

        List<String> additionalData = new ArrayList<>();
        String[] splitted;
        String type;
        String name;
        for(String data : dfo.getAdditionalData()) {
            StringBuilder sb = new StringBuilder();
            splitted = data.split(Main.SPLIT_SYMBOL);
            type = splitted[0];
            name = splitted[1];
            sb.append(type);
            sb.append(Main.SPLIT_SYMBOL);
            sb.append(name);
            sb.append(Main.SPLIT_SYMBOL);
            if(type.equals("INTEGER")) {
                sb.append(ru.random(Integer.parseInt(splitted[2]), Integer.parseInt(splitted[3])));
            } else if(type.equals("LONG")) {
                sb.append(ru.random(Long.parseLong(splitted[2]), Long.parseLong(splitted[3])));
            } else if(type.equals("REAL")) {
                sb.append(ru.random(Double.parseDouble(splitted[2]), Double.parseDouble(splitted[3])));
            } else if(type.equals("TEXT")){
                List<String> textList = new ArrayList<>();
                for(int count = 2; count < splitted.length; count++) {
                    textList.add(splitted[count]);
                }
                int index = ru.random(0, textList.size()-1);
                sb.append(textList.get(index));
            } else if(type.equals("BOOLEAN")) {
                double trueChance = Double.parseDouble(splitted[2]);
                if(trueChance > 100) {
                    sb.append(true);
                } else {
                    if(ru.random(0d, 100d) <= trueChance)
                        sb.append(true);
                    else
                        sb.append(false);
                }
            }
            additionalData.add(sb.toString());
        }
        return new DataObject(main, sub, startTime, endTime, amount, startLatitude, startLongitude, endLatitude, endLongitude, additionalData);
    }
}
