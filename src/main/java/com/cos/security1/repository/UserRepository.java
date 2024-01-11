package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 이렇게 JpaRepository를 상송받으면 기본적인 CRUD를 들고 있음.
// @Repository라는 어노테이션이 없어도 IOC가 된다 --> JpaRepository를 상속했기 떄문에..
public interface UserRepository extends JpaRepository<User, Integer> {
    // findBy 규칙 -> Username 문법
    // select * from user where username = 파라미터로 넘어온 username
    public User findByUsername(String username);
}
