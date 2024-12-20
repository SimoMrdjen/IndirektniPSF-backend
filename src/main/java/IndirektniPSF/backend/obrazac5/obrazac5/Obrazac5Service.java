package IndirektniPSF.backend.obrazac5.obrazac5;

import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIO;
import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIORepository;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetailService;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetails;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetailsRepository;
import IndirektniPSF.backend.arhbudzet.ArhbudzetService;
import IndirektniPSF.backend.excel.ExcelService;
import IndirektniPSF.backend.exceptions.ObrazacException;
import IndirektniPSF.backend.glavaSvi.GlavaSviService;
import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import IndirektniPSF.backend.obrazac5.obrazac5Details.Obrazac5Mapper;
import IndirektniPSF.backend.obrazac5.obrazac5Details.Obrazac5DetailsService;
import IndirektniPSF.backend.obrazac5.obrazac5Details.Obrazac5details;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.obrazac5.sekretarijat.Sekretarijat;
import IndirektniPSF.backend.parameters.*;
import IndirektniPSF.backend.raspodela.Raspodela;
import IndirektniPSF.backend.raspodela.RaspodelaService;
import IndirektniPSF.backend.review.ObrazacResponse;
import IndirektniPSF.backend.review.ObrazacType;
import IndirektniPSF.backend.security.user.User;
import IndirektniPSF.backend.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

//--5--
@RequiredArgsConstructor
@Service
@Component
public class Obrazac5Service extends AbParameterService implements IfObrazacChecker, IfObrazacService<Obrazac5> {
    private final Obrazac5Repository obrazacRepository;
    private final SekretarijarService sekretarijarService;
    private final Obrazac5DetailsService obrazac5DetailsService;
    private final PPartnerService pPartnerService;
    private final UserRepository userRepository;
    private StringBuilder responseMessage = new StringBuilder();
    private final Obrazac5Mapper mapper;
    private final ObrazacIORepository obrazacIOrepository;
    private final StatusService statusService;
    private final ExcelService excelService;
    private final ArhbudzetService arhbudzetService;
    private final GlavaSviService glavaSviService;
    private final RaspodelaService raspodelaService;
    private final ObrazacIODetailsRepository obrazacIODetailsRepository;
    private final ObrazacIODetailService obrazacIODetailsService;


    //--5--
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public StringBuilder saveObrazacFromExcel(MultipartFile file, Integer kvartal, String email) throws Exception {

        responseMessage.delete(0, responseMessage.length());
        User user = this.getUser(email);
        var jbbk = getJbbksIBK(user);

        //GET DATA FROM EXCEL
        Double prihodiFromPokrajinaFromExcel =
                excelService.readCellOfDoubleValueByIndexes(file.getInputStream(), 195, 6);
        Double konto791100FromExcel =
                excelService.readCellOfDoubleValueByIndexes(file.getInputStream(), 129, 6);

        //GET DATA FROM ARGUMENTS
        Integer sifSekret = user.getZa_sif_sekret();
        Sekretarijat sekretarijat = sekretarijarService.getSekretarijat(sifSekret);
        Integer today = (int) LocalDate.now().toEpochDay() + 25569;
        String oznakaGlave = glavaSviService.findGlava(jbbk);
        ObrazacIO validIO = this.findValidIO(kvartal, jbbk);//greska 43
        Integer version = checkIfExistValidObrazac5AndFindVersion(jbbk, kvartal);
        List<Obrazac5DTO> dtos = mapper.mapExcelToPojo(file.getInputStream());

        //VARIOUS CHECKS
      //  chekIfKvartalIsCorrect(kvartal, excelKvartal, year);//TODO implement this
        checkPrihodFromPokrajinaInObrazacAgainstDataInArhBudzet(
                prihodiFromPokrajinaFromExcel, kvartal, jbbk, oznakaGlave, sifSekret);//greska 44
        checkKonto791100InObrazacAgainstDataInArhBudzet(
                konto791100FromExcel, kvartal, jbbk, sifSekret);//greska 45
        compareTroskoviForSinKontosIOAgainstOBr5(dtos, validIO);

        //INITILIZATION AND PERSISTANCE OF MASTER OBJECT
        Obrazac5 zb = Obrazac5.builder()
                //.gen_interbase(1)
                .koji_kvartal(kvartal)
                .tip_obrazca(5)
                .sif_sekret(sifSekret)
                .razdeo(sekretarijat.getRazdeo())
                .sif_rac(1)
                .verzija(version)
                .dinarski(1)
                .status(0)
                .poslato_o(0)
                .RADNA(1)
                .povuceno(0)
                .konacno(0)
                .poslao_nam(user.getSifraradnika())
                .poslao_u_org(0)
                .poslao_iz_org(0)
                .zaprimio_ver(0)
                .overio_ver(0)
                .odobrio_ver(0)
                .proknjizeno(0)
                .jbbk_ind_kor(jbbk)
                .storno(0)
                .stosifrad(0)
                .opisstorno("")
                .podigao_status(0)
                .datum_pod_statusa(0)
                .datum_org(0)
                .nivo_konsolidacije(0)
                .build();

        Obrazac5 zbSaved = obrazacRepository.save(zb);
        List<Obrazac5details> detailsObr5 = obrazac5DetailsService.saveDetailsExcel(dtos, zbSaved, validIO);
        completeColumnInObrIODetailsUsingDataFromObr5(detailsObr5, validIO);
        return responseMessage;
    }

