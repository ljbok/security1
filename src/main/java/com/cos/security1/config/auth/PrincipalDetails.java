package com.cos.security1.config.auth;

import com.cos.security1.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// 시큐리티가 /login을 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
// 이때 로그인 진행이 완료가 되면 session을 만들어줍니다. 일반적인 session과 유사한데
// 시큐리티가 가지고 있는 session이 있다. (시큐리티가 자신만의 세션 공간을 가지고 있다.)
// Security ContextHolder 키 값에다가 session 정보를 저장한다.
// 시큐리티 세션에 들어갈 수 있는 object가 정해져있다. => Authentication 타입의 객체
// Authentication 타입의 객체 안에 User 정보가 있어야 된다.
// User오브젝트 타입이 => UserDetails 타입 객체여야한다.


// 정리하자면 ▼
// Security Session => Authentication 객체가 들어간다 => 이 그리고 이 Authentication 객체에
// 유저 정보를 전달할 때 유저정보가 UserDetails 타입이어야 한다.
// 여기서 PrincipalDetails가 UserDetails를 상속받았기 때문에 UserDetails === PrincipalDetails 라고 보면 된다.
// 이제 아래서 만든 PrincipalDetails 객체를 Authentication 객체 안에 넣을 수 있다.

// 오버라이드 하자!
public class PrincipalDetails implements UserDetails {


    private User user; // 내가 만들었던 user 객체 => 콤포지션

    public PrincipalDetails(User user) {
        this.user = user;
    }

    // 해당 유저의 권한을 리턴하는 곳
    @Override // return 타입이  Collection<? extends GrantedAuthority> 가 필요함.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 그냥 user.getRole() 은 string 타입
        Collection<GrantedAuthority> collect = new ArrayList<>(); // 우선 리턴할 타입을 맞추기 위한 객체를 생성해주자
        collect.add(new GrantedAuthority() {

            // new GrantAuthority 타입을 리턴하겠다고 코드를 자동생성하면 생성되는 함수 정의 부분인데
            // 여기에서는 String을 return 받을 수 있다.
            @Override
            public String getAuthority() {
                return user.getRole(); // 이제 드디어 user.getRole() 타입을 넘겨줄 수 있다.
            }

        }); //.add() 안에 들어가야 하는 매개변수의 타입은 granted authority 타입

        return collect;
    }

    // 패스워드를 리턴
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 유저네임 리턴
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 이 계정 만료 안 됐지?
    @Override
    public boolean isAccountNonExpired() {
        return true; // 응 만료 안 됐어
    }

    // 이 계정 안 잠겼지?
    @Override
    public boolean isAccountNonLocked() {
        return true; // 응 안 잠겼어
    }

    // 이 계정이 뭐 일년이 안 지났지?
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 응 안 지났어
    }

    // 이 계정이 활성화 되어있니?
    @Override
    public boolean isEnabled() {

        // 여기 응용법 [참고만 실제 코드 작업은 안 했음]
        // 우리 사이트에서 1년 동안 회원이 로그인을 안 하면 휴먼 계정으로 하기로 했다면
        // User 객체에 private Timestamp loginDate; 라는게 있어야 겠죠?
        // 로그인할 때 날짜를 넣어놓고
        // 여기서 user.getLoginDate; 해서 받아와서
        // 현재시간 - 로그인시간 해서 => 1년을 초과하면 return false;로 하면 된다.

        return true; // 응 활성화 되어있어
    }
}
