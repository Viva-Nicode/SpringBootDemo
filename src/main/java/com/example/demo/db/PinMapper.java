package com.example.demo.db;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface PinMapper {
	public void insertPin(PinVO p);
}