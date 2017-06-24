import java.util.concurrent.Callable;

public class Test implements Callable {

    public Object call() {
        return "Hello";
    }
}
