package com.example.demo.db;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserMapper {

	public void insertUserProfileImage(@Param("map") Map<String, String> map);

	public void updateUserEmail(@Param("map") Map<String, String> map);

	public List<UserVO> findUserByID(@Param("user_id") String user_id);

}
