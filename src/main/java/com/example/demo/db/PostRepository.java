package com.example.demo.db;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostInfoDTO, Integer> {
	
	public List<PostInfoDTO> findByWriter(String writer);
	public List<PostInfoDTO> findByPostid(int postid);

	@Modifying
	@Query(value = "UPDATE PostInfoDTO p SET p.hits = p.hits + 1 WHERE p.postid = :postid")
	public void increasedHits(@Param("postid") int postid);

}