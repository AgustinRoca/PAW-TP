package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.daos.VerificationTokenDao;
import ar.edu.itba.paw.models.VerificationToken;
import ar.edu.itba.paw.models.VerificationToken_;
import ar.edu.itba.paw.persistence.generics.GenericDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Repository
public class VerificationTokenDaoImpl extends GenericDaoImpl<VerificationToken, Integer> implements VerificationTokenDao {
    public VerificationTokenDaoImpl() {
        super(VerificationToken.class, VerificationToken_.id);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException();
        }
        return this.findBy(VerificationToken_.token, token).stream().findFirst();
    }

    @Override
    protected void insertOrderBy(CriteriaBuilder builder, CriteriaQuery<VerificationToken> query, Root<VerificationToken> root) {
    }
}
