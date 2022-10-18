package com.example.demo.db;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface PostInfoMapper {
	public List<PostInfoVO> findByUserid(@Param("user_id") String user_id);

	public String isMyself(@Param("map") Map<String, String> map);
	
	public void deletePost(@Param("postid") int postid);
}
