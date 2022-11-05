package ar.edu.itba.paw.interfaces.daos;

import ar.edu.itba.paw.interfaces.daos.generic.GenericDao;
import ar.edu.itba.paw.models.*;

import java.util.Collection;
import java.util.List;

public interface DoctorDao extends GenericDao<Doctor, Integer> {
    /**
     * @param name             if not null nor empty String, the search will filter the list by doctor's name that
     *                         contains the string (ignoring case)
     * @param surname          if not null nor empty String, the search will filter the list by doctor's surname that
     *                         contains the string (ignoring case)
     * @param offices          if not null nor empty collection, the search will include only those doctors
     *                         that work for any of the offices in the collection
     * @param doctorSpecialties if not null nor empty collection, the search will include only those doctors
     *                         that has any of the specialties in the collection
     * @return the filtered set
     */
    List<Doctor> findBy(String name, String surname, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities);

    List<Doctor> findBy(Collection<String> names, Collection<String> surnames, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities);

    Paginator<Doctor> findBy(String name, String surname, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities, int page, int pageSize);

    Paginator<Doctor> findBy(Collection<String> names, Collection<String> surnames, Collection<Office> offices, Collection<DoctorSpecialty> doctorSpecialties, Collection<Locality> localities, int page, int pageSize);

    List<Doctor> findByUser(User user);
}
