package com.cos.security1.controller;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;


@Controller // view 를 리턴하겠다.
public class IndexController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // # 일반로그인 시 시큐리티 세션에 있는 회원 정보 가지고 오는 방법
    @GetMapping("/test/login")
    public @ResponseBody String loginTest(
            Authentication authentication,
            @AuthenticationPrincipal PrincipalDetails userDetails) { // @AuthenticationPrincipal 어노테이션을 통해 세션정보에 접근할 수 있다.
        System.out.println("/test/login================");
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("auntentication:" + principalDetails.getUser());

        System.out.println("userDetails : " + userDetails.getUser().getUsername());
        return "세션 정보 확인하기";
    }

/*
    // [응용 전 어노테이션으로 캐스팅 하지 않고 직접 캐스팅 하는 버전]
    // # 구글로그인 시 시큐리티 세션에 있는 회원 정보 가지고 오는 방법
    // 구글로그인시 PrincipalOauth2UserServiet 의 loadUser(userRequest) 함수 테스트를 위한 매핑
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(
            Authentication authentication) {
        System.out.println("/test/oauth/login================");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication:" + oAuth2User.getAttributes());

        return "OAuth 세션 정보 확인하기";
    }
*/
    // [응용 후  어노테이션으로 캐스팅 하는 버전]
    // # 구글로그인 시 시큐리티 세션에 있는 회원 정보 가지고 오는 방법
    // 구글로그인시 PrincipalOauth2UserServiet 의 loadUser(userRequest) 함수 테스트를 위한 매핑
    @GetMapping("/test/oauth/login")
    public @ResponseBody String testOAuthLogin(
            Authentication authentication,                      // - 1
            @AuthenticationPrincipal OAuth2User oAuth) {        // - 2   // 1을 쓰든 2를 쓰든 둘 중에 하나로 세션-어센틱케이션-details의 attributes에 접근하면 된다.
        System.out.println("/test/oauth/login================"); // 맨 마지막에 작성했던 /user 매핑 때는 PrincipalDetails가 userDetails 뿐만 아니라
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();             // OAut2User로 상속 받기 시작하면서 어노테이션 뒤에 PrincipalDetails 적은 것!
        System.out.println("authentication:" + oAuth2User.getAttributes());
        System.out.println("oauth:" + oAuth.getAttributes());

        // 두개의 출력 결과가 같다 즉,
        // @AuthenticationPrincipal OAuth2User oAuth 와
        // OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // 두 개로 생성되는 객체가 동일한 타입의 객체라는 것
        // 단 캐스팅을 어노테이션으로 진행하여 생성한 것이라는 것!
        return "OAuth 세션 정보 확인하기";
    }

    // localhost:8090/
    // localhost:8090
    @GetMapping({"","/"})
    public String index() {
        // 머스테치 기본폴더 src/main/resources/
        // 뷰리졸버 설정 : template (prefix), .mustache (suffix) 로 잡으면 세팅이 끝난다.
        return "index";
    }

    @GetMapping("/user")    // 이제 UserDetails와 OAuth2User가 모두 PrincipalDetails에 들어있기 때문에
                            // 세션의 authentication에 있는 principalDetails 에 접근해서 데이터를 가지고 오면된다.
    public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails: " + principalDetails.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    // 스프링 시큐리티가  해당주소를 낚아채간다. --> SecurityConfig 파일 생성 후 작동 안 함
    @GetMapping("/loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        //System.out.println(user); --> 잘 받아오는 거 확인 완료
        user.setRole("ROLE_USER"); // --> 자동생성되지 않는 컬럼이기 떄문에
                                    // 여기서 임의로 작성해주자!
        userRepository.save(user); // 회원가입 잘됨. 비밀번호: 1234 => 시큐리티로 로그인을 할 수 없음.
                                    // 이유는 패스워드가 암호화가 안 되었기 때문
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);
        userRepository.save(user);
        return "redirect:/loginForm";
    }

    @Secured("ROLE_ADMIN") // --> ROLE_ADMIN 권한이 있는 사람만 매핑 가능하게 설정
                            // 상당히 편하다.
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // --> ROLE_ADMIN 권한이 있는 사람만 매핑 가능하게 설정
    // 상당히 편하다.
    @GetMapping("/data")
    public @ResponseBody String data(){
        return "데이터정보";
    }
}
