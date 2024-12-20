package IndirektniPSF.backend.arhbudzet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArhbudzetRepository extends JpaRepository<Arhbudzet, ArhbudzetId> {

//    @Query("SELECT SUM(a.duguje) FROM Arhbudzet a JOIN izvor i WHERE a.sinKonto > 4000 AND a.sinKonto < 7000 " +
//            "AND a.sifSekr = :sifSekr AND a.datum <= :date " +
//            "AND a.jbbkIndKor = :jbbk AND i.kakva = 'budz'")
//    Double sumUplataIzBudzetaForIndKor(Integer sifSekr, Double date,  Integer jbbk);
    @Query(value = "SELECT SUM(a.duguje) " +
        "FROM Arhbudzet a " +
        "JOIN izvor i ON a.IZVORFIN = i.IZVORFIN " +
        "WHERE a.SIN_KONTO > 4000 " +
        "AND a.SIN_KONTO < 7000 " +
        "AND a.SIF_SEKRET = :sifSekr " +
        "AND a.datum <= :date " +
        "AND a.JBBK_IND_KOR = :jbbk "
            + "AND i.kakva = 'budz'"
            ,
        nativeQuery = true)
    Double sumUplataIzBudzetaForIndKor(@Param("sifSekr") Integer sifSekr,
                                   @Param("date") Double date,
                                   @Param("jbbk") Integer jbbk);

    @Query(value = "SELECT SUM(a.duguje) " +
            "FROM Arhbudzet a " +
//        "JOIN izvor i ON a.IZVORFIN = i.IZVORFIN " +
            "WHERE a.SIN_KONTO > 4000 " +
            "AND a.SIN_KONTO < 7000 " +
            "AND a.SIF_SEKRET = :sifSekr " +
            "AND a.datum <= :date " +
            "AND a.JBBK_IND_KOR = :jbbk "
//            + "AND i.kakva = 'budz'"
            ,
            nativeQuery = true)
    Double sumUplataIzBudzetaForIndKorForObr5(@Param("sifSekr") Integer sifSekr,
                                       @Param("date") Double date,
                                       @Param("jbbk") Integer jbbk);

    @Query("SELECT a FROM Arhbudzet a " +
            "WHERE a.vrstaPromene BETWEEN 500 AND 599 " +
            "AND a.jbbkIndKor = :jbbkIndKor " +
            "AND a.datum <= :datum " +
            "GROUP BY a.redBrojAkt, a.funkKlas, a.sinKonto, a.izvor")
    List<Arhbudzet> findDistinctByJbbkIndKorAndSifSekrAndVrstaPromene(
            @Param("jbbkIndKor") Integer jbbkIndKor,
            @Param("datum") Double datum);

    @Query("SELECT new Arhbudzet(a.sinKonto, a.izvor, a.redBrojAkt, a.funkKlas, SUM(a.duguje + a.duggtbr)) " +
            "FROM Arhbudzet a " +
            "WHERE a.jbbkIndKor = :jbbkIndKor AND a.datum <= :datum " +
            "AND a.sinKonto > 3999 AND a.sinKonto < 7000 " +
            "GROUP BY a.sinKonto, a.redBrojAkt, a.izvor, a.funkKlas")
    List<Arhbudzet> findByJbbkIndKorAndDatumLessThanEqualGroupByFields(
            @Param("jbbkIndKor") Integer jbbkIndKor,
            @Param("datum") Double datum);

    @Query(value = "SELECT SUM(a.duguje) " +
            "FROM Arhbudzet a " +
            "WHERE a.SIN_KONTO > 4000 " +
            "AND a.SIN_KONTO < 7000 " +
            "AND a.SIF_SEKRET = :sifSekr " +
            "AND a.datum <= :date " +
            "AND a.JBBK_IND_KOR = :jbbk " +
            "AND a.izvorfin in ('0100','0102','0112'," +
            "'0701','0707','0708','0709','0710','0711','0713'," +
            "'0912','1000','1100','1204','1205','1206','1300'," +
            "'1302','1312','1400','1700')",
            nativeQuery = true)
    Double sumUplataIzBudzetaForIndKorForIzvoriFin(
            @Param("sifSekr") Integer sifSekr,
            @Param("date") Double date,
            @Param("jbbk") Integer jbbk);
}
