package net.javaguides.springboot.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import net.javaguides.springboot.models.Role;
import net.javaguides.springboot.models.User;


@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RoleRepository roleRepository;

    public UserRepository(JdbcTemplate jdbcTemplate, RoleRepository roleRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.roleRepository = roleRepository;
    }
    
  
    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            return ps;
        }, keyHolder);

        Long userId = keyHolder.getKey().longValue();
        user.setId(userId);

        for (Role role : user.getRoles()) {
            if (role.getId() == null) {
                role = roleRepository.save(role);
            }
            jdbcTemplate.update(
                 "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)",
                 userId,
                 role.getId()
            );
        }
        return user;
    }

    
    public Optional<User> findByUsername(String username) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE username = ?", BeanPropertyRowMapper.newInstance(User.class), username);
            if (user != null) {
                List<Role> roles = jdbcTemplate.query("SELECT r.* FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?", BeanPropertyRowMapper.newInstance(Role.class), user.getId());
                user.setRoles(new HashSet<>(roles));
    
                System.out.println("User: " + user.toString());
                System.out.println("Roles: " + roles.toString());
            }
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    
    public Optional<User> findByEmail(String email) {
        try {
            User user = jdbcTemplate.queryForObject("SELECT * FROM users WHERE email = ?", BeanPropertyRowMapper.newInstance(User.class), email);
            if (user != null) {
                List<Role> roles = jdbcTemplate.query("SELECT r.* FROM roles r INNER JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = ?", BeanPropertyRowMapper.newInstance(Role.class), user.getId());
                user.setRoles(new HashSet<>(roles));
    
                System.out.println("User: " + user.toString());
                System.out.println("Roles: " + roles.toString());
            }
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    
    
    public Boolean existsByUsername(String username) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE username = ?", Integer.class, username);
        return count != null && count > 0;
    }

    
    public Boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, email);
        return count != null && count > 0;
    }
    
    public List<User> findAll() {
        List<User> users = new ArrayList<User>();
        users = jdbcTemplate.query("SELECT * FROM user", BeanPropertyRowMapper.newInstance(User.class));
        return users;
    }
}
