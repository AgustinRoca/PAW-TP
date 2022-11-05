package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.MediCareException;
import ar.edu.itba.paw.interfaces.daos.VerificationTokenDao;
import ar.edu.itba.paw.interfaces.services.VerificationTokenService;
import ar.edu.itba.paw.models.VerificationToken;
import ar.edu.itba.paw.services.generics.GenericServiceImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

@Service
public class VerificationTokenServiceImpl extends GenericServiceImpl<VerificationTokenDao, VerificationToken, Integer> implements VerificationTokenService {
    private static final int RANDOM_LENGTH = 256;

    @Autowired
    private VerificationTokenDao repository;

    public VerificationTokenServiceImpl() {
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return this.repository.findByToken(token);
    }

    // Should not be able to manually update
    @Override
    public void update(VerificationToken model) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VerificationToken generate() {
        VerificationToken verificationToken = new VerificationToken();
        this.generateAndSaveToken(verificationToken, vt -> {
            vt = this.create(vt);
            verificationToken.setId(vt.getId());
        });
        return verificationToken;
    }

    @Override
    public String refresh(VerificationToken verificationToken) {
        this.generateAndSaveToken(verificationToken, this::secureUpdate);
        return verificationToken.getToken();
    }

    @Override
    protected VerificationTokenDao getRepository() {
        return this.repository;
    }

    private void secureUpdate(VerificationToken verificationToken) {
        super.update(verificationToken);
    }

    private void generateAndSaveToken(VerificationToken verificationToken, Consumer<VerificationToken> saver) {
        boolean set = false;
        int tries = 10;

        do {
            try {
                verificationToken.setToken(RandomStringUtils.random(RANDOM_LENGTH, true, false));
                verificationToken.setCreatedDate(DateTime.now());
                saver.accept(verificationToken);
                set = true;
            } catch (MediCareException ignored) {
                tries--;
            }
        } while (!set && tries > 0);

        if (!set)
            throw new MediCareException("");
    }
}
