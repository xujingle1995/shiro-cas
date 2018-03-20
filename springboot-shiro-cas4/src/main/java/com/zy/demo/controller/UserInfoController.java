package com.zy.demo.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;  
   
@Controller  
@RequestMapping("/user")  
public class UserInfoController {  
	
	@RequestMapping("/all")  
    public String userAll(){  
       return "userAll";  
    } 
    /** 
     * 用户查询. 
     * @return 
     */  
    @RequestMapping("/userList")  
    @RequiresPermissions("userInfo:view")//权限管理;
    public String userInfo(){  
       return "userInfo";  
    }  
     
    /** 
     * 用户添加; 
     * @return 
     */  
    @RequestMapping("/userAdd")  
    @RequiresPermissions("userInfo:add")//权限管理;
    public String userInfoAdd(){  
       return "userInfoAdd";  
    }  
    @RequestMapping("/userDel")  
    @RequiresPermissions("userInfo:del")//权限管理;
    public String userInfoDel(){
       return "userInfoDel";  
    } 
     
}  