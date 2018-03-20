# shiro-cas
单点登录以及单点登出
一共有三部分 
1.cas client工程springboot-shiro-cas4
2.cas server cas文件夹，放到tomcat中启动
3.shiro.sql文件 导入数据库信息
也可以看一下另一篇文章 也是根据本工程写的 https://www.showdoc.cc/web/#/35766799839476?page_id=205929499076989

springboot-shiro-cas4这个工程  这只是cas  client端的代码，cas server端的配置可以自行网上查找配置，只需去官网下载，
然后将war包重命名为cas.war放在tomcat的webapps文件夹下，启动tomcat，会自动解压出cas文件夹，
然后修改\Tomcat 8.0\webapps\cas\WEB-INF\deployerConfigContext.xml文件即可
当然也可以下载我配置好的server

cas server 可以将cas文件夹复制到de webapps文件夹下
cas 文件夹也可以百度云下载 链接：https://pan.baidu.com/s/1v5n1G95GD8We1vXeaxrH1A 密码：nzx9 将压缩包解压，放到tomcat的WebApps文件夹下即可
deployerConfigContext.xml文件（在WEB-INF文件夹下）需要修改两处：
<!-- 数据库连接信息 -->

	<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">

		<property name="driverClassName"><value>com.mysql.jdbc.Driver</value></property>

		<property name="url"><value>jdbc:mysql://localhost:3306/shiro</value></property>

		<property name="username"><value>root</value></property>

		<property name="password"><value>xjl130110009</value></property>

	</bean>
  数据库信息需要自己修改
  <bean class="org.jasig.cas.adaptors.jdbc.QueryDatabaseAuthenticationHandler">

					<property name="dataSource" ref="dataSource"></property>

					<property name="sql" value="select password from user_info where username=?">
					</property>

					<!-- <property name="passwordEncoder" ref="MD5PasswordEncoder"></property> -->

				</bean>
sql 语句需要修改，根据自己数据库存放的用户名以及密码

数据库文件shiro.sql文件，可以直接创建数据库信息
数据库导入 链接：https://pan.baidu.com/s/1tD3DvmvNjuzJL2QQOf7lZw 密码：6001 可以下载文件导入到自己的数据库中

上面的cas server 在tomcat中启动后，将springboot-shiro-cas4运行起来，此外数据库服务也要启动 访问http://localhost:8095/user/userDel http://localhost:8095/user/userAdd   http://localhost:8095/user/userList 会跳转到cas登录界面  输入用户名和密码  xjl   123就会跳转到相应的界面
