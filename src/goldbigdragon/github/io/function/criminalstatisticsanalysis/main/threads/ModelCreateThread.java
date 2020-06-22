package goldbigdragon.github.io.function.criminalstatisticsanalysis.main.threads;

import goldbigdragon.github.io.enums.ThreadType;
import goldbigdragon.github.io.function.BaseThread;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.query.DefaultCreate;

public class ModelCreateThread extends BaseThread {

    public ModelCreateThread(){
        super(ThreadType.BASIC, "ModelCreator");
    }

    @Override
    public void run() {
        new DefaultCreate().createModelDb();
        interrupt();
//        new DefaultCreate().createDataDb();
//        new DefaultCreate().createRandomData();
    }
}
