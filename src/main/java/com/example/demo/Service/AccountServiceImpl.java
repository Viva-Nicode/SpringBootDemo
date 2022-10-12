package com.example.demo.Service;

import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.db.UserDTO;
import com.example.demo.db.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

	private final UserRepository ur;
	private final PasswordEncoder pe;

	@Override
	public int checkedOverlapUserInfo(String id, String email, String pw) {

		if (!ur.findByID(id).isEmpty()) {
			return 1;
		} else if (!ur.findByEmail(email).isEmpty()) {
			return 2;
		} else {
			ur.save(new UserDTO(id, pe.encode(pw), email, null, null));
			return 0;
		}
	}

	@Override
	public int signin(String id, String pw) {

		List<UserDTO> l = ur.findByID(id);

		if (l.isEmpty())
			return 1;
		else if (!pe.matches(pw, l.get(0).getPW()))
			return 1;
		return 0;
	}

	@Override
	public int checkedOverlapEmail(String email) {
		if (!ur.findByEmail(email).isEmpty()) {
			return 1;// 중복되서 불가능
		}
		return 0; // 중복 안되서 바꾸기 가능
	}
}
