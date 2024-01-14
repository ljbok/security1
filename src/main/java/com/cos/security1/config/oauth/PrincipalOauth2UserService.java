package com.cos.security1.config.oauth;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

// 구글 로그인 후 자동으로 db에 회원가입 되게 만들 로직을 서술한 클래스
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    // 함수 종료 시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    // 구글 로그인 후 후처리 과정 (후처리되는 함수)
    // 구글로 받은 userRequest 데이터에 대한 후처리를 정의할 수 있다.
    // sysout 으로 찍어본 userRequest 데이터는
    // userRequest:org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest@55d29d9f 이런 꼴
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        System.out.println("userRequest:" + userRequest.getClientRegistration()); // registrationId로 어떤 Oauth로 로그인 했는지 확인 가능
        System.out.println("userRequest:" + userRequest.getAccessToken().getTokenValue());

        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 구글로그인 버튼 클릭 -> 구글로그인창 -> 로그인완료 -> code를 리턴(OAuth-Client 라이브러리) -> Access Token을 요청
            // └→ 여기까지가 userRequest 정보 -> 회원프로필 받아양함(loadUser함수로) -> 구글로부터 회원프로필 받아준다.
        //System.out.println("userRequest:" + super.loadUser(userRequest).getAttributes());
        // OAuth2User === DefaultOAuth2UserService.loadUser(userRequest) 이므로
        System.out.println("userRequest:" + oAuth2User.getAttributes());

        // 구글로부터 넘겨받은 회원의 이메일과 프로필 정보로
        // 강제 회원가입을 진행하는 곳 (무작정 시켜주면 안 되고 가입이 안 되어있는 경우에만 시켜야 됨)

        String provider = userRequest.getClientRegistration().getRegistrationId(); // "google"
        String providerId = oAuth2User.getAttribute("sub"); // google의 primary id
        String username = provider + "_" + providerId; // "google_10561560465546 이런 꼴" --> username 충돌 날 일 없음
        String password = bCryptPasswordEncoder.encode("ljb"); // 별의미는 없지만 password를 비워둘 수 없으니
                                                               // 나만 알아볼 수 있는 문자로 암호화해서 넣어주자
        String email =  oAuth2User.getAttribute("email");
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            // 구글로그인을 했는데 그 사용자가 회원가입된 사용자가 아닌 경우
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }

        // userEntity 가 null 이 아니면 --> 즉 이미 회원가입된 사람이라면
        // 이렇게 넣으면 PrincipalDetails에서 정의한 생성자에 의하여
        // oAuth2User의 어트리뷰트가 user타입으로 변함
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
        
        //PrincipalDetailsService와 동일하게
        // PrincipalDetails(userEntity, Map<String,Objevt> attributes) 이걸 반환하는데 이렇게 되면
        // 시큐리티 sesstion에 Authentication(UserDetails)가 자동으로 들어가게 된다.
        // Session(Authentication(User)) 꼴 완성

        // 시큐리티 세션에 자동 등록된다!
    }
}
