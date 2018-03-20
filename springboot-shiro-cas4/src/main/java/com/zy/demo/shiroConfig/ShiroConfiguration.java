package com.zy.demo.shiroConfig;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.cas.CasSubjectFactory;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.DelegatingFilterProxy;

@Configuration
public class ShiroConfiguration {

    //路径不能改
    private static final String casFilterUrlPattern = "/shiro-cass";
    /**
	 * 实例化SecurityManager，该类是shiro的核心类
	 * 
	 * @return
	 */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Value("${shiro.cas}") String casServerUrlPrefix,@Value("${shiro.server}") String shiroServerUrlPrefix) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(getShiroRealm(casServerUrlPrefix,shiroServerUrlPrefix));
//        securityManager.setCacheManager( getEhCacheManager());
        securityManager.setSubjectFactory(new CasSubjectFactory());
        return securityManager;
    }
    /**
	 * 配置缓存
	 * 
	 * @return
	 */
	@Bean
	public EhCacheManager getEhCacheManager() {
		EhCacheManager em = new EhCacheManager();
		em.setCacheManagerConfigFile("classpath:config/ehcache-shiro.xml");
		return em;
	}
    @Bean
    public MyShiroRealm getShiroRealm(@Value("${shiro.cas}") String casServerUrlPrefix,
                                 @Value("${shiro.server}") String shiroServerUrlPrefix) {
        //将MyRealm改成你自己的类,其他不动
        MyShiroRealm casRealm = new MyShiroRealm();
        casRealm.setCasServerUrlPrefix(casServerUrlPrefix);
        casRealm.setCasService(shiroServerUrlPrefix + casFilterUrlPattern);
        casRealm.setDefaultRoles("user");
        casRealm.setCacheManager(getEhCacheManager());
        return casRealm;
    }
    /**
   	 * 注册单点登出的listener
   	 * 
   	 * @return
   	 */
   	@SuppressWarnings({ "rawtypes", "unchecked" })
   	@Bean
   	@Order(Ordered.HIGHEST_PRECEDENCE) // 优先级需要高于Cas的Filter
   	public ServletListenerRegistrationBean<?> singleSignOutHttpSessionListener() {
   		ServletListenerRegistrationBean bean = new ServletListenerRegistrationBean();
   		bean.setListener(new SingleSignOutHttpSessionListener());
   		bean.setEnabled(true);
   		return bean;
   	}
/*注！！！！！！！！singleSignOutFilter的bean必须！！！
 * 在DelegatingFilterProxy（shiro）代理之上否则单点登出不会生效
 *  */
   	/**
   	 * 注册单点登出filter
   	 * 
   	 * @return
   	 */
   	@Bean(name="singleSignOutFilter")
   	public FilterRegistrationBean singleSignOutFilter() {
   		FilterRegistrationBean bean = new FilterRegistrationBean();
   		System.out.println("singleSignOutFilter被创建");
   		bean.setName("singleSignOutFilter");
   		bean.setFilter(new SingleSignOutFilter());
   		bean.addUrlPatterns("/*");
   		bean.setEnabled(true);
   		return bean;
   	}
    /**
	 * 注册DelegatingFilterProxy（Shiro）
	 */
    @Bean
    public FilterRegistrationBean delegatingFilterProxy() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");
        return filterRegistration;
    }
    
    /**
	 * 下面两个配置主要用来开启shiro aop注解支持. 使用代理方式;所以需要开启代码支持;
	 * 
	 * @return
	 */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
    
    /** 
     *  开启shiro aop注解支持. 
     *  使用代理方式;所以需要开启代码支持;  加入该bean然后再加上注解@RequiresPermissions("userInfo:add")//权限管理;，权限管理才能生效
     * @param securityManager 
     * @return 
     */ 
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }
    /** 
     *  开启shiro aop注解支持. 
     *  使用代理方式;所以需要开启代码支持;  加入该bean然后再加上注解@RequiresPermissions("userInfo:add")//权限管理;，权限管理才能生效
     * @param securityManager 
     * @return 
     */ 
	@Bean
	public AuthorizationAttributeSourceAdvisor getAuthorizationAttributeSourceAdvisor(
			DefaultWebSecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
		return authorizationAttributeSourceAdvisor;
	}


    
    

    //按你业务修改
    //anon表示不过滤
    //casFilter自定义过滤器:验证成功跳转地址/验证失败跳转地址
    //logout:自定义过滤器:过滤单点登录退出请求
    private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean) {
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
//        filterChainDefinitionMap.put("logout", "singleSignOutFilter");
        //你需要CAS校验的请求
        filterChainDefinitionMap.put(casFilterUrlPattern, "casFilter");
        //你需要CAS校验的请求
        filterChainDefinitionMap.put("/login", "authc");
      //你需要CAS校验的请求
        filterChainDefinitionMap.put("/user/*", "authc");
        //不需要拦截的静态文件请求
        filterChainDefinitionMap.put("/static", "anon");
        //单点登录退出请求拦截
        filterChainDefinitionMap.put("/logout","logout");
        //不过滤其他业务系统请求
        filterChainDefinitionMap.put("/templates/*", "anon");
        //不过滤其他业务系统请求
        filterChainDefinitionMap.put("/index", "anon");
      //不过滤其他业务系统请求
        filterChainDefinitionMap.put("/**", "authc");
        
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
    }

    /**
     * 定义 CAS Filter
     */
    @Bean(name = "casFilter")
    public CasFilter getCasFilter(@Value("${shiro.cas}") String casServerUrlPrefix,
                                  @Value("${shiro.server}") String shiroServerUrlPrefix) {
        CasFilter casFilter = new CasFilter();
        casFilter.setName("casFilter");
        casFilter.setEnabled(true);
        //校验失败地址，这里失败继续重定向单点登录界面//!!!!验证失败并没有跳转
        String failUrl = casServerUrlPrefix + "/login?service=" + shiroServerUrlPrefix + casFilterUrlPattern;
        //校验成功地址，登录成功后重定向的地址
        String successUrl = shiroServerUrlPrefix + "/index";
        casFilter.setFailureUrl(failUrl);
        casFilter.setSuccessUrl(successUrl);
        return casFilter;
    }

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager,CasFilter casFilter,@Value("${shiro.cas}") String casServerUrlPrefix,@Value("${shiro.server}") String shiroServerUrlPrefix) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        String loginUrl = casServerUrlPrefix + "/login?service=" + shiroServerUrlPrefix + casFilterUrlPattern;
        shiroFilterFactoryBean.setLoginUrl(loginUrl);
        shiroFilterFactoryBean.setSuccessUrl("/");
        Map<String, Filter> filters = new HashMap<>();
        filters.put("casFilter", casFilter);
        
        LogoutFilter logoutFilter = new LogoutFilter();
//      /logout过滤后重定向到下面的路径
        logoutFilter.setRedirectUrl(casServerUrlPrefix + "/logout?service=" + shiroServerUrlPrefix);
        filters.put("logout",logoutFilter);
        
        shiroFilterFactoryBean.setFilters(filters);
        loadShiroFilterChain(shiroFilterFactoryBean);
        return shiroFilterFactoryBean;
    }
   

}  
