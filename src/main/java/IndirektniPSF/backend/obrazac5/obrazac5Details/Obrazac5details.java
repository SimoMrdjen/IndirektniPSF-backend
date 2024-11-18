package IndirektniPSF.backend.obrazac5.obrazac5Details;

import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "obrazac")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Obrazac5details {
    //Entity is inherited from existing table/DB , which is used from another desktop app

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer redni;

    @ManyToOne
//            ( cascade={CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name="gen_mysql")
    private Obrazac5 obrazac5;

    @Column(name = "gen_interbase")
    private Integer gen_interbase;

    @Column(name = "verzija")
    private Integer verzija;

    @Column(name = "koji_kvartal")
    private Integer koji_kvartal;

    @Column(name = "sif_sekret")
    private Integer sif_sekret;

    @Column(name = "sif_rac")
    private Integer sif_rac = 1;

    @Column(name = "razdeo")
    private Integer razdeo;

    @Column(name = "oznakaop")
    private Integer oznakaop;

    @Column(name = "dinarski")
    private Integer dinarski = 1;

    @Column(name = "konto")
    private Integer konto;

    @Column(name = "opis")
    private String opis;

    @Column(name = "planprihoda")
    private Double planprihoda;

    @Column(name = "republika")
    private Double republika = 0.0;

    @Column(name = "pokrajina")
    private Double pokrajina = 0.0;

    @Column(name = "opstina")
    private Double opstina = 0.0;

    @Column(name = "ooso")
    private Double ooso = 0.0;

    @Column(name = "donacije")
    private Double donacije = 0.0;

    @Column(name = "ostali")
    private Double ostali = 0.0;

    @Column(name = "godplan")
    private Double godplan;

    @Column(name = "kvplan")
    private Double kvplan = 0.0;

    @Column(name = "izvrsenje")
    private Double izvrsenje;

    @Column(name = "rep_b")
    private Double rep_b = 0.0;

    @Column(name = "pok_b")
    private Double pok_b = 0.0;

    @Column(name = "ops_b")
    private Double ops_b = 0.0;

    @Column(name = "ooso_b")
    private Double ooso_b = 0.0;

    @Column(name = "dona_b")
    private Double dona_b = 0.0;

    @Column(name = "ost_b")
    private Double ost_b = 0.0;

    @Column(name = "izvrs_bit")
    private Double izvrs_bit = 0.0;

    @Column(name = "izvrs_sop")
    private Double izvrs_sop = 0.0;

    @Column(name = "unosio")
    private Integer unosio;

    @Column(name = "za_unos")
    private Integer za_unos = 1;

    @Column(name = "tip_obrazca")
    private Integer tip_obrazca = 5;

    @Column(name = "jbbk_ind_kor")
    private Integer jbbk_ind_kor;

    @Column(name = "nivo_konsolidacije")
    private Integer nivo_konsolidacije = 0;

//    public static List<Obrazac5details> difference(List<Obrazac5details> detailsObr5, List<Obrazac5details> detailsFromObrIO) {
//       for (Obrazac5details obr5 : detailsObr5) {
//           for (Obrazac5details obrIO : detailsFromObrIO) {
//               if(obr5.getKonto() == obrIO.getKonto()) {
//                   obr5.setRepublika( obr5.getRepublika() - obrIO.getRepublika());
//                   obr5.setPokrajina( obr5.getPokrajina() - obrIO.getPokrajina());
//                   obr5.setOpstina( obr5.getOpstina() - obrIO.getOpstina());
//                   obr5.setOoso( obr5.getOoso() - obrIO.getOoso());
//                   obr5.setDonacije( obr5.getDonacije() - obrIO.getDonacije());
//                   obr5.setOstali( obr5.getOstali() - obrIO.getOstali());
//               }
//           }
//       }
//       return detailsObr5;
//    }
    public static List<Obrazac5details> difference(List<Obrazac5details> detailsObr5, List<Obrazac5details> detailsFromObrIO) {
     Map<Integer, Obrazac5details> detailsFromObrIOMap = detailsFromObrIO.stream()
                .collect(Collectors.toMap(Obrazac5details::getKonto, obj -> obj));

      return detailsObr5.stream()
                .map(obr5 -> {
                   Obrazac5details matchingObrIO = detailsFromObrIOMap.get(obr5.getKonto());
                  if (matchingObrIO != null) {
                      return subtractDetails(obr5, matchingObrIO);
                   }
                  return obr5;
                })
                .toList();
    }

    private static Obrazac5details subtractDetails(Obrazac5details obr5, Obrazac5details obrIO) {
        Obrazac5details result = new Obrazac5details();
        result.setKonto(obr5.getKonto());
        result.setRepublika((obr5.getRepublika() != null ? obr5.getRepublika() : 0.0) -
                (obrIO.getRepublika() != null ? obrIO.getRepublika() : 0.0));
        result.setPokrajina((obr5.getPokrajina() != null ? obr5.getPokrajina() : 0.0) -
                (obrIO.getPokrajina() != null ? obrIO.getPokrajina() : 0.0));
        result.setOpstina((obr5.getOpstina() != null ? obr5.getOpstina() : 0.0) -
                (obrIO.getOpstina() != null ? obrIO.getOpstina() : 0.0));
        result.setOoso((obr5.getOoso() != null ? obr5.getOoso() : 0.0) -
                (obrIO.getOoso() != null ? obrIO.getOoso() : 0.0));
        result.setDonacije((obr5.getDonacije() != null ? obr5.getDonacije() : 0.0) -
                (obrIO.getDonacije() != null ? obrIO.getDonacije() : 0.0));
        result.setOstali((obr5.getOstali() != null ? obr5.getOstali() : 0.0) -
                (obrIO.getOstali() != null ? obrIO.getOstali() : 0.0));
        return result;
    }

}
