package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.generic.GenericService;
import ar.edu.itba.paw.models.Picture;
import ar.edu.itba.paw.models.User;

public interface PictureService extends GenericService<Picture, Integer> {
    User changeProfilePic(User user, Picture picture);
}
