import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        strings.add("111");
        strings.add("222");
        strings.add("333");

        String s = JSONArray.toJSONString(strings);
        System.out.println("toString后：" + s);

        JSONArray jsonArray = JSONArray.parseArray(s);
        jsonArray.add("444");
        System.out.println(jsonArray.toString());

        JSONArray jsonArray1 = JSONArray.parseArray(jsonArray.toJSONString());
        jsonArray1.add("555");

        System.out.println(jsonArray1.toString());

    }
}
