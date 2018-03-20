package com.zy.demo.service;

import com.zy.demo.bean.UserInfo;

public interface UserInfoService {  
    
    /**通过username查找用户信息;*/  
    public UserInfo findByUsername(String username);  
  /*  public void updateById(int role_id,int permission_id);*/
     
}
