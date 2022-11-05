package ar.edu.itba.paw.services;

import ar.edu.itba.paw.interfaces.MediCareException;
import ar.edu.itba.paw.interfaces.daos.RefreshTokenDao;
import ar.edu.itba.paw.interfaces.services.RefreshTokenService;
import ar.edu.itba.paw.models.RefreshToken;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.services.generics.GenericServiceImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
public class RefreshTokenServiceImpl extends GenericServiceImpl<RefreshTokenDao, RefreshToken, Integer> implements RefreshTokenService {
    private static final int RANDOM_LENGTH = 256;
    private static final int DAYS_VALID = 7;

    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshTokenServiceImpl.class);

    @Autowired
    private RefreshTokenDao repository;

    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1);

    public RefreshTokenServiceImpl() {
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return this.repository.findByToken(token);
    }

    // Should not be able to manually update
    @Override
    public void update(RefreshToken model) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String generate(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        this.generateAndSaveToken(refreshToken, rt -> {
            rt = this.create(rt);
            refreshToken.setId(rt.getId());
        });
        return refreshToken.getToken();
    }

    @Override
    public String refresh(String refreshToken) {
        Optional<RefreshToken> refreshTokenModel = this.findByToken(refreshToken);
        if (!refreshTokenModel.isPresent())
            throw new IllegalArgumentException();

        refreshTokenModel.get().setToken(refreshToken);

        this.generateAndSaveToken(refreshTokenModel.get(), this::secureUpdate);
        return refreshTokenModel.get().getToken();
    }

    @Override
    public void removeByToken(String token) {
        this.repository.removeByToken(token);
    }

    @Override
    public void removeByUserId(Integer userId) {
        this.repository.removeByUserId(userId);
    }

    @Override
    protected RefreshTokenDao getRepository() {
        return this.repository;
    }

    private void secureUpdate(RefreshToken refreshToken) {
        super.update(refreshToken);
    }

    private void generateAndSaveToken(RefreshToken refreshToken, Consumer<RefreshToken> saver) {
        boolean set = false;
        int tries = 10;

        do {
            try {
                refreshToken.setToken(RandomStringUtils.random(RANDOM_LENGTH, true, false));
                refreshToken.setCreatedDate(LocalDateTime.now());
                saver.accept(refreshToken);
                set = true;
            } catch (MediCareException ignored) {
                tries--;
            }
        } while (!set && tries > 0);

        if (!set)
            throw new MediCareException("");
    }

    @PostConstruct
    @Async
    @Override
    public void scheduleDeleteRefreshTokens(){
        scheduler.scheduleAtFixedRate(this::deleteOldRefreshTokens, 0, 1, TimeUnit.DAYS);
    }

    @Override
    public void deleteOldRefreshTokens() {
        int tokensDeleted = repository.removeTokensOlderThan(DAYS_VALID);
        LOGGER.info("Deleted {} refresh tokens from the database that were at least {} days old", tokensDeleted, DAYS_VALID);
    }
}
