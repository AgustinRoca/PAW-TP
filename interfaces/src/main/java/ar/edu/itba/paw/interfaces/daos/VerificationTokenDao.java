package ar.edu.itba.paw.interfaces.daos;

import ar.edu.itba.paw.interfaces.daos.generic.GenericDao;
import ar.edu.itba.paw.models.VerificationToken;

import java.util.Optional;

public interface VerificationTokenDao extends GenericDao<VerificationToken, Integer> {
    Optional<VerificationToken> findByToken(String token);
}
