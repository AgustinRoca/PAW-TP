package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.generic.GenericService;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Province;

import java.util.List;
import java.util.Optional;

public interface ProvinceService extends GenericService<Province, Integer> {
    Optional<Province> findByCountryAndId(Country country, Integer id);

    List<Province> findByCountry(Country country);

    /**
     * Returns Provinces with a name similar to the one provided filtered out by Country.
     * The search is not case-sensitive nor exact
     *
     * @param name the province's name
     * @return a collection of matched provinces
     */
    List<Province> findByCountryAndName(Country country, String name);
}
