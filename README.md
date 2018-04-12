
## 做这个种子的心路历程

最近在做一个大型的J2EE项目，后端语言选择了Java，理所当然的选择了Spring，使用Spring MVC来做restful风格的api开发很是方便，Spring下面有很多子项目通过Springboot集成也很舒服。程序员都知道沟通很重要，实际项目中，往往是各自为战，尽管使用的是相同的框架、工具，编写的代码却千差万别，为了统一基础代码风格，编写了这个项目种子。

除此之外，在开发一个Web后端api项目时，通常都会经历搭建项目、选择依赖管理工具、引入基础包依赖、配置框架等，为了加快项目的开发进度（早点下班）还需要封装一些常用的类和工具，如标准的响应结构体封装、统一异常处理切面、接口签名认证、初始化运行方法、轮询方法、api版本控制封装、异步方法配置等。

每次开始一个类型的新项目，以上这些步骤又要重复一遍，虽然能够将老项目拿过来删删减减达到目的，但还是很费时费力，还容易出问题。所以，可以利用面向对象的思想，抽取这类Web后端api项目的共同之处封装成一个项目种子。以后再开发类似的项目，就能直接在这个项目种子上迭代，减少重复劳动。

如果你有类似的需求，可以克隆下来试试。欢迎star或fork，如果在使用中发现问题或者有什么建议欢迎提 issue 或 pr 一起完善。

## 使用方法

1. 克隆本项目到本地
2. 使用IDEA打开，选择*.gradle文件，使用gradle构建本项目
3. 下载项目需要的依赖包
4. 运行Application.java中的main函数

## 特征

- 当前的springboot为1.5.10版
- 统一JSON响应结构、返回码封装
- 基于 @ControllerAdvice 统一异常处理
- 基于 ApplicationRunner 的初始化
- 基于 HandlerInterceptor 的 jwt 等拦截器
- 多种关系型数据库支持
- 基于 slf4j/logback 的日志处理
- 基于 @Scheduled 的轮询处理
- 基于 @Async 的异步任务处理
- 封装好的 RestApiVersion 处理 api 版本

## springboot 选择

当前 springboot 更新到 2.0.0 。新版本增加了spring webflux 支持。springboot 1.5.x 版本会持续更新，springboot 2.0.0 要求 jdk 至少为1.8 。springboot 2.0.0 是springboot的另一个分支。当前种子选择 springboot 1.5.10 ，后续会增加 springboot 2.0.0 的分支。

## 统一JSON响应结构、返回码封装。

```Java

public class StandardResponseDTO<T> {

    private int code;
    private String message;
    private T data;

    public StandardResponseDTO() {
    }

    /**
     * 成功时返回
     *
     * @param code 响应码
     * @param data   响应体
     */
    public StandardResponseDTO(int code, T data) {
        this(code, "", data);
    }

    // 失败时返回
    public StandardResponseDTO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    // getter or setter
}

```

## 基于 @ControllerAdvice 统一异常处理

自定义异常可以在末尾添加， @ExceptionHandler中填写异常类，@ResponseStatus为响应状态码返回，ErrorCodeEnum 为自定义返回码枚举。

```Java

@ControllerAdvice
public class ExceptionsHandler implements ThrowsAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionsHandler.class);

    @ExceptionHandler(DataAccessResourceFailureException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public StandardResponseDTO dataAccessResourceFailureException(DataAccessResourceFailureException e) {
        LOGGER.error(e.getMessage());
        return HttpResponseUtil.error(ErrorCodeEnum.CANNOT_ACCESS_DATABASE);
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public StandardResponseDTO nullPointerException(NullPointerException e) {
        LOGGER.error(e.getMessage());
        return HttpResponseUtil.error(ErrorCodeEnum.NULL_POINTER_EXCEPTION);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public StandardResponseDTO ioException(IOException e) {
        LOGGER.error(e.getMessage());
        return HttpResponseUtil.error(ErrorCodeEnum.IO_EXCEPTION);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public StandardResponseDTO exception(Exception e) {
        LOGGER.error("{}, message: {}", e.getClass(), e.getMessage());
        return HttpResponseUtil.error(ErrorCodeEnum.UNKNOWN_EXCEPTION);
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public StandardResponseDTO serviceException(ServiceException e) {
        LOGGER.error(e.getMessage());
        return HttpResponseUtil.error(ErrorCodeEnum.SERVICE_EXCEPTION, e.getMessage());
    }

    @ExceptionHandler(IllegalParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public StandardResponseDTO illegalParameterException(IllegalParameterException e) {
        LOGGER.error(e.getMessage());
        return HttpResponseUtil.error(ErrorCodeEnum.ILLEGAL_PARAMETER_EXCEPTION);
    }
}

```

