package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.speech.AipSpeech;
import com.dezheng.WuLaiTypeEnum;
import com.dezheng.dto.AnswerDTO;
import com.dezheng.dto.UserDTO;
import com.dezheng.service.user.UserService;
import com.dezheng.service.wulaiBot.WuLaiBotService;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

@Service
public class WuLaiBotServiceImpl implements WuLaiBotService {

    @Reference
    private UserService userService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private OkHttpClient okHttpClient = new OkHttpClient();
    /**
     * 吾来机器人api
     */
    private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private static String pubkey = "gMx00Efda4GcOobi1oMTC3CgIbTWu0KZ00a127184eb2f8aae6";
    private static String secret = "E6iKKOMbP0iBTbLrYIYu";
    private static MessageDigest md;

    /**
     * 百度语音识别Api
     */
    public static final String APP_ID = "19967792";
    public static final String API_KEY = "sFVl5SUf4TWZRdOKxnMAvO4C";
    public static final String SECRET_KEY = "d0iCrN6qh01qPS0mfQ7X35Asr7orgSsS";

    static {
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void createUser(String username) {
        Map userInfo = userService.getUserInfo(username);
        if (userInfo == null) {
            throw new RuntimeException("用户未登录");
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setUser_id(username);
        userDTO.setNickname(username);
        //设置头像
        userDTO.setAvatar_url((String) userInfo.get("haedPic"));
        String json = JSON.toJSONString(userDTO);
        getResponse(json, WuLaiTypeEnum.CREATE_USER.getUrl());
    }

    @Override
    public String getAnswer(String username, String question) {
        String json = getReqJson(username, question);
        System.out.println(json);
        String response = getResponse(json, WuLaiTypeEnum.GET_ANSWER.getUrl());
        JSONObject respJson = JSONObject.parseObject(response);
        JSONArray suggested_response = (JSONArray) respJson.get("suggested_response");
        if (suggested_response == null) {
            return null;
        }
        respJson = (JSONObject) suggested_response.get(0);
        JSONArray respArr = (JSONArray) respJson.get("response");
        if (respArr.size() == 0) {
            return null;
        }

        JSONObject msgBodyJson = (JSONObject) respArr.get(0);
        JSONObject msgBody = (JSONObject) msgBodyJson.get("msg_body");
        JSONObject textJson = (JSONObject) msgBody.get("text");
        String content = (String) textJson.get("content");
        System.out.println(content);
        return content;
    }

    @Override
    public void sendMessage(Map message) {
        JSONObject messageJson = new JSONObject(message);
        JSONObject msgBody = (JSONObject) messageJson.get("msg_body");
        JSONObject textJson = (JSONObject) msgBody.get("text");
        String content = (String) textJson.get("content");
        String username = (String) messageJson.get("user_id");
        JSONObject result = new JSONObject();
        result.put("username", username);
        result.put("answer", content);
        rabbitTemplate.convertAndSend("sendMessage", "", result.toJSONString());
    }

    @Override
    public void receiveMessage(String username, String question) {
        String json = getReqJson(username, question);
        String response = getResponse(json, WuLaiTypeEnum.RECEIVE_MESSAGE.getUrl());
        System.out.println(response);
    }

    /**
     * 构建请求体
     * msg_body : {"text":{"content":"测试文本消息"}}
     * user_id : string
     */
    private String getReqJson(String username, String question) {
        AnswerDTO answerDTO = new AnswerDTO();
        AnswerDTO.MsgBodyBean msg_body = new AnswerDTO.MsgBodyBean();
        AnswerDTO.MsgBodyBean.TextBean text = new AnswerDTO.MsgBodyBean.TextBean();
        text.setContent(question);
        msg_body.setText(text);
        answerDTO.setMsg_body(msg_body);
        answerDTO.setUser_id(username);
        return JSON.toJSONString(answerDTO);
    }

    private String getResponse(String json, String url) {
        RequestBody body = RequestBody.create(json, MEDIA_TYPE);
        Long timeStamp = System.currentTimeMillis() / 1000;
        String nonce = UUID.randomUUID().toString().replace("-", "");
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Api-Auth-pubkey", pubkey)
                .addHeader("Api-Auth-nonce", nonce)
                .addHeader("Api-Auth-timestamp", String.valueOf(timeStamp))
                .addHeader("Api-Auth-sign", DigestUtils.sha1Hex(nonce + timeStamp + secret))
                .post(body)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String voiceToText(byte[] voice) {
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);
        org.json.JSONObject asrRes2 = client.asr(voice, "wav", 16000, null);
        org.json.JSONArray result = (org.json.JSONArray) asrRes2.get("result");
        return (String) result.get(0);
    }
}
