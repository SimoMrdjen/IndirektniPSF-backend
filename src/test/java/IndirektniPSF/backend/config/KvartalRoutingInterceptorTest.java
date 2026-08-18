package IndirektniPSF.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KvartalRoutingInterceptorTest {

    private final KvartalRoutingInterceptor interceptor = new KvartalRoutingInterceptor();

    @AfterEach
    void tearDown() {
        KvartalDataSourceContextHolder.clear();
    }

    @Test
    void resolvesApvForKvartal1_2_3() {
        assertEquals(DataSourceType.APV, interceptor.resolveDataSourceType(1));
        assertEquals(DataSourceType.APV, interceptor.resolveDataSourceType(2));
        assertEquals(DataSourceType.APV, interceptor.resolveDataSourceType(3));
    }

    @Test
    void resolvesPgodinaForKvartal4_5() {
        assertEquals(DataSourceType.PGODINA, interceptor.resolveDataSourceType(4));
        assertEquals(DataSourceType.PGODINA, interceptor.resolveDataSourceType(5));
    }

    @Test
    void resolvesApvWhenKvartalMissingOrUnexpected() {
        assertEquals(DataSourceType.APV, interceptor.resolveDataSourceType(null));
        assertEquals(DataSourceType.APV, interceptor.resolveDataSourceType(0));
        assertEquals(DataSourceType.APV, interceptor.resolveDataSourceType(99));
    }

    @Test
    void extractsKvartalFromRequestParam() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("kvartal")).thenReturn("4");

        assertEquals(4, interceptor.extractKvartal(request));
    }

    @Test
    void extractsKvartalFromPathVariableWhenNoRequestParam() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("kvartal")).thenReturn(null);
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("kvartal", "5"));

        assertEquals(5, interceptor.extractKvartal(request));
    }

    @Test
    void extractsNullWhenKvartalNotPresentAnywhere() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("kvartal")).thenReturn(null);
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);

        assertNull(interceptor.extractKvartal(request));
    }

    @Test
    void extractsNullWhenKvartalNotNumeric() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("kvartal")).thenReturn("abc");

        assertNull(interceptor.extractKvartal(request));
    }

    @Test
    void preHandleRoutesToPgodinaForKvartal4() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("kvartal")).thenReturn("4");

        interceptor.preHandle(request, null, null);

        assertEquals(DataSourceType.PGODINA, KvartalDataSourceContextHolder.get());
    }

    @Test
    void preHandleRoutesToApvWhenKvartalAbsent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("kvartal")).thenReturn(null);
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);

        interceptor.preHandle(request, null, null);

        assertEquals(DataSourceType.APV, KvartalDataSourceContextHolder.get());
    }

    @Test
    void afterCompletionClearsContext() {
        KvartalDataSourceContextHolder.set(DataSourceType.PGODINA);

        interceptor.afterCompletion(null, null, null, null);

        assertEquals(DataSourceType.APV, KvartalDataSourceContextHolder.get());
    }
}