    protected void compareTroskoviForSinKontosIOAgainstOBr5(List<Obrazac5DTO> dtos, ObrazacIO validIO) {
        Map<Integer, Double> mapObr5 = transformObr5ToMap(dtos);
        Map<Integer, Double> mapObrIo = transformObrIoToMap(validIO);

        checkIfExistDiffernciesBetweenTroskoviForSinKontosIOAndOBr5(mapObr5, mapObrIo);
    }

    protected void checkIfExistDiffernciesBetweenTroskoviForSinKontosIOAndOBr5(
            Map<Integer, Double> mapObr5, Map<Integer, Double> mapObrIo) {

        StringBuilder exMess = new StringBuilder("Neuspešno učitavanje! \n");

        if (!mapObr5.keySet().equals(mapObrIo.keySet())) {
            var missingInObr5 = mapObrIo.keySet().stream()
                    .filter(key -> !mapObr5.containsKey(key))
                    .toList();
            var missingInObrIo = mapObr5.keySet().stream()
                    .filter(key -> !mapObrIo.containsKey(key))
                    .toList();

            if (!missingInObr5.isEmpty())
                exMess.append("Sin. konto " + missingInObr5.toString() + "\n postoji u Obrascu IO ali ne i u Obrascu 5!\n");

            if (!missingInObrIo.isEmpty())
                exMess.append("Sin. konto " + missingInObrIo.toString() + "\n postoji u Obrascu 5 ali ne i u Obrascu IO!\n");

            throw new IllegalArgumentException(String.valueOf(exMess));
        }

        // Compare values for the same keys
        mapObr5.forEach((key, valueObr5) -> {
            Double valueObrIo = mapObrIo.get(key);
            if (!areEqual(valueObr5, valueObrIo)) {
                exMess.append(String.format(
                        "Postoje razlike u sin.kontu: %d: Obr5=%.2f, ObrIo=%.2f%n \n",
                        key, valueObr5, valueObrIo
                ));
                throw new IllegalArgumentException(exMess.toString());
            }
        });
    }

    protected Map<Integer, Double> transformObr5ToMap(List<Obrazac5DTO> dtos) {
        return dtos.stream()
                .filter(dto -> dto.getKonto() % 1000 != 0)
                .filter(dto -> dto.getPlanPrihoda() != 0 || dto.getIzvrsenje() != 0)
                .collect(Collectors.toMap(
                        Obrazac5DTO::getKonto,
                        Obrazac5DTO::getIzvrsenje,
                        Double::sum
                ));
    }

    protected Map<Integer, Double> transformObrIoToMap( ObrazacIO validIO) {

        return validIO.getStavke().stream()
                .filter(a -> a.getSIN_KONTO() > 3999)
                .map(a -> {
                    ObrazacIODetails newDetail = new ObrazacIODetails();
                    newDetail.setSIN_KONTO(a.getSIN_KONTO() * 100);
                    newDetail.setDUGUJE(a.getDUGUJE() + a.getPOTRAZUJE());
                    // Copy other fields if necessary
                    return newDetail;
                })
                .collect(Collectors.toMap(
                        ObrazacIODetails::getSIN_KONTO,
                        ObrazacIODetails::getDUGUJE,
                        Double::sum
                ));
    }

