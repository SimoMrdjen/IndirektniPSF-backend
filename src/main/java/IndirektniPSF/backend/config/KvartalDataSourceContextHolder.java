package IndirektniPSF.backend.config;

/**
 * Cuva koju bazu (APV/PGODINA) treba koristiti za tekuci HTTP zahtev.
 * Postavlja ga {@link KvartalRoutingInterceptor} na pocetku zahteva (na
 * osnovu "kvartal" parametra iz URL-a), a {@link RoutingDataSource} ga
 * cita kad Hibernate/HikariCP zatrazi konekciju.
 * <p>
 * MORA se obavezno cistiti na kraju zahteva (vidi afterCompletion u
 * interceptoru) jer servlet kontejner ponovo koristi niti iz thread poola
 * za druge zahteve - u suprotnom bi vrednost mogla da "procuri" u sledeci,
 * nepovezan zahtev na istoj niti.
 */
public final class KvartalDataSourceContextHolder {

    private static final ThreadLocal<DataSourceType> CONTEXT = new ThreadLocal<>();

    private KvartalDataSourceContextHolder() {
    }

    public static void set(DataSourceType dataSourceType) {
        CONTEXT.set(dataSourceType);
    }

    public static DataSourceType get() {
        DataSourceType dataSourceType = CONTEXT.get();
        return dataSourceType != null ? dataSourceType : DataSourceType.APV;
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
