package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.generic.GenericService;
import ar.edu.itba.paw.models.*;

import java.util.Collection;
import java.util.List;

public interface DoctorService extends GenericService<Doctor, Integer> {
    List<Doctor> findBy(String name, String surname, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities);

    List<Doctor> findBy(Collection<String> names, Collection<String> surnames, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities);

    Paginator<Doctor> findBy(String name, String surname, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities, int page, int perPage);

    Paginator<Doctor> findBy(Collection<String> names, Collection<String> surnames, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities, int page, int perPage);

    List<Doctor> findByUser(User user);
}
