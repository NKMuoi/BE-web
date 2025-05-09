package com.lazycode.lazyhotel.service;

import com.lazycode.lazyhotel.exception.RoleAlreadyExistsException;
import com.lazycode.lazyhotel.exception.UserAlreadyExistsException;
import com.lazycode.lazyhotel.model.Role;
import com.lazycode.lazyhotel.model.User;
import com.lazycode.lazyhotel.repository.RoleRepository;
import com.lazycode.lazyhotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role theRole) {
        String roleName = "ROLE"+theRole.getName().toUpperCase();
        Role role = new Role(roleName);
        if(roleRepository.existsByName(role)) {
            throw new RoleAlreadyExistsException(theRole.getName()+" role already exists");
        }

        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long roleId) {
        this.removeAllUsersFromRole(roleId);
        roleRepository.deleteById(roleId);

    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new RuntimeException("Name not found"));
    }

    @Override
    public User removeUserFromRole(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (role.getUsers().contains(user)) {
            role.removeRoleFromUser(user);
            roleRepository.save(role);
            return user;
        }

        throw new UsernameNotFoundException("User not found");
    }

    @Override
    public User assignUserToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        if (user.getRoles().contains(role)) {
            throw new UserAlreadyExistsException(
                    user.getFirstName() + " is already assigned to the " + role.getName() + " role");
        }
        role.assignRoleToUser(user);
        roleRepository.save(role);
        return user;
    }

    @Override
    public Role removeAllUsersFromRole(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        role.removeAllUsersFromRole();
        return roleRepository.save(role);
    }
}
