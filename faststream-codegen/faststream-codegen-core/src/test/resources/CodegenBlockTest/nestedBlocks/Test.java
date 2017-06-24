public class Test {

    public void foo() {
        String str = "fooo";
        int i = 123;
        if (i > 321) {
            System.out.println(str + "first1");
            if (i < 121) {
                System.out.println(str + "second");
            }
            System.out.println(str + "first2");
        }
    }
}
