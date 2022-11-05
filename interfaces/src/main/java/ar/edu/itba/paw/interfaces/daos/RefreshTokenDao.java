package ar.edu.itba.paw.interfaces.daos;

import ar.edu.itba.paw.interfaces.daos.generic.GenericDao;
import ar.edu.itba.paw.models.RefreshToken;

import java.util.Optional;

public interface RefreshTokenDao extends GenericDao<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    void removeByToken(String token);

    void removeByUserId(Integer userId);

    int removeTokensOlderThan(int days);
}
