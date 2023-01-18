## 做这个种子的心路历程

最近在做一个大型的J2EE项目，后端语言选择了Java，理所当然的选择了SpringBoot，使用SpringBoot来做restful风格的api开发很是方便，Spring下面有很多子项目通过Springboot集成也很舒服。程序员都知道沟通很重要，实际项目中，往往是各自为战，尽管使用的是相同的框架、工具，编写的代码却千差万别，为了统一基础代码风格，编写了这个项目种子。

除此之外，在开发一个Web后端api项目时，通常都会经历搭建项目、选择依赖管理工具、引入基础包依赖、配置框架等，为了加快项目的开发进度（早点下班）还需要封装一些常用的类和工具，如标准的响应结构体封装、统一异常处理切面、接口签名认证、初始化运行方法、轮询方法、api版本控制封装、异步方法配置等。

每次开始一个类型的新项目，以上这些步骤又要重复一遍，虽然能够将老项目拿过来删删减减达到目的，但还是很费时费力，还容易出问题。所以，可以利用面向对象的思想，抽取这类Web后端api项目的共同之处封装成一个项目种子。以后再开发类似的项目，就能直接在这个项目种子上迭代，减少重复劳动。

如果你有类似的需求，可以克隆下来试试。欢迎star或fork，如果在使用中发现问题或者有什么建议欢迎提 issue 或 pr 一起完善。

## 使用方法

1. 克隆本项目到本地
2. 使用IDEA打开，选择pom.xml文件，使用maven构建本项目
3. 下载项目需要的依赖包
4. 修改[application-dev.yml](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/resources/application-dev.yml)中的pgsql、kafka配置
5. 运行[Application.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/Application.java)Application.java中的main函数
6. 访问 http://localhost:8080

## 环境依赖

- jdk: openjdk17
- kafka: 2.12-2.5.1
- pgsql: 15

## 特征

- 支持包管理工具maven和gradle
- springboot版本为3.0.1
- 统一HTTP Response响应JSON结构封装
- 基于 @ControllerAdvice 的AOP异常拦截处理
- 基于 ApplicationRunner 的初始化
- 基于 HandlerInterceptor 的Mvc拦截器配置
- PostgreSQL关系型数据库支持
- 基于 slf4j/logback 的日志切面
- 基于 @Scheduled 的定时任务
- 基于 @Async 的异步任务处理
- 文件分片上传下载示例
- websocket消息推送示例
- 多数据源示例
- docker构建脚本示例

## 统一HTTP Response响应JSON结构封装
- [BaseErrorCode.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/framework/common/exception/BaseErrorCode.java)
- [Res.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/framework/common/base/Res.java)
- [ResultHandler.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/sps/common/component/aop/ResultHandler.java)

基于@RestControllerAdvice的返回值拦截封装。

返回参数示例
```json
{
  "code": "0000000000",
  "msg": "操作成功",
  "data": {
    
  }
}
```

```Java
/**
 * 返回结果类
 */
@Data
public final class Res<T> {
  private static final String SUCCESS_CODE = "0000000000";
  private String code = SUCCESS_CODE;

  private String msg = "请求成功";

  private T data;


  private Res() {
  }

  private Res(T data) {
    this.data = data;
  }

  private Res(String code, String msg) {
    this(code, msg, null);
  }

  private Res(String code, String msg, T data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
  }

  public static Res<Object> success() {
    return new Res<>();
  }

  public static <T> Res<T> success(T data) {
    return new Res<>(data);
  }

  public static <T> Res<T> success(T data, String msg) {
    return new Res<>(SUCCESS_CODE, msg, data);
  }

  public static Res<Object> error(String code, String msg) {
    return new Res<>(code, msg);
  }

  public static Res<Object> error(IErrorCode error) {
    return new Res<>(error.getCode(), error.getMsg());
  }

  public static <T> Res<T> of(String code, String msg, T data) {
    return new Res<>(code, msg, data);
  }

  public static Res<Object> of(BaseErrorCode baseErrorCode) {
    return new Res<>(baseErrorCode.getCode(), baseErrorCode.getMsg());
  }

  @JsonIgnore
  public boolean isSuccess() {
    return SUCCESS_CODE.equals(code);
  }
}
```

## 基于 @ControllerAdvice 的AOP异常拦截处理
- [GlobalExceptionHandler.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/framework/common/exception/GlobalExceptionsHandler.java)
- [CustomExceptionsHandler.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/sps/common/exception/CustomExceptionsHandler.java)

