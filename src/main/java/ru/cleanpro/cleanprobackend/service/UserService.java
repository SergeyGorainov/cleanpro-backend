package ru.cleanpro.cleanprobackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.cleanpro.cleanprobackend.dto.UserCreateDto;
import ru.cleanpro.cleanprobackend.dto.UserDto;
import ru.cleanpro.cleanprobackend.dto.UserEditDto;
import ru.cleanpro.cleanprobackend.dto.UserRegisterDto;
import ru.cleanpro.cleanprobackend.model.UserEntity;
import ru.cleanpro.cleanprobackend.repository.UserRepository;
import ru.cleanpro.cleanprobackend.dto.PasswordChangeDto;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ===== Регистрация обычного пользователя =====

    @Transactional
    public UserEntity registerUser(UserRegisterDto dto) {
        if (userRepository.findByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Пароли не совпадают");
        }

        UserEntity user = UserEntity.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role("USER")
                .build();

        return userRepository.save(user);
    }

    // ===== Создание (для админки, если нужно) =====

    @Transactional
    public UserDto createUser(UserCreateDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        UserEntity user = UserEntity.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(userDto.getRole() != null ? userDto.getRole() : "USER")
                .build();

        return mapToDto(userRepository.save(user));
    }

    // ===== Получить всех (для админки) =====

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // ===== Управление пользователями (для админки) =====

    @Transactional(readOnly = true)
    public List<UserEntity> getAllUsersForAdmin() {
        return userRepository.findAllByOrderByLastNameAscFirstNameAsc();
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getUsersByRole(String role) {
        return userRepository.findAllByRoleOrderByLastNameAsc(role);
    }

    @Transactional(readOnly = true)
    public UserEntity getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + id));
    }

    @Transactional(readOnly = true)
    public long countAdmins() {
        return userRepository.countByRole("ADMIN");
    }

    @Transactional(readOnly = true)
    public long countEmployees() {
        return userRepository.countByRole("EMPLOYEE");
    }

    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.countByRole("USER");
    }

    @Transactional
    public UserEntity updateUser(Long id, UserEditDto dto) {
        UserEntity user = getById(id);

        UserEntity byEmail = userRepository.findByEmail(dto.getEmail());
        if (byEmail != null && !byEmail.getId().equals(id)) {
            throw new RuntimeException("Email уже занят другим пользователем");
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id, Long currentUserId) {
        if (id.equals(currentUserId)) {
            throw new RuntimeException("Нельзя удалить свою учётную запись");
        }
        userRepository.deleteById(id);
    }

    // Сброс пароля администратором.
    // Генерирует временный пароль из 8 символов, кодирует BCrypt,
    // возвращает в открытом виде — админ передаёт его пользователю.

    @Transactional
    public String resetPassword(Long id) {
        UserEntity user = getById(id);
        String temp = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(temp));
        userRepository.save(user);
        return temp;
    }

    // Смена пароля пользователем.
    // Проверяет текущий пароль, кодирует новый.

    @Transactional
    public void changePassword(Long userId, PasswordChangeDto dto) {
        UserEntity user = getById(userId);

        // Проверяем текущий пароль
        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Текущий пароль указан неверно");
        }

        // Проверяем совпадение нового пароля и подтверждения
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Новые пароли не совпадают");
        }

        // Кодируем и сохраняем
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    // ===== DTO mapping =====

    private UserDto mapToDto(UserEntity entity) {
        return UserDto.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .build();
    }
}