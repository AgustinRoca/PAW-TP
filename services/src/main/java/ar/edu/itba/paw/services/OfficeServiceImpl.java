package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.OfficeDao;
import ar.edu.itba.paw.interfaces.services.OfficeService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.generics.GenericSearchableServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class OfficeServiceImpl extends GenericSearchableServiceImpl<OfficeDao, Office, Integer> implements OfficeService {
    @Autowired
    private OfficeDao repository;

    @Override
    public List<Office> findByCountry(Country country) {
        return this.repository.findByCountry(country);
    }

    @Override
    public List<Office> findByProvince(Province province) {
        return this.repository.findByProvince(province);
    }

    @Override
    public List<Office> findByLocality(Locality locality) {
        return this.findByLocality(locality);
    }

    @Override
    protected OfficeDao getRepository() {
        return this.repository;
    }
}
