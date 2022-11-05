package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.DoctorDao;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.models.*;
import ar.edu.itba.paw.services.generics.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class DoctorServiceImpl extends GenericServiceImpl<DoctorDao, Doctor, Integer> implements DoctorService {
    @Autowired
    private DoctorDao repository;

    @Override
    public List<Doctor> findBy(String name, String surname, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities) {
        return this.repository.findBy(name, surname, offices, doctorSpecialties, localities);
    }

    @Override
    public List<Doctor> findBy(Collection<String> name, Collection<String> surname, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities) {
        return this.repository.findBy(name, surname, offices, doctorSpecialties, localities);
    }

    @Override
    @Transactional
    public Paginator<Doctor> findBy(String name, String surname, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities, int page, int perPage) {
        return this.repository.findBy(name, surname, offices, doctorSpecialties, localities, page, perPage);
    }

    @Override
    @Transactional
    public Paginator<Doctor> findBy(Collection<String> name, Collection<String> surname, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities, int page, int perPage) {
        return this.repository.findBy(name, surname, offices, doctorSpecialties, localities, page, perPage);
    }

    @Override
    public List<Doctor> findByUser(User user) {
        return this.repository.findByUser(user);
    }

    @Override
    protected DoctorDao getRepository() {
        return this.repository;
    }
}
