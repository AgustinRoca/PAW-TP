package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.ProvinceDao;
import ar.edu.itba.paw.interfaces.services.ProvinceService;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Province;
import ar.edu.itba.paw.services.generics.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProvinceServiceImpl extends GenericServiceImpl<ProvinceDao, Province, Integer> implements ProvinceService {
    @Autowired
    private ProvinceDao repository;

    @Override
    public Optional<Province> findByCountryAndId(Country country, Integer id) {
        return this.repository.findByCountryAndId(country, id);
    }

    @Override
    public List<Province> findByCountry(Country country) {
        return this.repository.findByCountry(country);
    }

    @Override
    public List<Province> findByCountryAndName(Country country, String name) {
        return this.repository.findByCountryAndName(country, name);
    }

    @Override
    protected ProvinceDao getRepository() {
        return this.repository;
    }
}
