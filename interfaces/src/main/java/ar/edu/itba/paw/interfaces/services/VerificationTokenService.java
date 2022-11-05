package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.generic.GenericService;
import ar.edu.itba.paw.models.VerificationToken;

import java.util.Optional;

public interface VerificationTokenService extends GenericService<VerificationToken, Integer> {
    Optional<VerificationToken> findByToken(String token);

    VerificationToken generate();

    String refresh(VerificationToken verificationToken);
}
