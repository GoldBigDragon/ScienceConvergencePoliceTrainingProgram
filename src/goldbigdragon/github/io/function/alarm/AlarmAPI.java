package goldbigdragon.github.io.function.alarm;

import goldbigdragon.github.io.function.alarm.view.AlarmView;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class AlarmAPI {

    public static Map<String, Stage> alarmMap = new HashMap<>();

    public void connectionError(){new AlarmView().view("[연결 실패]\n인터넷 연결을 확인 해 주세요");
    }
    public void serverConnectionError(){
        new AlarmView().view("[서버 연결 실패]\n현재 서버가 점검중입니다!");
    }
    public void newVersionNotice(){
        new AlarmView().view("[새 버전 알림]\n[메뉴]-[개발자 홈페이지]에서\n다운로드 받으세요!");
    }
    public void threadError(String location){
        new AlarmView().view("[스레드 에러]\n스레드 처리에 문제가 발생하였습니다!\n위치 : " + location);
    }
    public void dataLoadError(){
        new AlarmView().view("[데이터 로딩 에러]\n유효하지 않은 데이터입니다!");
    }
    public void noModelFound(){
        new AlarmView().view("[모델정보 없음]\n모델 정보가 없습니다!");
    }
    public void existCategory(){
        new AlarmView().view("[카테고리 이름 중복]\n해당 카테고리는 이미 존재합니다!");
    }
    public void existElement(){
        new AlarmView().view("[엘리먼트 이름 중복]\n해당 엘리먼트는 이미 존재합니다!");
    }
    public void existPointName(){
        new AlarmView().view("[기준점수 이름 중복]\n해당 기준은 이미 존재합니다!");
    }
    public void existAdditionalValueName(){
        new AlarmView().view("[추가정보 이름 중복]\n해당 추가정보는 이미 존재합니다!");
    }
    public void invalidAccess(){
        new AlarmView().view("[접근 실패]\n유효하지 않은 접근입니다!");
    }
    public void inputValueLengthError(){
        new AlarmView().view("[입력 값 에러]\n입력 값의 길이가 충분하지 않습니다!");
    }
    public void fileExportError(){
        new AlarmView().view("[파일 출력 에러]\n파일 출력에 실패하였습니다!");
    }
    public void fileExportSuccess(){
        new AlarmView().view("[파일 출력 성공]\n파일 출력이 완료되었습니다!");
    }
    public void fileImportError(){
        new AlarmView().view("[불러오기 실패]\n지원하지 않는 형식입니다!");
    }
    public void serverCreateError(){
        new AlarmView().view("[서버 생성 실패]\n웹 페이지를 생성하지 못하였습니다!");
    }
    public void serverStartError(){
        new AlarmView().view("[서버 구동 실패]\n서버를 실행시키지 못하였습니다!");
    }
    public void listEmptyError(){
        new AlarmView().view("[데이터 없음]\n추가된 데이터가 없습니다!");
    }
    public void emptyCategory(){
        new AlarmView().view("[모델이 존재하지 않음]\n최소 1개 이상의 모델이 등록 되어있어야 합니다!");
    }
    public void existTarget(){
        new AlarmView().view("[이미 등록됨]\n대상이 이미 등록되어 있습니다!");
    }
    public void emptyDatas(){
        new AlarmView().view("[데이터 부족]\n등록된 데이터가 부족합니다!");
    }
    public void emptyVisualizationTarget(){
        new AlarmView().view("[시각화 대상 미지정]\n시각화 대상 데이터가 충분하지 않습니다!");
    }
    public void notVisualizationDbFile(){
        new AlarmView().view("[DB 형식 불일치]\n시각화 DB 파일이 아닙니다!");
    }
    public void notSelectedVisualizationTarget(){
        new AlarmView().view("[시각화 대상 미지정]\n시각화 대상 변수를 1개 이상 선택 해 주세요!");
    }
    public void tooMuchData(){
        new AlarmView().view("[시각화 대상 지정 개수 초과]\n시각화 대상을 10개 이하로 지정 해 주세요!");
    }
}
