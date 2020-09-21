# spring-boot-starter-auth
easy auth for springboot

## 集成
### 引入依赖
```xml
<dependency>
    <groupId>com.seepine</groupId>
    <artifactId>spring-boot-starter-auth</artifactId>
    <version>1.0.0</version>
</dependency>
```
### 注解使用
- 依赖引入后，所有接口都需要带上正确token才能被访问，若没有token将会返回`invalid token`，
若token错误将会返回`not auth`
- 若需要让接口对外暴露，只需要在接口或类上加`@Expose`注解
- 若在某个类上加了`@Expose`注解，但想让该类中某个方法不暴露，可在该方法上加`@NotExpose`注解

## 使用
### 获取token
在登录接口使用注解`@Login`，获取到用户信息后调用AuthUtil.loginSuccess，该方法将会返回用户token
并且可传入不同用户信息，比如User，比如UserVo
```java
@Login
@GetMapping("/login/{username}/{password}")
public R login(@PathVariable String username,@PathVariable String password) {
    User user = userService.getByUsername(username,password);
    return R.ok(AuthUtil.loginSuccess(user));
}

@Login
@GetMapping("/login/{code}")
public R login(@PathVariable String code) {
    UserVO user = userService.getByCode(code);
    return R.ok(AuthUtil.loginSuccess(user));
}
```

### 请求接口
请求接口时，请求头中加上{'token':'xxxxxxxxxxxxxxxxxx'}`其中token后的字符串由登录接口获得`，即可在方法中通过AuthUtil.getUser()
获取到当前登录者的用户信息
```
@GetMapping("/info")
public R info() {
    Object obj = AuthUtil.getUser();
    User user = AuthUtil.<User>getUser();
    UserVO userVO = AuthUtil.<UserVO>getUser();
    return R.ok(user);
}
```

## 自定义配置
其中可自定义三个配置
- header: 请求头参数，如{"custom_token":"asfoav5h35v43692"}
- cacheKey: 缓存redis的key
- timeout: 登录有效期
```java
@Configuration
@AutoConfigureBefore(AuthAutoConfigurer.class)
public class MyAuthConfig {

    @Bean
    public AuthProperties authProperties() {
        return new AuthProperties().header("custom_token")
                .cacheKey("custom_auth:").timeout(3, TimeUnit.DAYS);
    }
}
```