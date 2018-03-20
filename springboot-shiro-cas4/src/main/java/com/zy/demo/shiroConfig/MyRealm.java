package com.zy.demo.shiroConfig;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.zy.demo.bean.SysPermission;
import com.zy.demo.bean.SysRole;
import com.zy.demo.bean.UserInfo;
import com.zy.demo.service.UserInfoService;

public class MyRealm extends  CasRealm {

    private static Logger logger = LoggerFactory.getLogger(MyRealm.class);
    @Autowired
    private UserInfoService userInfoService;
    

    /**
     * 单Cas服务登录校验通过后，便会调用这个方法，并携带用户信息的Token参数
     * 假设只要是有Token过来，就说明是有效的登录用户，不再对密码等做校验
     * 方法名称 : doGetAuthenticationInfo
     * 功能描述 : 验证当前登陆的Subject
     * @param authcToken 当前登录用户的token
     * @return 验证信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) {
    	
        AuthenticationInfo token = super.doGetAuthenticationInfo(authcToken);
        String userName =(String) token.getPrincipals().getPrimaryPrincipal();
        System.out.println("doGetAuthenticationInfo");
        logger.info("当前Subject时获取到用户名为" + userName);
        //根据用户名，查找用户信息
        UserInfo user = userInfoService.findByUsername(userName);
        if (user != null) {
             //user字符应该是固定写法
            SecurityUtils.getSubject().getSession().setAttribute("user", user);
        }
        //这个token返回后便会进入配置中的成功路径
        return token;
    }

    /**
     * 这里应该是请求用户的权限的方法，页面中 <shiro:hasRole name="ROLE_ADMIN"> 等类似的权限标签才会请求的方法，迁移过来业务相关代码，不解释了.
     * 方法名称 : doGetAuthorizationInfo
     * 功能描述 : 获取登录用户的权限信息
     * @param principals 登录用户信息
     * @return 用户权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    	System.out.println("权限配置-->MyShiroRealm.doGetAuthorizationInfo()");
    	SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();  
        // 获取单点登陆后的用户名，也可以从session中获取，因为在认证成功后，已经将用户名放到session中去了  
        String userName = (String) super.getAvailablePrincipal(principals);  
//              principals.getPrimaryPrincipal(); 这种方式也可以获取用户名  
  
        // 根据用户名获取该用户的角色和权限信息  
        UserInfo userInfo = userInfoService.findByUsername(userName);  
  
        // 将用户对应的角色和权限信息打包放到AuthorizationInfo中  
        for (SysRole role : userInfo.getRoleList()) {  
            authorizationInfo.addRole(role.getRole());  
            for (SysPermission p : role.getPermissions()) {  
                authorizationInfo.addStringPermission(p.getPermission());  
            }  
        }  
  
        return authorizationInfo;  
    }

    /**
     * 方法名称 : loginUser
     * 功能描述 : 登陆用户信息
     * @param userName 用户名
     * @return 用户信息
     *//*
    public InfoUserBean loginUser(String userName) {
        //查询用户信息
        InfoUserBean userBean = infoUserMapper.selectByName(userName);
        String pass = userBean.getPassword();
        //这里是对数据库提取的密码做加密操作，业务逻辑不必深究
        Object[] result = DataConvertUtil.getPassAndSaltByte(userBean.getPassword());
        String passwordHexStr = Hex.encodeHexString((byte[]) result[1]);
        userBean.setPassword(passwordHexStr);
        return userBean;
    }*/
}
