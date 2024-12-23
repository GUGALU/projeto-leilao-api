package com.leilao.backend.repository;

import com.leilao.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    User findByCpf(String cpf);

    User findByEmailAndPassword(String email, String password);
}
