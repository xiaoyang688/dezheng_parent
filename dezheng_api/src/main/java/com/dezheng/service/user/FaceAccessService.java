package com.dezheng.service.user;

import com.dezheng.pojo.user.FaceResult;

public interface FaceAccessService {

    /**
     * 添加faceToken到faceSet
     * @param username
     */
    public void addFace(String username, String imageUrl);

    /**
     * 搜索人脸库
     * @return
     */
    public FaceResult searchFace(String imageUrl);

    /**
     * 删除人脸
     */
    public void deleteFace(String username);
}
