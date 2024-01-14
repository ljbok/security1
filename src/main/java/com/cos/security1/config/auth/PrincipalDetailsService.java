package com.cos.security1.config.auth;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티 session <= 『 Authentication(UserDetails) 』 : UserDetails 타입을 Authentication 에 넣어주는 과정

// 이 서비스가 발동하는 시점
// 시큐리티 설정에서 loginProcessUrl(로그인 성공 후 이동할 url)로 인해 로그인 후 지정해놓은 "/" 요청이 오면
// .. 요청이 오면 ==> 자동으로 UserDetailsService 타입으로 IoC 되어있는 loadByUsername 함수가 실행 <- 그냥 법칙이다.
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // 함수 종료 시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override  // 여기 파라미터로 있는 username은 로그인 form input type="text" name="username"의 name과 반드시 동일해야한다.
                // 동일하지 않은 경우 로그인이 정상적으로 이루어지지 않는다.
                // 만약 form에 있는 name을 username2로 바꾸고 싶다면
                // (왕비추) SecurityConfig.java 파일에
                // (왕비추) http.authorizeRequests()부분에 .usernameParameter("username2") 라고 추가해주어야 한다. 그래야 발동한다.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username); // 이 함수를 사용하려면 UserRepository에 함수를 추가해야 한다.
        if (userEntity != null) { // 위를 통했는데 만약 username으로 조회된 유저가 있다면
                                // PrincipalDetails(userEntity) 이걸 반환하는데 이렇게 되면
                                // 시큐리티 sesstion에 Authentication(UserDetails)가 자동으로 들어가게 된다.
                                // Session(Authentication(User)) 꼴 완성
            return new PrincipalDetails(userEntity);
        }

        // 만약 찾았다면 --> 새로운 리턴
        // 시큐리티 session에 <= Authentication가 담기고 이 Authentication 객체에  <= UserDetails 타입 객체가 담긴다고 설명했었다.
        return null;
    }
}
