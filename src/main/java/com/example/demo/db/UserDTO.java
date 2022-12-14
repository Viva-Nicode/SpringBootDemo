package com.example.demo.db;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
desc User;
+----------+--------------+------+-----+-----------+-------------------+
| Field    | Type         | Null | Key | Default   | Extra             |
+----------+--------------+------+-----+-----------+-------------------+
| ID       | varchar(20)  | NO   | PRI | NULL      |                   |
| PW       | varchar(256) | NO   |     | NULL      |                   |
| email    | varchar(40)  | NO   |     | NULL      |                   |
| joindate | date         | YES  |     | curdate() | DEFAULT_GENERATED |
| profile  | varchar(64)  | YES  |     | NULL      |                   |
+----------+--------------+------+-----+-----------+-------------------+
*/

@Entity
@Table(name = "User")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
	
	@Id
	@Column(name = "ID", length = 20)
	private String ID;

	@Column(name = "PW", length = 20)
	private String PW;

	@Column(name = "email", length = 40)
	private String email;

	@Column(name = "joindate")
	@CreationTimestamp
	private Date date;

	@Column(name = "profile")
	private String profile;
}
