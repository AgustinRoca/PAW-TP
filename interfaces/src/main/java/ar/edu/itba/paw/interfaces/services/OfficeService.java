package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.generic.GenericSearchableService;
import ar.edu.itba.paw.models.*;

import java.util.Collection;
import java.util.List;

public interface OfficeService extends GenericSearchableService<Office, Integer> {
    List<Office> findByCountry(Country country);

    List<Office> findByProvince(Province province);

    List<Office> findByLocality(Locality locality);
}
