package ar.edu.itba.paw.webapp.media_types.parsers.utils;

import ar.edu.itba.paw.webapp.exceptions.APIErrorFactory;
import ar.edu.itba.paw.webapp.models.error.ErrorConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.IOException;
import java.io.InputStream;

public abstract class ParserUtils {
    // Devuelve un ObjectNode o ArrayNode
    public static JsonNode inputToJSON(InputStream inputStream) throws IOException, BadRequestException {
        // Tratamos de parsearlo
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(inputStream);
        } catch (JsonProcessingException e) {
            throw new BadRequestException(
                    Response
                            .status(Status.BAD_REQUEST)
                            .entity(
                                    APIErrorFactory
                                            .buildError(Status.BAD_REQUEST)
                                            .withReason(ErrorConstants.MISSING_INVALID_JSON_BODY)
                                            .build()
                            )
                            .build()
            );
        }
    }

    // Devuelve un ObjectNode o ArrayNode
    public static JsonNode stringToJson(String input) throws IOException, BadRequestException {
        // Tratamos de parsearlo
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(input);
        } catch (JsonProcessingException e) {
            throw new BadRequestException(
                    Response
                            .status(Status.BAD_REQUEST)
                            .entity(
                                    APIErrorFactory
                                            .buildError(Status.BAD_REQUEST)
                                            .withReason(ErrorConstants.MISSING_INVALID_JSON_BODY)
                                            .build()
                            )
                            .build()
            );
        }
    }
}
