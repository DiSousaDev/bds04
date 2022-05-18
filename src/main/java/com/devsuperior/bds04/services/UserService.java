package com.devsuperior.bds04.services;

import com.devsuperior.bds04.dto.UserDto;
import com.devsuperior.bds04.entities.User;
import com.devsuperior.bds04.repositories.RoleRepository;
import com.devsuperior.bds04.repositories.UserRepository;
import com.devsuperior.bds04.services.exceptions.DataNotFoundException;
import com.devsuperior.bds04.services.exceptions.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository repository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<User> user = repository.findByEmail(email);

        if(user.isEmpty()) {
            logger.error("User not found: " + email);
            throw new UsernameNotFoundException("Email not found.");
        }
        logger.info("User found: " + user);
        return user.get();
    }

    @Transactional(readOnly = true)
    public Page<UserDto> findAllPaged(Pageable pageable) {
        return repository.findAll(pageable).map(UserDto::new);
    }

    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        User user = findUserById(id);
        return new UserDto(user);
    }

    @Transactional
    public UserDto save(UserDto userDto) {
        User user = new User();
        copyDtoToEntity(userDto, user);
        user = repository.saveAndFlush(user);
        return new UserDto(user);
    }

    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User prod = findUserById(id);
        copyDtoToEntity(userDto, prod);
        return new UserDto(repository.save(prod));
    }

    public void deleteById(Long id) {
        try{
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new DataNotFoundException("Usuário não encontrado id: " + id + " entity: " + User.class.getName());
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Impossivel excluir. Possui entidades relacionadas id: " + id + " entity: " + User.class.getName());
        }

    }

    private User findUserById(Long id) {
        return repository.findById(id).orElseThrow(() -> new DataNotFoundException(
                "Usuario não encontrado id: " + id + " entity: " + User.class.getName()));
    }

    private void copyDtoToEntity(UserDto userDto, User user) {
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.getRoles().clear();
        userDto.getRoles().forEach(role -> user.getRoles().add(roleRepository.getOne(role.getId())));

    }

}