    public void completeColumnInObrIODetailsUsingDataFromObr5(List<Obrazac5details> detailsObr5, ObrazacIO validIO) {

        List<ObrazacIODetails> ioDetailsEmptyPrihodiColumns =
                getIoDetailsEmptyPrihodiColumns(validIO);//nepopunjeni io

        List<Obrazac5details> detailsFromObrIO =
                mapper.mapIOtoObr5(validIO.getStavke());//lista stavki io pretvorena u obr5
        //i agregirana po sin kontu

        List<Obrazac5details> differnciesBetweenObrIOAndObr5 =
                getDifferenceBetweenPrihodiFromIoAgainstObr5(detailsObr5, detailsFromObrIO);//lista viska prihoda iz obr5

        allocateExpensesByIncomeSource(ioDetailsEmptyPrihodiColumns, differnciesBetweenObrIOAndObr5);

    }

    //TODO naci nerasporedjen deo u IO i svisak smestiti u objekte lista 5
    List<Obrazac5details> getDifferenceBetweenPrihodiFromIoAgainstObr5(List<Obrazac5details> detailsObr5,
                                                                       List<Obrazac5details> detailsFromObrIO) {
        return Obrazac5details.difference(detailsObr5, detailsFromObrIO)
                .stream()
                .filter(this::isNotEmptyPrihod).toList();
    }

    private boolean isNotEmptyPrihod(Obrazac5details a) {
        return !areEqual(a.getOstali(), 0.0) || !areEqual(a.getRepublika(), 0.0)
                || !areEqual(a.getPokrajina(), 0.0) || !areEqual(a.getOpstina(), 0.0)
                || !areEqual(a.getDonacije(), 0.0) || !areEqual(a.getOoso(), 0.0);
    }

    //TODO naci listu stavki IO koje nisu popunjene
    List<ObrazacIODetails> getIoDetailsEmptyPrihodiColumns(ObrazacIO validIO) {
        return validIO.getStavke().stream()
                .filter(this::isNotEqualDugujeAndSumOfIzvori)
                .toList();
    }

    //TODO proci kroz IO i rasporediti za izvore koji nisu jdnoznacni
    public void allocateExpensesByIncomeSource(List<ObrazacIODetails> ioDetailsEmptyPrihodiColumns, List<Obrazac5details> differnciesBetweenObrIOAndObr5) {

        //TODO naci izvore koji nemaju jednoznacni raspored
        List<Raspodela> raspodelas = raspodelaService.findIzvorFinIfNotUnique();

        //TODO popuniti prazne stavke
        populateEmptyIzvoriIO(ioDetailsEmptyPrihodiColumns, differnciesBetweenObrIOAndObr5, raspodelas);

        //TODO snimiti stavke - persistance
        obrazacIODetailsService.saveAll(ioDetailsEmptyPrihodiColumns);
    }

    public void populateEmptyIzvoriIO(List<ObrazacIODetails> ioDetailsEmptyPrihodiColumns,
                                       List<Obrazac5details> differnciesBetweenObrIOAndObr5, List<Raspodela> raspodelas) {

        //TODO proci kroz listu emptyIzvoriDetailsIO i popuniti kolonu prihodA u zavisnosti od izvora i
        // umanjiti differnciesBetweenObrIOAndObr5
        ioDetailsEmptyPrihodiColumns.stream()
                .forEach(io -> populateColumnPrihodiInIO(io, differnciesBetweenObrIOAndObr5, raspodelas));
    }

    public void populateColumnPrihodiInIO(ObrazacIODetails ioEmptyPrihodiColumns,
                                   List<Obrazac5details> differnciesBetweenObrIOAndObr5,
                                   List<Raspodela> raspodelas) {

        // TODO objekat razlike sa kolonama
        Obrazac5details singleDifferencies = findProperDifferenceAccordingSinKonto(differnciesBetweenObrIOAndObr5,
                ioEmptyPrihodiColumns.getSIN_KONTO());
        //TODO popuniti polja prihoda u zavisnosti od izvora, i umanjiti raspolozivo
        //TODO izmeniti tako da se radi sa listom raspodela u zvisnosti od izvora
        List<Raspodela> raspodelasForParticularIzvor =
                getRaspodelasForParticularIzvor(ioEmptyPrihodiColumns.getIZVORFIN(), raspodelas);

        for (Raspodela raspodela : raspodelasForParticularIzvor) {
                //TODO naci u koju kolonu treba upisati iznos i umanjiti diff iznos
                populateColumnPrihodiInIOAccordingIzvorFin(raspodela, ioEmptyPrihodiColumns, singleDifferencies);
            }
    }


