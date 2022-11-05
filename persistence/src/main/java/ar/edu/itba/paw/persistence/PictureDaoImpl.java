package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.daos.PictureDao;
import ar.edu.itba.paw.models.Picture;
import ar.edu.itba.paw.models.Picture_;
import ar.edu.itba.paw.persistence.generics.GenericDaoImpl;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository
public class PictureDaoImpl extends GenericDaoImpl<Picture, Integer> implements PictureDao {
    public PictureDaoImpl() {
        super(Picture.class, Picture_.id);
    }

    @Override
    protected void insertOrderBy(CriteriaBuilder builder, CriteriaQuery<Picture> query, Root<Picture> root) {
    }
}
