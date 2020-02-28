package com.bacefook.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bacefook.entity.UserInfo;

public interface UsersInfoRepository extends JpaRepository<UserInfo, Integer> {
  UserInfo findByPhone(String phone);
}