可以参考CustomExceptionsHandler.java的异常捕获实现，将自定义异常拦截添加到CustomExceptionsHandler.java末尾。

```Java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler implements ThrowsAdvice {

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseBody
  public ResponseEntity<Object> constraintViolationException(ConstraintViolationException e) {
    log.error("ConstraintViolationException Params valid exception={}\n{}", SessionHelper.getRequest().getRequestURI(), e.getMessage());
    final Res<Object> res = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), e.getMessage());
    return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
  }

  /**
   * 参数校验异常
   *
   * @param e
   * @return
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  public ResponseEntity<Object> methodArgumentNotValidException(MethodArgumentNotValidException e) {
    log.error("Params valid exception={}\n{}", SessionHelper.getRequest().getRequestURI(), e.getMessage());
    return bindingResult(e.getBindingResult());
  }

  @ExceptionHandler(BindException.class)
  @ResponseBody
  public ResponseEntity<Object> bindException(BindException e) {
    log.error("Params valid exception={}\n{}", SessionHelper.getRequest().getRequestURI(), e.getMessage());
    return bindingResult(e.getBindingResult());
  }

  private ResponseEntity<Object> bindingResult(BindingResult bindingResult) {
    final String notEmpty = "不能为空";
    List<FieldError> errors = bindingResult.getFieldErrors();
    StringBuilder messageBuilder = new StringBuilder();
    String message;
    for (int i = 0; i < errors.size(); i++) {
      FieldError error = errors.get(i);
      message = Strings.isNotBlank(error.getDefaultMessage()) ? error.getDefaultMessage() : notEmpty;
      if (notEmpty.equals(message)) {
        messageBuilder.append(error.getField());
      }
      messageBuilder.append(message);
      if (i < errors.size() - 1) {
        messageBuilder.append(";");
      }
    }
    final Res<Object> res = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), messageBuilder.toString());
    return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
  }

  /**
   * 请求方法不支持
   *
   * @param e
   * @return
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  @ResponseBody
  public ResponseEntity<Object> methodNotSupportHandle(HttpRequestMethodNotSupportedException e) {
    log.error("HttpRequestMethodNotSupportedException exception={}", e.getMessage(), e);
    final Res<Object> res = Res.error(BaseErrorCode.HTTP_REQUEST_METHOD_NOT_SUPPORTED_ERROR.getCode(), e.getMessage());
    return new ResponseEntity<>(res, HttpStatus.METHOD_NOT_ALLOWED);
  }

  /**
   * 参数校验异常
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<Object> missingServletRequestParameterException(MissingServletRequestParameterException e) {
    Res<Object> result = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), BaseErrorCode.INVALID_PARAM_ERROR.getMsg() + e.getMessage());
    log.error("Params valid exception={}", e.getMessage(), e);
    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<Object> maxUploadSizeExceededException(MaxUploadSizeExceededException e) {

    if (e.getCause().getCause() instanceof SizeLimitExceededException) {
      final SizeLimitExceededException slee = (SizeLimitExceededException) e.getCause().getCause();

      final String message = BaseErrorCode.FILE_SIZE_ERROR.getMsg() + "限制大小："
        + slee.getPermittedSize() / 1024 / 1024 + "MB，" + "实际大小：" + slee.getActualSize() / 1024 / 1024
        + "MB";
      Res<Object> result = Res.error(BaseErrorCode.FILE_SIZE_ERROR.getCode(), message);
      log.error("file size exceeded exception={}", e.getMessage(), e);
      return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    } else {
      Res<Object> result = Res.error(BaseErrorCode.FILE_SIZE_ERROR.getCode(), BaseErrorCode.FILE_SIZE_ERROR.getMsg() + e.getMessage());
      log.error("file size exceeded exception={}", e.getMessage(), e);
      return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
  }

  @ExceptionHandler(TypeMismatchException.class)
  @ResponseBody
  public ResponseEntity<Object> typeMismatchHandle(TypeMismatchException e) {
    log.error("param type mismatch exception={}", e.getMessage(), e);
    final Res<Object> res = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), BaseErrorCode.INVALID_PARAM_ERROR.getMsg() + e.getPropertyName() + "类型应该为" + e.getRequiredType());
    return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({HttpMessageNotReadableException.class})
  public ResponseEntity<Object> httpMessageNotReadableHandle(HttpMessageNotReadableException e) {
    if (e.getCause() instanceof InvalidFormatException) {
      InvalidFormatException ife = (InvalidFormatException) e.getCause();
      Joiner joiner = Joiner.on(" ").skipNulls();
      String message = BaseErrorCode.INVALID_PARAM_ERROR.getMsg();
      if (null != ife) {
        message = joiner.join(message, "字段：", ife.getValue(), "正确类型:", ife.getTargetType());
      }

      Res<Object> result = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), message);
      log.error("param type mismatch exception={}", e.getMessage(), e);
      return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    } else {
      Res<Object> result = Res.error(BaseErrorCode.INVALID_PARAM_ERROR.getCode(), BaseErrorCode.INVALID_PARAM_ERROR.getMsg() + e.getMessage());
      log.error("param type mismatch exception={}", e.getMessage(), e);
      return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseBody
  public ResponseEntity<Object> noHandlerFoundException(NoHandlerFoundException e) {
    log.error("api not exist exception={}", e.getMessage(), e);
    final Res<Object> res = Res.error(BaseErrorCode.API_NOT_EXIST_ERROR.getCode(), BaseErrorCode.API_NOT_EXIST_ERROR.getMsg() + " 请求地址：" + e.getRequestURL());
    return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MultipartException.class)
  public ResponseEntity<Object> multipartException(MultipartException e) {
    Res<Object> result = Res.error(BaseErrorCode.HTTP_REQUEST_FAILED.getCode(), BaseErrorCode.HTTP_REQUEST_FAILED.getMsg() + e.getMessage());
    log.error("upload file or form data exception={}", e.getMessage(), e);
    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(FeignException.class)
  @ResponseBody
  public ResponseEntity<Object> feignException(FeignException e) {
    final String content = e.contentUTF8();
    Object data = null;
    Res<Object> res = null;
    if (!StringUtils.isEmpty(content)) {
      res = JSON.toBean(content, new TypeReference<Res<Object>>() {
      });
      if (res.isSuccess()) {
        data = res.getData();
      }
    }
    if (res == null || res.isSuccess()) {
      res = Res.error(BaseErrorCode.CALL_FAILED.of());
      res.setData(data);
    }
    log.error("Remote call exception={}\n{}", e.request().url(), res);
    return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * SQL 异常
   */
  @ExceptionHandler(SQLException.class)
  @ResponseBody
  public ResponseEntity<Object> SQLException(SQLException e) {
    log.error("sql  exception={}", e.getMessage(), e);
    final Res<Object> res = Res.of(BaseErrorCode.SQL_EXCEPTION);
    return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * 业务异常
   */
  @ExceptionHandler(BaseException.class)
  @ResponseBody
  public ResponseEntity<Object> baseException(BaseException e) {
    log.error("internal server exception={}", e.getMessage(), e);
    final Res<Object> res = Res.error(e.getCode(), e.getMessage());
    return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * 全局异常
   */
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ResponseEntity<Object> globalHandle(Exception e) {
    log.error("exception={}", e.getMessage(), e);
    final Res<Object> res = Res.of(BaseErrorCode.SYS_INTERNAL_ERROR);
    return new ResponseEntity<>(res, HttpStatus.INTERNAL_SERVER_ERROR);
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
  public void run(ApplicationArguments args) throws Exception {
    service.init();
  }
}

```

