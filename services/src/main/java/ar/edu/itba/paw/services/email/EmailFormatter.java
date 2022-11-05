package ar.edu.itba.paw.services.email;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class EmailFormatter implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        EmailFormatter.applicationContext = applicationContext;
    }

    private static final String HEAD =
            "<head>" +
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" +
                    "<title>MediCare</title>" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>" +
                    "</head>";

    public String format(String body) {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">" +
                "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
                HEAD +
                "<body style=\"margin: 0; padding: 0;\">" +
                body +
                "</body>" +
                "</html>";
    }

    public String getHTMLFromFilename(String filename) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStreamReader streamReader = new InputStreamReader(Objects.requireNonNull(classLoader.getResourceAsStream(filename)), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);

        StringBuilder stringBuilder = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
}
