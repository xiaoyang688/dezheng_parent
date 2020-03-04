import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        strings.add("1111");
        strings.add("2222");

        System.out.println("toString:" + strings.toString());

        List<Object> objects = Arrays.asList(strings.toArray());
        System.out.println("toList:" + objects);
    }

}
