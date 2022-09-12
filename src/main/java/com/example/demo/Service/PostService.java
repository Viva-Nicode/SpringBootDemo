package com.example.demo.Service;

import com.example.demo.db.PostInfoDTO;

public interface PostService {
	public int insertPost(PostInfoDTO p);

	public PostInfoDTO getPost(int postid);
	
	public void increasedHits(int postid);
}
