package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.PictureDao;
import ar.edu.itba.paw.interfaces.services.PictureService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Picture;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.generics.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PictureServiceImpl extends GenericServiceImpl<PictureDao, Picture, Integer> implements PictureService {
    @Autowired
    private PictureDao repository;
    @Autowired
    private UserService userService;

    @Override
    protected PictureDao getRepository() {
        return this.repository;
    }

    @Override
    @Transactional
    public User changeProfilePic(User user, Picture picture) {
        if(user == null)
            throw new IllegalArgumentException();
        if (user.getProfilePictureId() != null)
            remove(user.getProfilePictureId());
        user.setProfilePicture(create(picture));
        userService.update(user);
        return user;
    }
}
