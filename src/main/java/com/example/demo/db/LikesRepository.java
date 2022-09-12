package com.example.demo.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikesRepository extends JpaRepository<LikesDTO, LikesId> {

	public boolean existsBylikesId(LikesId li);

	public void deleteById(LikesId li);


}
