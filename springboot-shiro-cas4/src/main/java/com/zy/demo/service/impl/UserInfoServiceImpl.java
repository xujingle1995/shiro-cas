package com.zy.demo.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zy.demo.bean.UserInfo;
import com.zy.demo.repository.UserInfoRepository;
import com.zy.demo.service.UserInfoService;  
   
@Service  
public class UserInfoServiceImpl implements UserInfoService{  
     
    @Resource  
    private UserInfoRepository userInfoRepository;  
     
    @Override  
    public UserInfo findByUsername(String username) {  
       System.out.println("UserInfoServiceImpl.findByUsername()");  
       return userInfoRepository.findByUsername(username);  
    }

	/*@Override
	public void updateById(int role_id,int permission_id) {
		userInfoRepository.updateAuthzation(role_id, permission_id);
		// TODO Auto-generated method stub
		
	}  */
     
}