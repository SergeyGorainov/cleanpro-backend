package ru.cleanpro.cleanprobackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cleanpro.cleanprobackend.model.UserEntity;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    // Все пользователи по алфавиту (по фамилии)
    List<UserEntity> findAllByOrderByLastNameAscFirstNameAsc();

    // Только пользователи с указанной ролью
    List<UserEntity> findAllByRoleOrderByLastNameAsc(String role);

    // Счётчик по роли
    long countByRole(String role);
}