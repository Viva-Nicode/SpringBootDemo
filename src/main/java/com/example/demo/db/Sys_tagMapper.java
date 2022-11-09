package com.example.demo.db;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface Sys_tagMapper {
	public void insertSystag(List<Sys_tagVO> l);
}
