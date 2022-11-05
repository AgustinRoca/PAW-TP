package ar.edu.itba.paw.interfaces.daos;

import ar.edu.itba.paw.interfaces.daos.generic.GenericSearchableDao;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Locality;
import ar.edu.itba.paw.models.Office;
import ar.edu.itba.paw.models.Province;

import java.util.List;

public interface OfficeDao extends GenericSearchableDao<Office, Integer> {
    List<Office> findByCountry(Country country);

    List<Office> findByProvince(Province province);

    List<Office> findByLocality(Locality locality);
}
