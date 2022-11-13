package com.example.demo.db;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.demo.Service.PinInfoObject;

import org.apache.ibatis.annotations.Param;

@Mapper
@Repository
public interface TagMapper {
	public void insertTag(List<tagVO> l);

	public List<String> findTagByUseridAll(@Param("user_id") String user_id);

	public List<String> findTagByUseridOnlyPublic(@Param("user_id") String user_id);

	public List<TagCount> getTagCountList(@Param("user_id") String user_id);

	public int deleteTagByUseridAndTagName(@Param("map") Map<String, String> map);

	public int modifyTagName(@Param("map") Map<String, String> map);

	public List<PinInfoObject> getPininfoListByUploaderAll(@Param("uploader") String uploader);

	public List<PinInfoObject> getPininfoListByUploaderOnlyPublic(@Param("uploader") String uploader);
}
