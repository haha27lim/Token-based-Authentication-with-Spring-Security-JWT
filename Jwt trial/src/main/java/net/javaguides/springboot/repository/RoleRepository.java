package net.javaguides.springboot.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import net.javaguides.springboot.models.ERole;
import net.javaguides.springboot.models.Role;


@Repository
public class RoleRepository {
    
    private final JdbcTemplate jdbcTemplate;

    public RoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    
    public Role save(Role role) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO roles (name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, role.getName().toString());
            return ps;
        }, keyHolder);

        Integer roleId = (Integer) keyHolder.getKeys().get("id");
        role.setId(roleId);
        
        return role;
    }

    
    public Optional<Role> findByName(ERole name) {
        String sql = "SELECT * FROM roles WHERE name = ?";
        Role role = jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(Role.class), name.toString());
        return Optional.ofNullable(role);
    }
}