package com.zy.demo.shiroConfig;

import javax.annotation.Resource;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;

import com.zy.demo.bean.SysPermission;
import com.zy.demo.bean.SysRole;
import com.zy.demo.bean.UserInfo;
import com.zy.demo.service.UserInfoService;  
  
/** 
 * 权限校验核心类; 由于使用了单点登录，所以无需再进行身份认证 只需要授权即可 
 *  
 * @author chhliu 
 */  
public class MyShiroRealm extends CasRealm {  
  
    @Resource  
    private UserInfoService userInfoService;  
  
    /** 
     * 1、CAS认证 ,验证用户身份  
     * 2、将用户基本信息设置到会话中,方便获取 
     * 3、该方法可以直接使用CasRealm中的认证方法，此处仅用作测试 
     */  
    @Override  
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {  
    	System.out.println("MyShiroRealm.doGetAuthenticationInfo()");
  
        // 调用父类中的认证方法，CasRealm已经为我们实现了单点认证。  
        AuthenticationInfo authc = super.doGetAuthenticationInfo(token);  
  
        // 获取登录的账号，cas认证成功后，会将账号存起来  
        String account = (String) authc.getPrincipals().getPrimaryPrincipal();  
  
        // 将用户信息存入session中,方便程序获取,此处可以将根据登录账号查询出的用户信息放到session中  
        SecurityUtils.getSubject().getSession().setAttribute("no", account);  
  
        return authc;  
    }  
  
    /** 
     * 此方法调用 hasRole,hasPermission的时候才会进行回调. 
     *  
     * 权限信息.(授权): 1、如果用户正常退出，缓存自动清空； 2、如果用户非正常退出，缓存自动清空； 
     * 3、如果我们修改了用户的权限，而用户不退出系统，修改的权限无法立即生效。 （需要手动编程进行实现；放在service进行调用） 
     * 在权限修改后调用realm中的方法，realm已经由spring管理，所以从spring中获取realm实例， 调用clearCached方法； 
     * :Authorization 是授权访问控制，用于对用户进行的操作授权，证明该用户是否允许进行当前操作，如访问某个链接，某个资源文件等。 
     *  
     * @param principals 
     * @return 
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
}  
     
 
