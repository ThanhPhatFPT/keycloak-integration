package com.devteria.profile.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.devteria.profile.entity.Profile;

import java.util.Optional;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, String> {
    Profile findByUsername(String username);  // Tìm kiếm người dùng theo username
    Profile findByEmail(String email);        // Tìm kiếm người dùng theo email
    String findProfileIdByUsername(String username);  // Tìm kiếm profileId theo username
//    Optional<Profile> findByUsername(String username);

}
