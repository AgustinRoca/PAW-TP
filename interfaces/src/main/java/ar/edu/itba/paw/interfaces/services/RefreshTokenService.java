package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.interfaces.services.generic.GenericService;
import ar.edu.itba.paw.models.RefreshToken;
import ar.edu.itba.paw.models.User;

import java.util.Optional;

public interface RefreshTokenService extends GenericService<RefreshToken, Integer> {
    Optional<RefreshToken> findByToken(String token);

    String generate(User user);

    String refresh(String refreshToken);

    void removeByToken(String token);

    void removeByUserId(Integer userId);

    void deleteOldRefreshTokens();

    void scheduleDeleteRefreshTokens();
}