## 基于 HandlerInterceptor 的Mvc拦截器配置

preHandle 函数中返回 true 表示验证通过，请求会向下传递，返回false请求会被打回，处理header中的用户信息。

Header用户信息示例：
```text
Header: username:admin&usercode:admin
```

```Java

public class BaseHeaderInterceptor extends HandlerInterceptorAdapter {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String user = request.getHeader(GlobalConstant.USER);
    if (StringUtils.hasText(user)) {
      ThreadLocalUtils.put(GlobalConstant.USER, user);
      //user 解析
      handleUserInfo(user);
    }
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    super.postHandle(request, response, handler, modelAndView);
    ThreadLocalUtils.remove(GlobalConstant.USER);
    UserHandler.remove();
  }

  private void handleUserInfo(String authorization) throws Exception {
    // 编码转换
//        authorization = EscapeUtils.unescape(EscapeUtils.unescape(authorization));
    authorization = URLDecoder.decode(authorization, "UTF-8");

    String[] array = authorization.split("&");

    User user = UserHandler.getUser();
    if (null == user) {
      user = new User();
    }
    for (String line : array) {
      String[] keyValue = line.split(":");
      if (keyValue.length < 2) {
        continue;
      }
      if ("usercode".equalsIgnoreCase(keyValue[0])) {
        user.setUserCode(keyValue[1]);
      }

      if ("username".equalsIgnoreCase(keyValue[0])) {
        user.setUserName(keyValue[1]);
      }
    }

    //处理其它用户信息
    UserHandler.setUser(user);
  }
}
```

