package com.ten.service;

import com.ten.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ten.dao.RoleDao;
import com.ten.model.Role;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    public RoleServiceImpl() {
    }

    private RoleDao roleDao;

    @Autowired
    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public void addRole(Role role) {
        roleDao.save(role);
    }

    @Override
    public Role getRoleByName(String roleName) {
        return roleDao.getRoleByName(roleName);
    }

    @Override
    public Role getRoleById(Long id) {
        return roleDao.getById(id);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleDao.getAll();
    }

    @Override
    public void updateRole(Role role) {
        roleDao.update(role);
    }

    @Override
    public void deleteRoleById(Long id) {
        roleDao.deleteById(id);
    }
}
