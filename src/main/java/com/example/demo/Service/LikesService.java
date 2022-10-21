package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.db.LikesDTO;
import com.example.demo.db.LikesId;
import com.example.demo.db.LikesRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LikesService {

	@Autowired
	private final LikesRepository lr;

	public void doLikes(int postid, String liker, int flag) {
		if (flag == 1) {
			lr.deleteById(new LikesId(postid, liker));
		} else if (flag == 0) {
			lr.save(new LikesDTO(postid, liker));
		}
	}
}