## PostgreSQL关系型数据库支持

支持 PostgreSQL、MySQL 数据库，相应的模板连接文件已经配置好，修改连接地址，用户名密码即可使用，这些数据库都支持 Mybatis 管理。

使用不同数据库只需更改application.yml中的

```yaml
spring:
  profiles:
    active: dev,h2
```

## 基于 slf4j/logback 的日志切面
- [RequestAspect.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/sps/common/component/aop/ResultAspect.java)

有关于 RequestMapping 的日志切面，可记录当前调用函数起止时间。

```java
/**
 * 异常拦截切面
 */
@Aspect // 声明切面
@Component // 让此切面成为Spring容器管理的bean
@Slf4j
public class RequestAspect {

    public static final String GET = "@annotation(org.springframework.web.bind.annotation.GetMapping)";
    public static final String POST = "||@annotation(org.springframework.web.bind.annotation.PostMapping)";
    public static final String PUT = "||@annotation(org.springframework.web.bind.annotation.PutMapping)";
    public static final String PATCH = "||@annotation(org.springframework.web.bind.annotation.PatchMapping)";
    public static final String DELETE = "||@annotation(org.springframework.web.bind.annotation.DeleteMapping)";
    public static final String REQUEST = "||@annotation(org.springframework.web.bind.annotation.RequestMapping)";
    @Value("${request.aspect.excluded.urls:${springdoc.swagger-ui.path},${springdoc.api-docs.path}/**,/${api-prefix}/files/**}")
    private List<String> excludedUrls;

    @Pointcut(GET + POST + PUT + PATCH + DELETE + REQUEST) // 声明切点
    private void request() {
    }

    /**
     * 核心业务逻辑调用异常退出后，执行此advice，处理错误信息。
     *
     * @param proceedingJoinPoint 代理对象
     */
    @Around("request()") // 声明一个建言，传入定义的切点
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjectUtils.isEmpty(attributes)) {
            return proceedingJoinPoint.proceed();
        }
        HttpServletRequest request = attributes.getRequest();

        String requestURI = request.getRequestURI();

        boolean excluded = CollectionUtils.isEmpty(excludedUrls) || excludedUrls.stream().anyMatch(pattern -> new AntPathMatcher().match(pattern, requestURI));

        if (!excluded) {
            log.info("REQUEST {} : {}", requestURI, JSON.toString(proceedingJoinPoint.getArgs()));
        }
        try {
            Object proceed = proceedingJoinPoint.proceed();
            if (!excludedUrls.contains(requestURI)) {
                log.info("RESPONSE : {}", proceed);
            }
            return proceed;
        } catch (Throwable e) {
            log.error("REQUEST {} : {}", requestURI, JSON.toString(proceedingJoinPoint.getArgs()), e);
            throw e;
        }
    }

}
```

## 基于 @Scheduled 的定时任务

- [KafkaConsumerRestartTask.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/sps/common/component/scheduler/KafkaConsumerRestartTask.java)
kafka 消费者掉线恢复

