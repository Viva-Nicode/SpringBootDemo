package com.example.demo.db;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CommentsMapper {
	public List<CommentsVO> findCommentsByPostid(@Param("postid") int postid);

	public void insertComment(CommentsVO cvo);

	public List<MyComments> findByCommenter(@Param("user_id") String user_id);

	public void deleteCommentByCommentid(@Param("commentid") int commentid);
}
