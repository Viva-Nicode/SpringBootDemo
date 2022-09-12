package com.example.demo.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostImageId implements Serializable{
	
	@Column(name = "postid")
	private int postid;

	@Column(name = "imagename")
	private String imagename;
}
