# shiro-cas
单点登录以及单点登出

springboot-shiro-cas4这个工程  这只是cas  client端的代码，cas server端的配置可以自行网上查找配置，只需去官网下载，
然后将war包重命名为cas.war放在tomcat的webapps文件夹下，启动tomcat，会自动解压出cas文件夹，
然后修改\Tomcat 8.0\webapps\cas\WEB-INF\deployerConfigContext.xml文件即可将该文件夹
当然也可以下载我配置好的server

cas server 可以参考 链接：https://pan.baidu.com/s/1v5n1G95GD8We1vXeaxrH1A 密码：nzx9 将压缩包解压，放到tomcat的WebApps文件夹下即可
deployerConfigContext.xml文件需要修改两处：
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

数据库导入 链接：https://pan.baidu.com/s/1tD3DvmvNjuzJL2QQOf7lZw 密码：6001 可以下载文件导入到自己的数据库中
