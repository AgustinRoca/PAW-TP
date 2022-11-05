package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.daos.RefreshTokenDao;
import ar.edu.itba.paw.models.RefreshToken;
import ar.edu.itba.paw.models.RefreshToken_;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.persistence.generics.GenericDaoImpl;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.Optional;

@Repository
public class RefreshTokenDaoImpl extends GenericDaoImpl<RefreshToken, Integer> implements RefreshTokenDao {
    public RefreshTokenDaoImpl() {
        super(RefreshToken.class, RefreshToken_.id);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException();
        }
        return this.findBy(RefreshToken_.token, token).stream().findFirst();
    }

    @Override
    @Transactional
    public void removeByToken(String token) {
        if (token == null) {
            throw new IllegalArgumentException();
        }

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaDelete<RefreshToken> delete = builder.createCriteriaDelete(RefreshToken.class);

        Root<RefreshToken> root = delete.from(RefreshToken.class);
        delete.where(builder.equal(root.get(RefreshToken_.token), token));
        this.getEntityManager().createQuery(delete).executeUpdate();
    }

    @Override
    @Transactional
    public void removeByUserId(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException();
        }

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaDelete<RefreshToken> delete = builder.createCriteriaDelete(RefreshToken.class);

        Root<RefreshToken> root = delete.from(RefreshToken.class);
        delete.where(builder.equal(root.get(RefreshToken_.userId), userId));
        this.getEntityManager().createQuery(delete).executeUpdate();
    }

    @Override
    @Transactional
    public int removeTokensOlderThan(int days) {
        LocalDateTime maximumCreatedDate = LocalDateTime.now().minusDays(days);

        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaDelete<RefreshToken> delete = builder.createCriteriaDelete(RefreshToken.class);
        Root<RefreshToken> root = delete.from(RefreshToken.class);

        delete.where(builder.lessThanOrEqualTo(root.get(RefreshToken_.createdDate), maximumCreatedDate));
        return this.getEntityManager().createQuery(delete).executeUpdate();
    }

    @Override
    protected void insertOrderBy(CriteriaBuilder builder, CriteriaQuery<RefreshToken> query, Root<RefreshToken> root) {
    }
}
