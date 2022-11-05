package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.generic.GenericService;
import ar.edu.itba.paw.models.Locality;
import ar.edu.itba.paw.models.Province;

import java.util.Collection;
import java.util.Optional;

public interface LocalityService extends GenericService<Locality, Integer> {
    Optional<Locality> findByProvinceAndId(Province province, Integer id);

    Collection<Locality> findByProvince(Province province);

    /**
     * Returns Provinces with a name similar to the one provided filtered out by Country.
     * The search is not case-sensitive nor exact
     *
     * @param name the province's name
     * @return a collection of matched provinces
     */
    Collection<Locality> findByProvinceAndName(Province province, String name);
}
