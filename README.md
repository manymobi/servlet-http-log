Servlet Http Log
===============================================

给使用servlet的应用打印请求日志

**暂时不支持异步**

引入框架后将输出请求日志. request body只有系统读取了body才会打印. 例如: 未使用 @RequestBody 的时候可能不会输出日志
```log
2022-05-29 23:24:40.025  INFO 431055 --- [nio-8080-exec-6] com.manymobi.servlet.http.log.LogFilter  : request POST http://127.0.0.1:8080/d {method=POST, ip=127.0.0.1, parameter=a=b, header={content-length=15, postman-token=489cf0b9-329c-48fc-913f-e4d6b69fb3f1, host=127.0.0.1:8080, connection=keep-alive, content-type=application/json, accept-encoding=gzip, deflate, br, accept=*/*, user-agent=PostmanRuntime/7.29.0}, uri=/d, url=http://127.0.0.1:8080/d}
2022-05-29 23:24:40.026  INFO 431055 --- [nio-8080-exec-6] com.manymobi.servlet.http.log.LogFilter  : request body={"test":"test"}
2022-05-29 23:24:40.027  INFO 431055 --- [nio-8080-exec-6] com.manymobi.servlet.http.log.LogFilter  : response status=200 time=2ms body={"key":"value"} header={Keep-Alive=timeout=60, Transfer-Encoding=chunked, Connection=keep-alive, Date=Sun, 29 May 2022 15:24:40 GMT, Content-Type=application/json}
````

# 如何使用?

- Maven:
  ```xml
  <dependency>
      <groupId>com.manymobi</groupId>
      <artifactId>servlet-http-log-spring-boot-starter</artifactId>
      <version>1.1.0</version>
  </dependency>
  ```
- Gradle
  ```groovy
  implementation 'com.manymobi:servlet-http-log-spring-boot-starter:1.1.0'
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
          responseBodyMaxSize: 8192
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
      <version>1.1.0</version>
  </dependency>
  ```
- Gradle
  ```groovy
  implementation 'com.manymobi:servlet-http-log-logstash-spring-boot-starter:1.1.0'
  ```