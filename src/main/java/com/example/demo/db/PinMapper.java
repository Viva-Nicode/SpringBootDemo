package com.example.demo.db;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import org.apache.ibatis.annotations.Param;

@Repository
@Mapper
public interface PinMapper {
	public void insertPin(PinVO p);

	public List<PinVO> checkPinByUserid(@Param("map") Map<String, String> m);

	public int deletePins(List<String> l);
}