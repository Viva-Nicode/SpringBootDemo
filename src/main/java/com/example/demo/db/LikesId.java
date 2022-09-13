package com.example.demo.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class LikesId implements Serializable{
	
	@Column(name = "postid")
	private int postid;

	@Column(name = "liker")
	private String liker;

	public LikesId(int postid, String liker){
		this.postid = postid;
		this.liker = liker;
	}
}
