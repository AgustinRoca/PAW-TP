package ar.edu.itba.paw.interfaces.daos;

import ar.edu.itba.paw.interfaces.daos.generic.GenericSearchableDao;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface UserDao extends GenericSearchableDao<User, Integer> {
    boolean existsEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationTokenId(Integer id);
}
