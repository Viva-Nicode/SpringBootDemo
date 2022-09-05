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
import lombok.AllArgsConstructor;
import lombok.Getter;
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
@Table(name = "PostInfo")
@Getter
@Setter
@Access(AccessType.FIELD)
public class PostInfoDTO {

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

	@Column(name = "writtentime", columnDefinition = "datetime default now() not null")
	private Timestamp writtentime;

	@Column(name = "commentsnum")
	private int commentsnum;

	@Column(name = "imagenum")
	private int imagenum;

	public PostInfoDTO(int postid, String title, String writer, int hits, int likes, String contents,
			Timestamp writtentime, int commentsnum, int imagenum) {
		this.postid = postid;
		this.title = title;
		this.writer = writer;
		this.hits = hits;
		this.likes = likes;
		this.contents = contents;
		this.writtentime = writtentime;
		this.commentsnum = commentsnum;
		this.imagenum = imagenum;
	}

}
