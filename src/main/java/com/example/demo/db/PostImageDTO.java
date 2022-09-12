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
+-----------+--------------+------+-----+---------+-------+
| Field     | Type         | Null | Key | Default | Extra |
+-----------+--------------+------+-----+---------+-------+
| postid    | int          | NO   | PRI | NULL    |       |
| imagename | varchar(100) | NO   | PRI | NULL    |       |
+-----------+--------------+------+-----+---------+-------+
*/

@Entity
@Table(name = "Postimage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Access(AccessType.FIELD)

public class PostImageDTO {

	@Id
	private PostImageId postImageId;

	public PostImageDTO(int postid, String name){
		this.postImageId = new PostImageId(postid, name);
	}

}


