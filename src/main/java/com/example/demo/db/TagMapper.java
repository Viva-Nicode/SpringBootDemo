package com.example.demo.db;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

@Mapper
@Repository
public interface TagMapper {
	public void insertTag(tagVO t);

	public List<String> findTagByUserid(@Param("user_id") String user_id);

	public List<TagCount> getTagCountList(@Param("user_id") String user_id);

	public int deleteTagByUseridAndTagName(@Param("map") Map<String, String> map);

	public int modifyTagName(@Param("map") Map<String, String> map);
}
