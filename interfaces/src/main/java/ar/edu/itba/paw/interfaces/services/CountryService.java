package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.generic.GenericSearchableService;
import ar.edu.itba.paw.models.Country;
import ar.edu.itba.paw.models.Province;

import java.util.Set;

public interface CountryService extends GenericSearchableService<Country, String> {
}