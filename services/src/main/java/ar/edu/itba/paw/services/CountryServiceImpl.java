package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.CountryDao;
import ar.edu.itba.paw.interfaces.services.CountryService;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Province;
import ar.edu.itba.paw.services.generics.GenericSearchableServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CountryServiceImpl extends GenericSearchableServiceImpl<CountryDao, Country, String> implements CountryService {
    @Autowired
    private CountryDao repository;

    @Override
    protected CountryDao getRepository() {
        return this.repository;
    }
}
