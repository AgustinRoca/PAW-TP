package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.daos.DoctorSpecialtyDao;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.DoctorSpecialtyService;
import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.models.DoctorSpecialty;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.generics.GenericSearchableServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorSpecialtyServiceImpl extends GenericSearchableServiceImpl<DoctorSpecialtyDao, DoctorSpecialty, Integer> implements DoctorSpecialtyService {
    @Autowired
    private DoctorSpecialtyDao repository;

    @Autowired
    DoctorService doctorService;

    @Override
    public void addToUser(int doctorSpecialtyId, User user) {
        List<Doctor> doctors = this.doctorService.findByUser(user);
        Optional<DoctorSpecialty> doctorSpecialty = findById(doctorSpecialtyId);
        if(doctorSpecialty.isPresent()) {
            for (Doctor doctor : doctors) {
                if(!doctor.getDoctorSpecialties().contains(doctorSpecialty.get())) {
                    doctor.getDoctorSpecialties().add(doctorSpecialty.get());
                    doctorService.update(doctor);
                }
            }
        }
    }

    @Override
    public void removeFromUser(int doctorSpecialtyId, User user) {
        List<Doctor> doctors = this.doctorService.findByUser(user);
        Optional<DoctorSpecialty> doctorSpecialty = findById(doctorSpecialtyId);
        if(doctorSpecialty.isPresent()) {
            for (Doctor doctor : doctors) {
                doctor.getDoctorSpecialties().remove(doctorSpecialty.get());
                doctorService.update(doctor);
            }
        }
    }

    @Override
    protected DoctorSpecialtyDao getRepository() {
        return this.repository;
    }
}
