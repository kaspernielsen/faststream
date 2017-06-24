import java.util.concurrent.Callable;

public class Test implements Callable {

    String value;

    public Test(String value) {
        this.value = value;
    }

    public Object call() {
        return value;
    }
}