    public List<Raspodela> getRaspodelasForParticularIzvor(String izvor, List<Raspodela> raspodelas) {
        return raspodelas.stream()
                .filter(a -> a.getIzvorFin().equals(izvor)).toList();
    }

    //TODO
    public void populateColumnPrihodiInIOAccordingIzvorFin(Raspodela raspodela,
                                                            ObrazacIODetails ioEmptyPrihodiColumns,
                                                            Obrazac5details singleDifferencies) {
       //TODO set x kao vrednost duguje - rasporedeno
        double x = (ioEmptyPrihodiColumns.getDUGUJE() + ioEmptyPrihodiColumns.getPOTRAZUJE()) - sumOfIzvori(ioEmptyPrihodiColumns);
        double y= 0.0;
        var kolona = raspodela.getKolona();
        if (kolona == 6) {
            y = singleDifferencies.getRepublika();
            //TODO ako ima slobodnih sredstava u obr5 za tu kolonu
            if (x < y || areEqual(x, y)) {
                ioEmptyPrihodiColumns.setREPUBLIKA(x);
                singleDifferencies.setRepublika(y-x);
                //TODO ukoliko je manje sredstava u obr5 u toj koloni nego sto je potrbno
            } else if ( !areEqual(y,0.0)) {
                ioEmptyPrihodiColumns.setREPUBLIKA(y);
                singleDifferencies.setRepublika(0.0);
            }
            //TODO kolona Pokrajina
        } else if (kolona == 7) {
            y = singleDifferencies.getPokrajina();
            if (x < y || areEqual(x, y)) {
                ioEmptyPrihodiColumns.setPOKRAJINA(x);
                singleDifferencies.setPokrajina(y-x);
            } else if ( !areEqual(y,0.0)) {
                ioEmptyPrihodiColumns.setPOKRAJINA(y);
                singleDifferencies.setPokrajina(0.0);
            }
            //TODO kolona OPSTINA
        } else if (kolona == 8) {
            y = singleDifferencies.getOpstina();
            if (x < y || areEqual(x, y)) {
                ioEmptyPrihodiColumns.setOPSTINA(x);
                singleDifferencies.setOpstina(y-x);
            } else if ( !areEqual(y,0.0)) {
                ioEmptyPrihodiColumns.setOPSTINA(y);
                singleDifferencies.setOpstina(0.0);
            }
            //TODO kolona DONACIJE
        } else if (kolona == 10) {
            y = singleDifferencies.getDonacije();
            if (x < y || areEqual(x, y)) {
                ioEmptyPrihodiColumns.setDONACIJE(x);
                singleDifferencies.setDonacije(y-x);
            } else if ( !areEqual(y,0.0)) {
                ioEmptyPrihodiColumns.setDONACIJE(y);
                singleDifferencies.setDonacije(0.0);
            }
            //TODO kolona OOSO
        } else if (kolona == 9) {
            y = singleDifferencies.getOoso();
            if (x < y || areEqual(x, y)) {
                ioEmptyPrihodiColumns.setOOSO(x);
                singleDifferencies.setOoso(y-x);
            } else if ( !areEqual(y,0.0)) {
                ioEmptyPrihodiColumns.setOOSO(y);
                singleDifferencies.setOoso(0.0);
            }
            //TODO kolona OSTALI
        } else if (kolona == 11) {
            y = singleDifferencies.getOstali();
            if (x < y || areEqual(x, y)) {
                ioEmptyPrihodiColumns.setOSTALI(x);
                singleDifferencies.setOstali(y-x);
            } else if ( !areEqual(y,0.0)) {
                ioEmptyPrihodiColumns.setOSTALI(y);
                singleDifferencies.setOstali(0.0);
            }
        }
    }

    public Obrazac5details findProperDifferenceAccordingSinKonto(List<Obrazac5details> differnciesBetweenObrIOAndObr5,
                                                                  Integer sinKonto) {
        var konto = sinKonto * 100;
        for (Obrazac5details singleDifferencies : differnciesBetweenObrIOAndObr5) {
            if (singleDifferencies.getKonto().equals(konto))
                return singleDifferencies;
        }
        //TODO add exception maybe
        return null;
    }

