package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.LocalityDao;
import ar.edu.itba.paw.interfaces.services.LocalityService;
import ar.edu.itba.paw.models.Locality;
import ar.edu.itba.paw.models.Province;
import ar.edu.itba.paw.services.generics.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class LocalityServiceImpl extends GenericServiceImpl<LocalityDao, Locality, Integer> implements LocalityService {
    @Autowired
    private LocalityDao repository;

    @Override
    public Optional<Locality> findByProvinceAndId(Province province, Integer id) {
        return this.repository.findByProvinceAndId(province, id);
    }

    @Override
    public Collection<Locality> findByProvince(Province province) {
        return this.repository.findByProvince(province);
    }

    @Override
    public Collection<Locality> findByProvinceAndName(Province province, String name) {
        return this.repository.findByProvinceAndName(province, name);
    }

    @Override
    protected LocalityDao getRepository() {
        return this.repository;
    }
}
