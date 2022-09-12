package com.example.demo.db;

import java.sql.Timestamp;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
desc PostInfo;
+-------------+---------------+------+-----+---------+-------------------+
| Field       | Type          | Null | Key | Default | Extra             |
+-------------+---------------+------+-----+---------+-------------------+
| postid      | int           | NO   | PRI | NULL    | auto_increment    |
| title       | varchar(30)   | NO   |     | NULL    |                   |
| writer      | varchar(20)   | NO   | MUL | NULL    |                   |
| hits        | int           | NO   |     | NULL    |                   |
| likes       | int           | NO   |     | 0       | DEFAULT_GENERATED |
| contents    | varchar(4096) | NO   |     | NULL    |                   |
| writtenTime | datetime      | YES  |     | now()   | DEFAULT_GENERATED |
| commentsNum | int           | YES  |     | 0       | DEFAULT_GENERATED |
| imageNum    | int           | YES  |     | 0       | DEFAULT_GENERATED |
+-------------+---------------+------+-----+---------+-------------------+
*/


@Entity
@Table(name = "Postinfo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)
public class PostInfoDTO implements Comparable<PostInfoDTO> {

	public PostInfoDTO(String title, String writer, String contents, int imagenum) {
		this.title = title;
		this.writer = writer;
		this.contents = contents;
		this.imagenum = imagenum;
	}

	@Id
	@Column(name = "postid")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int postid;

	@Column(name = "title", length = 30)
	private String title;

	@Column(name = "writer", length = 20)
	private String writer;

	@Column(name = "hits")
	private int hits;

	@Column(name = "likes")
	private int likes;

	@Column(name = "contents", length = 4096)
	private String contents;

	@Column(name = "writtentime")
	@CreationTimestamp
	private Timestamp writtentime;

	@Column(name = "commentsnum")
	private int commentsnum;

	@Column(name = "imagenum")
	private int imagenum;

	@Override
	public int compareTo(PostInfoDTO o) {
		if (this.postid > o.getPostid()) {
			return -1;
		} else if (this.postid < o.getPostid()) {
			return 1;
		}
		return 0;
	}

}



