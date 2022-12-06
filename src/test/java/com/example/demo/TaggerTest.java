package com.example.demo;

import static org.junit.Assert.assertThat;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Service.Tagger;

import lombok.RequiredArgsConstructor;

@SpringBootTest
public class TaggerTest {
	
	@Autowired
	private Tagger tagger;

	@RepeatedTest(5)
	@Transactional
	public void doTest(){
		System.out.println("test ex");
	}
}
