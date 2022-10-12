package com.example.demo.Service;

public interface AccountService {
	public int checkedOverlapUserInfo(String id, String email, String pw);

	public int checkedOverlapEmail(String email);

	public int signin(String id, String pw);
}
