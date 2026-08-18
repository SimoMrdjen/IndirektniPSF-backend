package IndirektniPSF.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

/**
 * Na osnovu "kvartal" parametra iz zahteva (path variable ili request param -
 * u celom kodu se koristi iskljucivo jedno od ta dva, videti kontrolere) bira
 * koja baza (APV/PGODINA) vazi za ceo taj zahtev, i upisuje to u
 * {@link KvartalDataSourceContextHolder} pre nego sto zahtev stigne do
 * kontrolera/servisa.
 */
@Component
public class KvartalRoutingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Integer kvartal = extractKvartal(request);
        KvartalDataSourceContextHolder.set(resolveDataSourceType(kvartal));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Obavezno ciscenje - servlet kontejner ponovo koristi niti iz thread poola.
        KvartalDataSourceContextHolder.clear();
    }

    DataSourceType resolveDataSourceType(Integer kvartal) {
        if (kvartal != null && (kvartal == 4 || kvartal == 5)) {
            return DataSourceType.PGODINA;
        }
        return DataSourceType.APV;
    }

    Integer extractKvartal(HttpServletRequest request) {
        String param = request.getParameter("kvartal");
        if (param != null) {
            return parseOrNull(param);
        }

        Object pathVariables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables instanceof Map<?, ?> variables) {
            Object value = variables.get("kvartal");
            if (value != null) {
                return parseOrNull(value.toString());
            }
        }
        return null;
    }

    private Integer parseOrNull(String value) {
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
