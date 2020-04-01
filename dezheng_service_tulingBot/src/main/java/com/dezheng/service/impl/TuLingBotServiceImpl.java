package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dezheng.pojo.tuling.InputText;
import com.dezheng.pojo.tuling.Perception;
import com.dezheng.pojo.tuling.TuLingBot;
import com.dezheng.pojo.tuling.UserInfo;
import com.dezheng.service.tulingBot.TuLingBotService;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class TuLingBotServiceImpl implements TuLingBotService {

    public static final MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
    public static final String url = "http://openapi.tuling123.com/openapi/api/v2";

    @Override
    public Map<String, Object> getAnswer(Map<String, String> question) {
        Map<String, Object> result = null;

        while (true) {
            //获取请求体
            String requestBody = getRequestBody(question);

            //获取响应
            result = getResponse(requestBody);

            //判断是否超过次数
            JSONObject intent = (JSONObject) result.get("intent");
            Integer code = (Integer) intent.get("code");
            if (code.equals(4003)) { //超过请求次数
                continue;
            }
            break;
        }
        return result;
    }

    /**
     * keyApi列表
     *
     * @return
     */
    public List<String> getApiList() {
        List<String> apiList = new ArrayList<>();
        apiList.add("bf4d1fdd260b4baeb84205c700f96623");
        apiList.add("c7b5e34e983d4415bb745a5b1e115340");
        apiList.add("ba8c88095d544440953b9f94974dc815");
        apiList.add("65cde0c1c83d46fea21add86d26fd516");
        apiList.add("1184dd3ca6b545c1ad64169c18c90fba");
        return apiList;
    }

    /**
     * 获取请求体
     *
     * @param question
     * @return
     */
    private String getRequestBody(Map<String, String> question) {

        //获取keyApi列表
        List<String> apiList = getApiList();

        if (question == null) {
            throw new RuntimeException("请输入内容！");
        }

        //获取问题
        String que = question.get("text");
        if (que == null) {
            throw new RuntimeException("请输入内容！");
        }

        //构建请求体
        TuLingBot tuLingBot = new TuLingBot();
        tuLingBot.setReqType("0");

        //设置perception
        Perception perception = new Perception();
        InputText inputText = new InputText();
        inputText.setText(que);
        perception.setInputText(inputText);
        tuLingBot.setPerception(perception);

        //设置用户信息
        UserInfo userInfo = new UserInfo();

        //随机获取api
        int index = new Random().nextInt(apiList.size());
        String apiKey = apiList.get(index);
        userInfo.setApiKey(apiKey);
        userInfo.setUserId(question.get("username"));
        tuLingBot.setUserInfo(userInfo);

        String requestBody = JSON.toJSONString(tuLingBot);

        return requestBody;

    }

    /**
     * 获取响应
     *
     * @param requestBody
     * @return
     */
    private Map<String, Object> getResponse(String requestBody) {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(requestBody, mediaType);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Map<String, Object> result = JSON.parseObject(response.body().string(), Map.class);
            return result;
        } catch (IOException e) {
            throw new RuntimeException("请求失败");
        }
    }
}