    public boolean isNotEqualDugujeAndSumOfIzvori(ObrazacIODetails io) {
        return !areEqual((io.getDUGUJE() + io.getPOTRAZUJE()), sumOfIzvori(io));
    }

    public double sumOfIzvori(ObrazacIODetails io) {
        return io.getPOKRAJINA() + io.getREPUBLIKA() + io.getOPSTINA() +
                io.getDONACIJE() + io.getOOSO() + io.getOSTALI();
    }

    void checkKonto791100InObrazacAgainstDataInArhBudzet(
            Double konto791100FromExcel, Integer kvartal, Integer jbbk, Integer sifSekret) throws ObrazacException {

        Double date = (double)getLastDayOfKvartal(kvartal).toEpochDay() + 25569;
        Double prihodFromArhBudzet =
                arhbudzetService.sumUplataIzBudzetaForIndKorForIzvoriFin(
                        sifSekret, date,jbbk);
        if (!areEqual(prihodFromArhBudzet, konto791100FromExcel)) {
            throw new ObrazacException("Ne slaže se iznos prenetih sredstava iz evidencije APV \n"  +
                    "(" + prihodFromArhBudzet.doubleValue() + ")" +
                    "sa iznosom primljenih sredstava od  APV  u Excel obrascu \n" +
                    "( " +  konto791100FromExcel.doubleValue() + ") 1"

            );
        }
    }

    void checkPrihodFromPokrajinaInObrazacAgainstDataInArhBudzet(
            Double prihodiFromPokrajinaFromExcel, Integer kvartal,
            Integer jbbk, String oznakaGlave, Integer sifSekret) throws ObrazacException {

        Double date = (double)getLastDayOfKvartal(kvartal).toEpochDay() + 25569;
        Double prihodFromArhBudzet =
                arhbudzetService.sumUplataIzBudzetaForIndKorForObr5(
                        sifSekret, date, oznakaGlave, jbbk);
        if (!areEqual(prihodFromArhBudzet, prihodiFromPokrajinaFromExcel)) {
            throw new ObrazacException("Ne slaže se iznos prenetih sredstava iz evidencije APV \n " +
                    "(" + prihodFromArhBudzet.doubleValue() + ")" +
                    "sa iznosom primljenih sredstava od  APV  u Excel obrascu \n" +
                    "( " +  prihodiFromPokrajinaFromExcel.doubleValue() + ") 2"
            );
        }
    }

    @Override
    public Obrazac5 findObrazacById(Integer id, Integer kvartal) {

       return obrazacRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Obrazac 5  ne postoji!"));
    }

    @Override
    public ObrazacResponse getObrazactWithDetailsForResponseById(Integer id, Integer kvartal) {
        var zb = findObrazacById(id, kvartal);
        List<Obrazac5DTO> details =
                zb.getStavke().stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
        ObrazacResponse response = mapper.toResponse(zb);
        response.setObrazac5DTOS(details);
        response.setObrazacType(ObrazacType.OBRAZAC_5);

        return response;
    }

    private ObrazacIO findValidIO(Integer kvartal, Integer jbbk) throws Exception {
        Optional<ObrazacIO> optionalZb =
                obrazacIOrepository.findFirstByJbbkIndKorAndKojiKvartalOrderByVerzijaDesc(jbbk, kvartal);

        if (optionalZb.isEmpty() || optionalZb.get().getRADNA() == 0 || optionalZb.get().getSTORNO() == 1) {
            throw new Exception("Nije moguce ucitati obrazac,\nne postoji vec ucitan" +
                    "Obrazac IO. Prvo ucitajte \n Obrazac IO!");
        }
        return optionalZb.get();
    }

    public ObrazacResponse obrazac5ForResponse(Integer jbbks, Integer kvartal) {
        Optional<Obrazac5> optionalObrazac5 =
                obrazacRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc( kvartal, jbbks);
        if (optionalObrazac5.isPresent()) {
            ObrazacResponse obrazacResponse = mapper.toResponse(optionalObrazac5.get());
            obrazacResponse.setObrazacType(ObrazacType.OBRAZAC_5);
            return obrazacResponse;
        }
        var response = new ObrazacResponse();
        response.setObrazacType(ObrazacType.OBRAZAC_5);
        return response;
    }

