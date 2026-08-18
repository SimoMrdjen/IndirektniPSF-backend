package IndirektniPSF.backend.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KvartalDataSourceContextHolderTest {

    @AfterEach
    void tearDown() {
        KvartalDataSourceContextHolder.clear();
    }

    @Test
    void defaultsToApvWhenNothingSet() {
        assertEquals(DataSourceType.APV, KvartalDataSourceContextHolder.get());
    }

    @Test
    void returnsWhateverWasSet() {
        KvartalDataSourceContextHolder.set(DataSourceType.PGODINA);
        assertEquals(DataSourceType.PGODINA, KvartalDataSourceContextHolder.get());
    }

    @Test
    void defaultsToApvAgainAfterClear() {
        KvartalDataSourceContextHolder.set(DataSourceType.PGODINA);
        KvartalDataSourceContextHolder.clear();
        assertEquals(DataSourceType.APV, KvartalDataSourceContextHolder.get());
    }
}
