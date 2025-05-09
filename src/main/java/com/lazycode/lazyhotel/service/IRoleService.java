package com.lazycode.lazyhotel.service;

import com.lazycode.lazyhotel.model.Role;
import com.lazycode.lazyhotel.model.User;

import java.util.List;

public interface IRoleService {
    List<Role> getRoles();
    Role createRole(Role theRole);

    void deleteRole(Long id);
    Role findByName(String name);
    User removeUserFromRole(Long userId, Long roleId);

    User assignUserToUser(Long userId, Long roleId);

    Role removeAllUsersFromRole(Long roleId);
}