```Java
/**
 * kafka 定时检测消费组是否在线，下线的重新拉起
 */
@Component
@EnableConfigurationProperties(KafkaTopicProperties.class)
@AutoConfigureAfter(KafkaInitialConfiguration.class)
@Slf4j
public class KafkaConsumerRestartTask {

  public static final int CONNECTIONS_MAX_IDLE_MS_CONFIG = 10000;
  public static final int REQUEST_TIMEOUT_MS_CONFIG = 5000;

  @Resource
  private KafkaAdmin kafkaAdmin;
  @Resource
  private KafkaTopicProperties topicProperties;
  @Resource
  private KafkaListenerEndpointRegistry endpointRegistry;
  List<String> topics;

  /**
   * 计划任务，每隔5分钟执行一次
   */
  @Scheduled(cron = "${kyyee.config.kafka.container.restart-corn:0 0/5 * * * ?}")
  public void consumerRestart() {
    Instant start = Instant.now();
    doRestart();
    log.info("the task used:{}s", ChronoUnit.SECONDS.between(start, Instant.now()));
  }

  public void doRestart() {
    if (CollectionUtils.isEmpty(this.topics)) {
      this.topics = topicProperties.getTopics().stream().map(KafkaTopicProperties.Topic::getName).collect(Collectors.toList());
      if (CollectionUtils.isEmpty(this.topics)) {
        return;
      }
    }
    // kafka服务端配置信息
    Map<String, Object> properties = new HashMap<>(kafkaAdmin.getConfigurationProperties());
    properties.put(AdminClientConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, CONNECTIONS_MAX_IDLE_MS_CONFIG);
    properties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, REQUEST_TIMEOUT_MS_CONFIG);

    // 创建KafkaAdminClient
    try (AdminClient client = KafkaAdminClient.create(properties)) {

      // 获取在线消费者列表
      List<String> groups = Collections.singletonList(String.valueOf(properties.get("spring.kafka.consumer.group-id")));
      // 获取在线消费者列表订阅的topic集合
      Set<String> assignedTopics = client.describeConsumerGroups(groups).all().get().values()
        .stream().flatMap(consumerGroupDescription -> consumerGroupDescription.members().stream())
        .flatMap(memberDescription -> memberDescription.assignment().topicPartitions().stream().map(TopicPartition::topic))
        .collect(Collectors.toSet());

      //kafka 集群当前的所有topic
      Set<String> allClusterTopics = client.listTopics().names().get();

      // 过滤获得未订阅的topic集合（消费者离线）
      List<String> unassignedTopics = this.topics.stream().filter(e -> !assignedTopics.contains(e) && allClusterTopics.contains(e)).collect(Collectors.toList());

      if (unassignedTopics.isEmpty()) {
        log.info("unassigned topics is empty.");
        return;
      }
      log.info("unassigned topics:{}", unassignedTopics);

      //获取监听了未订阅topic的kafka监听器
      List<MessageListenerContainer> needRestartContainers = new LinkedList<>();
      Collection<MessageListenerContainer> allListenerContainers = endpointRegistry.getAllListenerContainers();
      for (MessageListenerContainer listenerContainer : allListenerContainers) {
        ContainerProperties containerProperties = listenerContainer.getContainerProperties();
        for (String topic : unassignedTopics) {
          boolean topicCheck = Optional.ofNullable(containerProperties.getTopics()).map(Arrays::asList).map(list -> list.contains(topic)).orElse(false);
          boolean topicPatternCheck = Optional.ofNullable(containerProperties.getTopicPattern()).map(pattern -> pattern.matcher(topic).find()).orElse(false);
          if (topicCheck || topicPatternCheck) {
            needRestartContainers.add(listenerContainer);
          }
        }
      }
      if (needRestartContainers.isEmpty()) {
        log.info("need restart containers is empty.");
        return;
      }
      //依次重启kafka监听器
      for (MessageListenerContainer toRestartContainer : needRestartContainers) {
        AbstractMessageListenerContainer container = (AbstractMessageListenerContainer) toRestartContainer;
        log.info("kafka consumer restart, container:{}", container.getContainerProperties());
        container.stop(false);
        container.start();
      }
    } catch (Exception e) {
      log.error("kafka consumer restart failed, message:{}", e.getMessage());
    }
  }

}

```

## 基于 @Async 的异步任务处理

在普通方法上添加@Async，该方法将变成异步方法，可与 websocket 结合，实现消息推送。

## 文件分片上传下载示例
- [FileServiceImpl.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/sps/service/impl/FileServiceImpl.java)
- [file.js](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/resources/static/file.js)
- [file.html](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/resources/static/file.html)

## websocket消息推送示例
- [KafkaMockController.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/sps/controller/mock/KafkaMockController.java)
- [WebsocketMockController.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/sps/controller/mock/WebsocketMockController.java)
- [WebSocketConfiguration.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/sps/configuration/websocket/WebSocketConfiguration.java)
- [WebSocketSender.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/sps/manager/websocket/WebSocketSender.java)
- [websocket.js](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/resources/static/websocket.js)
- [websocket.html](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/resources/static/websocket.html)

## 多数据源示例

- [DataSourcePrimaryConfiguration.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/sps/configuration/datasource/DataSourcePrimaryConfiguration.java)
- [DataSourceSecondaryConfiguration.java](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/src/main/java/com/kyyee/sps/configuration/datasource/DataSourceSecondaryConfiguration.java)

## docker构建脚本示例

- [Dockerfile](https://github.com/kyyee/springboot-project-seed/blob/v2.0.0/docker/Dockerfile)

## 总结

该项目抽取了几个基于 springboot 开发的项目的一些公共代码，只是一个项目框架。这个项目的特性多是 spring 及 mybatis 的特性。与
spring 耦合度很高，springboot 3.0.0 宣称在性能上相比 springboot 2.0.0 有很大提升，后续我会将该种子项目的 springboot 版本更新到
3.0.0。

希望它对你有所帮助。
