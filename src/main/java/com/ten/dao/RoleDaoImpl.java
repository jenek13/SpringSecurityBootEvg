package com.ten.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.ten.model.Role;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
@Transactional
public class RoleDaoImpl implements RoleDao {

    public RoleDaoImpl() {
    }

    @PersistenceContext
    private EntityManager em;

    @Override
    public Role getRoleByName(String roleName) {
        TypedQuery<Role> query = em.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class);
        query.setParameter("name", roleName);
        return query.getSingleResult();
    }

    @Override
    public void save(Role role) {
        em.persist(role);
    }

    @Override
    public Role getById(long id) {
        return em.find(Role.class, id);
    }

    @Override
    public List<Role> getAll() {
        TypedQuery<Role> query = em.createQuery("SELECT r FROM Role r", Role.class);
        return query.getResultList();
    }

    @Override
    public void update(Role role) {
        em.merge(role);
    }

    @Override
    public void deleteById(long id) {
        Query query = em.createQuery("DELETE from Role r WHERE r.id = :id");
        query.setParameter("id", id);
        query.executeUpdate();
    }
}
