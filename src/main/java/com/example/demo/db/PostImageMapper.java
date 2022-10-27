package com.example.demo.db;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface PostImageMapper {

	public String findByPostid(@Param("postid")int postid);

}
