package com.lazycode.lazyhotel.controller;


import com.lazycode.lazyhotel.exception.RoleAlreadyExistsException;
import com.lazycode.lazyhotel.model.Role;
import com.lazycode.lazyhotel.model.User;
import com.lazycode.lazyhotel.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final IRoleService roleService;

    @GetMapping("/all-roles")
    public ResponseEntity<List<Role>> getAllRoles(){
        return new ResponseEntity<>(roleService.getRoles(), HttpStatus.OK);
    }


    @PostMapping("/create-new-role")
    public ResponseEntity<String> createRole(@RequestBody Role theRole){
        try{
            roleService.createRole(theRole);
            return ResponseEntity.ok("New role created successfully");
        }catch(RoleAlreadyExistsException re){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(re.getMessage());

        }
    }

    @DeleteMapping("/delete/{roleId}")
    public void deleteRole(@PathVariable("roleId") Long roleId){
        roleService.deleteRole(roleId);
    }


    @PostMapping("/remove-all-users-from-role/{roleId}")
    public Role removeAllUsersFromRole(@PathVariable("roleId") Long roleId){
        return roleService.removeAllUsersFromRole(roleId);
    }

    @PostMapping("/remove-user-from-role")
    public User removeUserFromRole(@RequestParam("userId") Long userId,
                                   @RequestParam("roleId") Long roleId){
        return roleService.removeUserFromRole(userId, roleId);
    }

    @PostMapping("/assign-user-to-role")
    public User assignUserToRole(@RequestParam("userId") Long userId,
                                 @RequestParam("roleId") Long roleId){
        return roleService.assignUserToUser(userId, roleId);
    }
}
