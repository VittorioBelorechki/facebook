package com.bacefook.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bacefook.entity.Gender;

@Repository
public interface GenderRepository extends JpaRepository<Gender, Integer> {
	Gender findByGenderName(String genderName);
}
