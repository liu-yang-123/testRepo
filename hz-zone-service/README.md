
## 技术框架
  核心框架： SpringBoot 2.3.9
  
  持久层框架： Mybatis 3.5.6
  
  项目管理框架: Maven3.0以上
  
  系统使用SpringBoot+Mybatis
  
  nacos提供服务注册中心
  
##  开发环境
   建议开发者使用以下环境，可以避免版本带来的问题
   
   IDE: IntelliJ IDEA 2017+
   
   JDK: JDK1.8
   
   Maven: Maven 3.0以上
   
   mysql 5.7及以上

##代码结构包名说明
│  .gitignore

│  pom.xml

│  README.md
 
│---- zone-base-api  【区域基础模块API接口】     

│---- zone-common  【通用模块工具类、枚举类或公用方法调用，与业务无关、与框架无关】

│---- zone-db 【通用数据层接口】

│---- zone-gateway-api 【网关接入层】

│---- zone-pad-api 【移动终端API接口】   

│---- zone-sync 【数据表上传总平台模块】

│---- xxx.log  【系统运行日志】
 
##常见命名规范
### 约定俗称的惯例
1.项目名  全部小写，多个单词用中划线分隔‘-’    eg.spring-cloud

2.包名  全部小写    eg.com.alibaba.fastjson

3.类名/接口名 单词首字母大写     eg.ParserConfig,DefaultFieldDeserializer

4.变量名 首字母小写，多个单词组成时以驼峰规则命名，除首个单词，其他单词首字母都要大写     eg.password, userName

5.常量名全部大写，多个单词，用'_'分隔      eg.CACHE_EXPIRED_TIME

6.方法  同变量    eg.read(), readObject(), getById()

### 类命名
1.抽象类   Abstract或者Nase开头        eg. BaseUserService

2.枚举类  Enum作为后缀       eg. GenderEnum

3.工具类  Utils作为后缀      eg.StringUtils
 
4.异常类 Exception结尾      eg. RuntimeException

5.接口实现类  接口名+Impl      eg.UserServiceImpl

6.设计模式相关类  Builder,Factory等  使用到设计模式时，需要使用对应的设计模式作为后缀，如ThreadFactory

7.处理特定功能的类   Handler,Predicate,Validator   标识处理器，校验器，断言，这些类工厂还有配套的方法名 eg.handle,predicate,validate

8.特定层级类  Controller,Service,ServiceImpl,Dao,Mapper后缀    eg.UserController,UserServiceImpl,UserMapper

9.特定层级的值对象 Dto,Param,Bo,Config,Message   Param调用入参  Dto传输对象  Config配置类 Message消息类

10.测试类  Test结尾    eg.UserServiceTest,表示用来测试UserService类   


###方法命名

1.不做过多要求，采用小驼峰形式命名，尽量达到通过函数名字能够直接明白该函数实现了什么样的功能。

###代码规范注释

1.建议IDE、Eclipse安装 阿里代码规约插件，统一代码风格。参考地址：https://github.com/alibaba/p3c