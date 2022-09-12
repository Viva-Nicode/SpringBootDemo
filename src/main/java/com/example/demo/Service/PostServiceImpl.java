package com.example.demo.Service;

import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.demo.db.PostRepository;

import lombok.RequiredArgsConstructor;
import com.example.demo.db.PostInfoDTO;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	private final PostRepository pr;

	public int insertPost(PostInfoDTO p) {
		pr.save(p);
		List<PostInfoDTO> l = pr.findByWriter(p.getWriter());
		Collections.sort(l);
		return l.get(0).getPostid();
	}

	@Override
	public PostInfoDTO getPost(int postid) {
		return pr.findByPostid(postid).get(0);
	}

	public void increasedHits(int postid){
		pr.increasedHits(postid);
	}
}