    private ObrazacResponse findValidObr5ForStorno(String email, Integer kvartal) throws Exception {

        Integer jbbk = this.getJbbksIBK(email);
        Obrazac5 zb =
                obrazacRepository
                        .findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(jbbk, kvartal)
                        .orElseThrow(() -> new IllegalArgumentException("Ne postoji ucitan dokument!"));
        this.isObrazacStorniran(zb);
        return mapper.toResponse(zb);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Integer checkIfExistValidObrazac5AndFindVersion(Integer jbbks, Integer kvartal) throws ObrazacException {
        var optionalObrazacZb =
                this.findLastVersionOfObrazac5Zb(jbbks, kvartal);
        if (optionalObrazacZb.isEmpty()) {
        return 1;}
        Obrazac5 zb = optionalObrazacZb.get();
        checkIfExistValidObrazacYet(zb);
        return zb.getVerzija() + 1;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoObrAfterObrIO(User user, Integer kvartal) {

        Optional<Obrazac5> optionalObrazacZb = this.findLastVersionOfObrazac5Zb(user, kvartal);

        if (optionalObrazacZb.isEmpty())
            return "";

        var obrazacZb = optionalObrazacZb.get();
        if (obrazacZb.getRADNA() == 0 || obrazacZb.getSTORNO() == 1)
            return "";

//        obrazacZb.setOpisstorno("Storniran prethodni dokument!");
        return this.stornoObr5(obrazacZb, user,"Storniran prethodni dokument!");
    }

    public String stornoObr5FromUser(Integer id, String email, Integer kvartal, String opis) {

      var zbForStorno = findObrazacById(id, kvartal);
        User user = this.getUser(email);
       return stornoObr5(zbForStorno, user, opis);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoObr5(Integer id, String email) {
        User user = this.getUser(email);
        var obrazacZb = obrazacRepository.findById(id).get();
        obrazacZb.setRADNA(0);
        obrazacZb.setStorno(1);
        obrazacZb.setStosifrad(user.getSifraradnika());
        //obrazacZb.setOpisstorno(opis);

        obrazacRepository.save(obrazacZb);
        return "Obrazac5 uspesno je storniran";
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoObr5(Obrazac5 obrazac5, User user, String opis) {

        obrazac5.setRADNA(0);
        obrazac5.setStorno(1);
        obrazac5.setStosifrad(user.getSifraradnika());
        obrazac5.setOpisstorno(opis);
        obrazacRepository.save(obrazac5);
        return "Obrazac5 uspesno je storniran";
    }

    private  Optional<Obrazac5> findLastVersionOfObrazac5Zb(User user, Integer kvartal) {
        var jbbk = this.getJbbksIBK(user);
        return obrazacRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbk);
    }

    public   Optional<Obrazac5> findLastVersionOfObrazac5Zb(Integer jbbk, Integer kvartal) {
        return obrazacRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbk);
    }

    private  Optional<Obrazac5> findLastVersionOfObrazac5Zb(String email, Integer kvartal) {

        var jbbk = this.getJbbksIBK(email);
        return obrazacRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbk);
    }

    public List<ObrazacResponse> findValidObrazacToStorno(String email, Integer kvartal) throws Exception {

        Obrazac5 zb = findLastVersionOfObrazac5Zb(email, kvartal)
                .orElseThrow(() -> new IllegalArgumentException("Ne postoji ucitan dokument!"));
        isObrazacSentToDBK(zb);
        isObrazacStorniran(zb);
        return List.of(mapper.toResponse(zb));
    }

    public List<ObrazacResponse> findValidObrazacToRaise(String email, Integer status, Integer kvartal) throws Exception {
        //TODO implement logic
        Obrazac5 zb = findLastVersionOfObrazac5Zb(email, kvartal)
                .orElseThrow(() -> new IllegalArgumentException("Ne postoji ucitan dokument!"));
        isObrazacStorniran(zb);
        statusService.resolveObrazacAccordingStatus(zb, status);
        // check previous
        var obrazacIO = findValidIO(kvartal, getJbbksIBK(email));
        statusService.resolveObrazacAccordingPreviousObrazac(zb, obrazacIO);
        return List.of(mapper.toResponse(zb));
    }

    public String raiseStatus(Integer id, String email, Integer kvartal) throws Exception {

        User user = this.getUser(email);
        var zb = findObrazacById(id, kvartal);
        this.checkStatusAndStorno(zb);
        return statusService.raiseStatusDependentOfActuallStatus(zb, user, obrazacRepository);
    }
}
