package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dezheng.WuLaiTypeEnum;
import com.dezheng.dto.AnswerDTO;
import com.dezheng.dto.UserDTO;
import com.dezheng.service.user.UserService;
import com.dezheng.service.wulaiBot.WuLaiBotService;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

@Service
public class WuLaiBotServiceImpl implements WuLaiBotService {

    @Reference
    private UserService userService;

    private OkHttpClient okHttpClient = new OkHttpClient();
    private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private static String pubkey = "gMx00Efda4GcOobi1oMTC3CgIbTWu0KZ00a127184eb2f8aae6";
    private static String secret = "E6iKKOMbP0iBTbLrYIYu";
    private static MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    private String getResponse(String json, String url){
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
    public void createUser(String username) {
        Map userInfo = userService.getUserInfo(username);
        UserDTO userDTO = new UserDTO();
        userDTO.setUser_id(username);
        userDTO.setNickname(username);
        //设置头像
        userDTO.setAvatar_url((String) userInfo.get("haedPic"));
        String json = JSON.toJSONString(userDTO);
        String response = getResponse(json, WuLaiTypeEnum.CREATE_USER.getUrl());
        System.out.println(response);
    }

    @Override
    public String getAnswer(String username, String question) {
        AnswerDTO answerDTO = new AnswerDTO();
        AnswerDTO.MsgBodyBean msg_body = new AnswerDTO.MsgBodyBean();
        AnswerDTO.MsgBodyBean.TextBean text = new AnswerDTO.MsgBodyBean.TextBean();
        text.setContent(question);
        msg_body.setText(text);
        answerDTO.setMsg_body(msg_body);
        answerDTO.setUser_id(username);
        String json = JSON.toJSONString(answerDTO);
        System.out.println(json);
        String response = getResponse(json, WuLaiTypeEnum.GET_ANSWER.getUrl());
        JSONObject respJson = JSONObject.parseObject(response);
        JSONArray suggested_response = (JSONArray) respJson.get("suggested_response");
        if (suggested_response.size() == 0) {
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
}
