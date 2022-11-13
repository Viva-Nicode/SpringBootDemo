package com.example.demo.db;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.example.demo.Service.PinInfoObject;

import org.apache.ibatis.annotations.Param;

/* selectNottingTagPinsNone */
@Mapper
@Repository
public interface TagMapper {

	/* <핀이름, 태그이름>리스트를 반복해서 tag 테이블에 insert한다. */
	public void insertTag(List<tagVO> l);

	public void insertTagNone(List<String> l);

	/* 태그가 하나도 붙여지지 않은 핀의 이름리스트 반환. 이 핀들에 대해 none태그를 붙여주기 위함. */
	public List<String> selectNottingTagPinsNone(@Param("user_id") String user_id);

	public List<TagCount> getTagCountList(@Param("user_id") String user_id);
	
	public int deleteTagByUseridAndTagName(@Param("map") Map<String, String> map);
	
	public int modifyTagName(@Param("map") Map<String, String> map);

	public List<String> findTagByUseridAll(@Param("user_id") String user_id);

	public List<String> findTagByUseridOnlyPublic(@Param("user_id") String user_id);

	public List<PinInfoObject> getPininfoListByUploaderAll(@Param("uploader") String uploader);

	public List<PinInfoObject> getPininfoListByUploaderOnlyPublic(@Param("uploader") String uploader);
}
