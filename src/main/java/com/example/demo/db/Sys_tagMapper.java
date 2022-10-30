package com.example.demo.db;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface Sys_tagMapper {
	public void insertSystag(Sys_tagVO s);
}
