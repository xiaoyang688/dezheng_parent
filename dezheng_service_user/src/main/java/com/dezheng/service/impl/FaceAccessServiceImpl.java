package com.dezheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dezheng.dao.UserMapper;
import com.dezheng.pojo.user.FaceResult;
import com.dezheng.pojo.user.User;
import com.dezheng.service.user.FaceAccessService;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Service
public class FaceAccessServiceImpl implements FaceAccessService {

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    @Autowired
    private UserMapper userMapper;

    private static final String API_KEY = "Yr9cqldUbcUeuM7bA5zc3HVZOvqJu9Xp";

    private static final String API_SECRET = "UYWAR2bJyWLuLRyPC3B04pHEWErpF6h5";

    private static final String FACESET_TOKEN = "6d6f5ea34e69e36a1644d06180f5011a";

    /**
     * 检测人脸
     *
     * @param image_base64
     * @return
     */
    private String getFaceToken(String image_base64) {

        //post方式提交的数据
        FormBody formBody = new FormBody.Builder()
                .add("api_key", API_KEY)
                .add("api_secret", API_SECRET)
                .add("image_base64", image_base64)
                .build();

        final Request request = new Request.Builder()
                .url("https://api-cn.faceplusplus.com/facepp/v3/detect")//请求的url
                .post(formBody)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String resp = response.body().string();
            JSONObject respJson = JSONObject.parseObject(resp);
            JSONArray facesArr = (JSONArray) respJson.get("faces");
            JSONObject facesJson = (JSONObject) facesArr.get(0);
            String faceToken = (String) facesJson.get("face_token");
            return faceToken;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *创建人脸集合
     * @return
     */
   /* public String getFaceSet() {

        //post方式提交的数据
        FormBody formBody = new FormBody.Builder()
                .add("api_key", API_KEY)
                .add("api_secret", API_SECRET)
                .build();

        final Request request = new Request.Builder()
                .url("https://api-cn.faceplusplus.com/facepp/v3/faceset/create")//请求的url
                .post(formBody)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String resp = response.body().string();
            JSONObject respJson = JSONObject.parseObject(resp);
            String faceSet = (String) respJson.get("faceset_token");
            return faceSet;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
*/
    @Override
    public void addFace(String username, String image_base64) {

        User searchUser = userMapper.selectByPrimaryKey(username);

        if (searchUser.getFaceToken() != null) {
            throw new RuntimeException("人脸已录入，请勿重复录入");
        }

        String faceToken = getFaceToken(image_base64);

        //post方式提交的数据
        FormBody formBody = new FormBody.Builder()
                .add("api_key", API_KEY)
                .add("api_secret", API_SECRET)
                .add("faceset_token", FACESET_TOKEN)
                .add("face_tokens", faceToken)
                .build();

        final Request request = new Request.Builder()
                .url("https://api-cn.faceplusplus.com/facepp/v3/faceset/addface")//请求的url
                .post(formBody)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            System.out.println(response.body().string());
            User user = new User();
            user.setUsername(username);
            user.setFaceToken(faceToken);
            userMapper.updateByPrimaryKeySelective(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FaceResult searchFace(String image_base64) {

        //post方式提交的数据
        FormBody formBody = new FormBody.Builder()
                .add("api_key", API_KEY)
                .add("api_secret", API_SECRET)
                .add("faceset_token", FACESET_TOKEN)
                .add("image_base64", image_base64)
                .build();

        final Request request = new Request.Builder()
                .url("https://api-cn.faceplusplus.com/facepp/v3/search")//请求的url
                .post(formBody)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String resp = response.body().string();
            JSONObject respJson = JSONObject.parseObject(resp);
            JSONArray resultsArr = (JSONArray) respJson.get("results");
            if (resultsArr == null) {
                throw new RuntimeException("人脸未录入");
            }
            JSONObject resultsJson = (JSONObject) resultsArr.get(0);
            BigDecimal confidence = (BigDecimal) resultsJson.get("confidence");
            String faceToken = (String) resultsJson.get("face_token");
            FaceResult faceResult = new FaceResult();
            faceResult.setConfidence(confidence);
            //通过faceToken查找username
            Example example = new Example(User.class);
            example.createCriteria()
                    .andEqualTo("faceToken", faceToken);
            User user = userMapper.selectOneByExample(example);
            if (user == null) {
                throw new RuntimeException("人脸识别失败或人脸没有录入");
            }
            faceResult.setUserName(user.getUsername());
            return faceResult;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteFace(String username) {

        //通过用户名查找faceToken
        User user = userMapper.selectByPrimaryKey(username);
        String faceToken;
        System.out.println(user.getFaceToken());
        if (!"".equals(user.getFaceToken())) {
            faceToken = user.getFaceToken();
            user.setFaceToken(null);
            userMapper.updateByPrimaryKey(user);
        } else {
            throw new RuntimeException("当前用户没用录入人脸数据");
        }

        //post方式提交的数据
        FormBody formBody = new FormBody.Builder()
                .add("api_key", API_KEY)
                .add("api_secret", API_SECRET)
                .add("faceset_token", FACESET_TOKEN)
                .add("face_tokens", faceToken)
                .build();

        final Request request = new Request.Builder()
                .url("https://api-cn.faceplusplus.com/facepp/v3/faceset/removeface")//请求的url
                .post(formBody)
                .build();

        try {
            Response response = okHttpClient.newCall(request).execute();
            String resp = response.body().string();
            System.out.println(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
