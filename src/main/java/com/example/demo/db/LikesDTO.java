package com.example.demo.db;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
+--------+-------------+------+-----+---------+-------+
| Field  | Type        | Null | Key | Default | Extra |
+--------+-------------+------+-----+---------+-------+
| postid | int         | NO   | PRI | NULL    |       |
| liker  | varchar(20) | NO   | PRI | NULL    |       |
+--------+-------------+------+-----+---------+-------+ 
*/

@Entity
@Table(name = "likes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)
public class LikesDTO {
	
	@Id
	private LikesId likesId;

	public LikesDTO(int postid, String liker){
		this.likesId = new LikesId(postid, liker);
	}
}