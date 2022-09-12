package com.example.demo.Service;

public interface AccountService {
	public int checkedOverlapUserInfo(String id, String email, String name, String pw);

	public int signin(String id, String pw);
}
