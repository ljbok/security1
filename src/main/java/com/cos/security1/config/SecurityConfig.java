package com.cos.security1.config;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity // 활성화를 위해 붙인 어노테이션
                    // 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
                            // secureEnable = true --> 특정 하나의 권한을 가지고 있어야만 접속 가능하게 해주는 어노테이션 @Secure 활성화 설정
                            // prePostEnabled = true --> 지정한 1개 또는 n개의 권한을 가지고 있어야만 접속 가능하게 해주는 어노테이션 @PreAuthorize 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

/*
    // PrincipalOauth2UserService 와의 순환 참조 문제로 BcryptConfig.java 파일로 옮기고
    // 주석으로 비활성화
    // 해당 메서드의 리턴되는 오브젝트 IOC를 등록해준다.
    @Bean
    public BCryptPasswordEncoder encodePwd(){
        return new BCryptPasswordEncoder();
    }
*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated() // 인증된 사람만 접속 가능하게
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')") // 로그인은 했지만 권한이 있는 사람만 접속 가능하게
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")    // .formLogin(), .loginPage() 인증이 필요하면 무조건 loginForm으로 보내겠다 설정
                .loginProcessingUrl("/login") // login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해준다.
                                              // 이게 무슨 의미냐면 컨트롤러에 login을 만들지 않아도 된다는 의미
                .defaultSuccessUrl("/") // 로그인에 성공하고 나면 이동할 url  컨트롤러에서 index.html로 가게끔 매핑해뒀다.
                .and()
                .oauth2Login()
                .loginPage("/loginForm") //.and, .oauth2Login(), loginPage ==> oauth2 로그인(ex, 구글, 페이스북)
                                            // 등등의 소셜 로그인을 사용하겠다 선언 그리고 진행할 로그인 페이지를 loginForm.html으로 지정한 것
                .userInfoEndpoint()// --> 구글로그인이 완료된 이후의 후처리 과정: 1.코드받기(인증) 2.엑세트토큰, 3.사용자프로필 정보를 가저오고
                .userService(principalOauth2UserService);//-> 매개변수에 oausth2userservice 타입이 들어가야 함
                            // 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함
                            // 4-2. (이메일, 전화번호, 이름, 아이디) 만 넘어오기 때문에 정보가 모자라다.
                            // ex) 쇼핑몰의 경우 집주소, 등급 등등
                            // 추가적인 정보가 요구되는 경우 추가적인 창을 띄워서 데이터를 받아야 한다.
                            // but 추가적인 정보가 필요없다면 구글이 주는 기본적인 정보로만 회원가입을 진행시켜도 된다.
                            // tip. 구글 로그인이 되면 코드를 돌려받는 게 아니라 엑세스토큰 + 사용자 프로필을 넘겨받는다.

    }
}
