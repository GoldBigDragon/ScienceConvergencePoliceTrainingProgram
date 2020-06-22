package goldbigdragon.github.io.function;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.ThreadType;

import java.util.Random;

public class BaseThread extends Thread {

    protected ThreadType threadType = null;
    protected String threadName = null;

    public BaseThread(ThreadType threadType, String threadName) {
        this.threadType = threadType;
        this.threadName = threadName + new Random().nextInt(65535);
    }

    @Override
    public void interrupt() {
        Main.mainVariables.threads.remove(threadName);
        super.interrupt();
    }
}
