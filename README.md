Servlet Http Log
===============================================

给使用servlet的应用打印请求日志

**暂时不支持异步**

# 如何使用?

- Maven:
  ```xml
  <dependency>
      <groupId>com.manymobi</groupId>
      <artifactId>servlet-http-log-spring-boot-starter</artifactId>
      <version>1.0.0</version>
  </dependency>
  ```
- Gradle
  ```groovy
  implementation 'com.manymobi:servlet-http-log-spring-boot-starter:1.0.0'
  ```

# 配置解释

```yaml
servlet:
  http:
    log:
      enabled: true
      default-strategy: # 默认日志打印策略
        output-log: true
        http-methods: ALL
      path-strategy:
        "[/api/**]":
          # 路径匹配支持通配符, 路径会根据 "/" 切割
          # "*" 或 {id}: 匹配一个
          # "**": 匹配多个
          # 根据这个匹配路径,匹配的路径,当为null或空数组的时候,
          # 将使用所在map的key作为匹配
          path: [ "/api/**","/a/**" ]
          # 是否输出日志
          output-log: true
          # 请求方式 null将代表所有方法
          httpMethods:
            - GET
          # 请求body是否输出日志
          requestBody: true
          # 请求需要输出日志的内容格式
          # 只支持完全相同匹配. 只比较";"前面部分
          requestContentType:
            - application/json
          # 请求内容默认缓存长度
          # 0:使用默认的
          # -1: 根据 contentLength 初始化变量. 当 contentLength 大于 requestBodyMaxSize时候,将使用 requestBodyMaxSize 进行初始化
          requestBodyInitialSize: 0
          # 请求内容最大缓存长度
          # 0:使用默认的
          # -1: 不限制
          requestBodyMaxSize: 8192
          # 响应body是否输出日志
          responseBody: true
          # 打印响应的格式
          # 只支持完全相同匹配. 只比较";"前面部分
          responseContentType:
            - application/json
          # 响应内容默认缓存长度
          # 0:使用默认的
          responseBodyInitialSize: 1024
          # 响应内容最大默认缓存长度
          # 0:使用默认的
          # -1: 不限制
          responseBodyMaxLength: 8192
          # 自定义
          custom:
            "key": "value"
        "[/actuator/**]":
          output-log: false
```

# 当使用 logstash-logback-encoder 时候可以使用下列扩展将提供独立字段
- Maven:
  ```xml
  <dependency>
      <groupId>com.manymobi</groupId>
      <artifactId>servlet-http-log-logstash-spring-boot-starter</artifactId>
      <version>1.0.0</version>
  </dependency>
  ```
- Gradle
  ```groovy
  implementation 'com.manymobi:servlet-http-log-logstash-spring-boot-starter:1.0.0'
  ```