## 基于 ApplicationRunner 的初始化

在 run 函数中可初始化数据库，清楚缓存等。

```Java

@Component
public class StartupRunnerConfig implements ApplicationRunner {
    @Resource
    private
    InitService service;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        service.init();
    }
}


```

## 基于 HandlerInterceptor 的 jwt 等拦截器

preHandle 函数中返回 true 表示验证通过，请求会向下传递，返回false请求会被打回。

```Java

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        // TODO jwt处理
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}

```

## 多种关系型数据库支持

支持 H2、MySQL、Oracle、Sqlite 数据库，相应的模板连接文件已经配置好，修改连接地址，用户名密码即可使用，这些数据库都支持 Spring Data JPA 管理。

使用不同数据库只需更改application.yml中的
```yaml
spring:
  profiles:
    active: h2
```
即可。

```yaml

#h2 database config
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:./h2/seed;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: root
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

```

```yaml

#mysql database config
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
#    ip 可修改
    url: jdbc:mysql://localhost:3306/seed
    username: root
    password: 123456
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

```

```yaml

#oracle database config
spring:
  datasource:
    driver-class-name: oracle.jdbc.OracleDriver
#    ip可以修改
    url: jdbc:oracle:thin:@localhost:1521:xe
    username: system
    password: oracle
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

```

```yaml

#h2 database config
spring:
  datasource:
    driver-class-name: org.sqlite.JDBC
    url: jdbc:sqlite:./sqlite/seed.db
    username:
    password:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

```

## 基于 slf4j/logback 的日志处理

有关于 RequestMapping 的日志切面，可记录当前调用函数起止时间。

## 基于 @Scheduled 的轮询处理

```Java

@Component
public class Tasks {
    private static final Logger LOGGER = LoggerFactory.getLogger(Tasks.class);

    /**
     * 计划任务，每隔5分钟更新一次数据库VM的运行状态
     */
    @Scheduled(fixedRate = ConfigConst.VM_STATUS_UPDATE_THRESHOLD)
    public void scheduled() {
        LOGGER.info("5 分钟一次轮询！");
    }
}

```

## 基于 @Async 的异步任务处理

在普通方法上添加@Async，该方法将变成异步方法，可与 websocket 结合，实现消息推送。

## 封装好的 RestApiVersion 处理 api 版本

```Java

@RestController
@RequestMapping("/{version}/employee")
@CrossOrigin
public class EmployeeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);

    @Resource
    private
    EmployeeService service;

    @GetMapping("/detail/{name}")
    @RestApiVersion(1)
    public StandardResponseDTO getEmployeeByName(@PathVariable String name) {
        return HttpResponseUtil.success(service.getEmployeeByName(name));
    }

    @GetMapping
    @RestApiVersion(1)
    public StandardResponseDTO listEmployee() {
        return HttpResponseUtil.success(service.listEmployee());
    }

    @GetMapping("/count")
    @RestApiVersion(1)
    public StandardResponseDTO countEmployee() {
        return HttpResponseUtil.success(service.countEmployee());
    }

    @PostMapping
    @RestApiVersion(1)
    public StandardResponseDTO saveEmployee(@RequestBody EmployeeDO employee) {
        LOGGER.info("{}", employee);
        return HttpResponseUtil.success(service.saveEmployee(employee));
    }

    @DeleteMapping("/{name}")
    @RestApiVersion(1)
    public StandardResponseDTO removeEmployee(@PathVariable String name) {
        service.removeEmployee(name);
        return HttpResponseUtil.success();
    }

    @PutMapping("/{name}")
    @RestApiVersion(1)
    public StandardResponseDTO updateEmployee(@PathVariable String name, @RequestBody EmployeeDO employee) {
        LOGGER.info("{}", employee);
        return HttpResponseUtil.success(service.updateEmployee(name, employee));
    }
}

```

## 总结

该项目抽取了几个基于 springboot 开发的项目的一些公共代码，只是一个项目框架。这个项目的特性多是 spring 及 hibernate 的特性。与 spring 耦合度很高，springboot 2.0.0 宣称在性能上相比 springboot 1.5.x 有很大提升，后续我会将该种子项目的 springboot 版本更新到 2.0.0。

希望它对你有所帮助。
