package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// 이렇게 JpaRepository를 상송받으면 기본적인 CRUD를 들고 있음.
// @Repository라는 어노테이션이 없어도 IOC가 된다 --> JpaRepository를 상속했기 떄문에..
public interface UserRepository extends JpaRepository<User, Integer> {
}
