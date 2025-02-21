package IndirektniPSF.backend.IOobrazac.obrazacIO;


import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.PomObrazac;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetailService;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIOMapper;
import IndirektniPSF.backend.arhbudzet.Arhbudzet;
import IndirektniPSF.backend.arhbudzet.ArhbudzetService;
import IndirektniPSF.backend.excel.ExcelService;
import IndirektniPSF.backend.exceptions.ObrazacException;
import IndirektniPSF.backend.glavaSvi.GlavaSviService;
import IndirektniPSF.backend.krt.StanjeKrtaService;
import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5;
import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5Service;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.obrazac5.sekretarijat.Sekretarijat;
import IndirektniPSF.backend.parameters.*;
import IndirektniPSF.backend.review.ObrazacResponse;
import IndirektniPSF.backend.review.ObrazacType;
import IndirektniPSF.backend.security.user.User;
import IndirektniPSF.backend.security.user.UserRepository;
import IndirektniPSF.backend.zakljucniList.details.ZakljucniListDetails;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZb;
import IndirektniPSF.backend.zakljucniList.zb.ZakljucniListZbRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;


@RequiredArgsConstructor
@Service
@Component
public class ObrazacIOService extends AbParameterService implements IfObrazacChecker, IfObrazacService<ObrazacIO>, NumberUtils {

    private final ObrazacIORepository obrazacIOrepository;
    private final SekretarijarService sekretarijarService;
    private final PPartnerService pPartnerService;
    private final ObrazacIODetailService obrazacIODetailService;
    private final UserRepository userRepository;
    private final Obrazac5Service obrazac5Service;
    private StringBuilder responseMessage = new StringBuilder();
    private final ExcelService excelService;
    private final ObrazacIOMapper mapper;
    private final ZakljucniListZbRepository zakljucniRepository;
    private final StatusService statusService;
    private final GlavaSviService glavaSviService;
    private final ArhbudzetService arhbudzetService;
    private final StanjeKrtaService stanjeKrtaService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public StringBuilder saveObrazacFromExcel(MultipartFile file, Integer kvartal, String email) throws Exception {

        //INITILIZATION
        responseMessage.delete(0, responseMessage.length());
        User user = this.getUser(email);
        Integer jbbks = this.getJbbksIBK(user);
        String oznakaGlave = glavaSviService.findGlava(jbbks);
        Integer version = checkIfExistValidObrazacIOAndFindVersion(jbbks, kvartal);
        ZakljucniListZb zakList = this.findValidZakList(kvartal, jbbks);
        Integer sifSekret = user.getZa_sif_sekret();
        Sekretarijat sekretarijat = sekretarijarService.getSekretarijat(sifSekret);
        Integer today = (int) LocalDate.now().toEpochDay() + 25569;
        Integer year = excelService.readCellByIndexes(file.getInputStream(), 2, 3);
        Integer jbbkExcel = excelService.readCellByIndexes(file.getInputStream(), 2, 1);
        List<ObrazacIODTO> dtos = mapper.mapExcelToPojo(file.getInputStream());

        //VARIOUS CHECKS
        chekIfKvartalIsCorrect(kvartal, kvartal, year); //TODO uncomment in production
        checkJbbks(user, jbbkExcel);
        checkForDuplicatesStandKlasif(dtos);
        responseMessage.append(checkSumOfPrenetihSredsAgainstKonto791111(user, jbbks, oznakaGlave ,kvartal,  dtos));//TODO uncomment in production
        responseMessage.append(checkIfStandKlasifFromExcelExistInFinPlana(dtos,jbbks,kvartal));
        checkIfPlanAndIzvrsenjeAreZero(dtos);
        responseMessage.append(checkSumOnKlasa3(jbbks, file));
        compareIoAndZakljucni(dtos, kvartal, jbbks);

        //INITILIZATION AND PERSISTANCE OF MASTER OBJECT
        ObrazacIO obrIO = ObrazacIO.builder()
                    .KOJI_KVARTAL(kvartal)
                    .GODINA(year)
                    .VERZIJA(version)
                    .RADNA(1)
                    .SIF_SEKRET(sifSekret)
                    .RAZDEO(sekretarijat.getRazdeo())
                    .JBBK(sekretarijat.getJED_BROJ_KORISNIKA())
                    .JBBK_IND_KOR(jbbkExcel)
                    .SIF_RAC(1)
                    .DINARSKI(1)
                    .STATUS(0)
                    .POSLATO_O(0)
                    .POVUCENO(0)
                    .KONACNO(0)
                    .POSLAO_NAM(user.getSifraradnika())
                    .DATUM_DOK(today)
                    .PODIGAO_STATUS(0)
                    .DATUM_POD_STATUSA(0)
                    .POSLAO_U_ORG(0)
                    .DATUM_SLANJA(0)
                    .POSLAO_IZ_ORG(0)
                    .DATUM_ORG(0)
                    .ZAPRIMIO_VER(0)
                    .OVERIO_VER(0)
                    .ODOBRIO_VER(0)
                    .PROKNJIZENO(0)
                    .XLS(1)
                    .STORNO(0)
                    .STOSIFRAD(0)
                    .GEN_OPENTAB(0)
                    .build();

            ObrazacIO obrIOSaved = obrazacIOrepository.save(obrIO);
            var details = obrazacIODetailService.saveListOfObrazac5_pom(dtos, obrIOSaved, zakList.getStavke(), oznakaGlave);
            return responseMessage;
    }

    public void compareIoAndZakljucni(List<ObrazacIODTO> dtos, Integer kvartal, Integer jbbks) throws Exception {

//        Konta 311712 i 321311 ako postoje u zaključnom obrascu mora da postoje i u IO-obrasca i obrnuto.
//        Vrednosti moraju da budu jednake
        //for compared klase 4,5,6,7,8,9
        //TODO za klase 4,5,6 poredi se io sa TP Duguje a zaklase 7,8,9 sa TP potrazuje
        List<PomObrazac> zak = convertZakListInPomObrazac(kvartal, jbbks, 300000);
        List<PomObrazac> zak4 = zak.stream().filter(entry -> entry.getKonto() > 400000 && entry.getKonto() < 999999).toList();
        List<PomObrazac> ioRaw = convertIoToPomObrazac(dtos, 400000);
        List<PomObrazac> io = ioRaw.stream().filter(entry -> entry.getSaldo() > 0.0).toList();
        checkIfAllKontosFromIoExistInZk(zak4,io);
        chekEqualityOfIoAndZlBySaldo(zak4, io);

        //for compared klasa 3
        List<PomObrazac> io3 = convertIoToPomObrazac(dtos, 300000);
        List<PomObrazac> ioKlasa3 = io3.stream().filter(entry ->  entry.getKonto() < 400000).toList();
        List<PomObrazac> zakKlasa3 = zak.stream().filter(entry -> entry.getKonto() > 300000 && entry.getKonto() < 400000).toList();
        checkKlasa3InIoExistAndIsSmallerThenInZakList(ioKlasa3, zakKlasa3);
        checkForKonto311712And321311InZakAndIo(zakKlasa3, ioKlasa3);
    }

    void checkForKonto311712And321311InZakAndIo(List<PomObrazac> zakKlasa3, List<PomObrazac> ioKlasa3) throws ObrazacException {
//        final Double tolerance = 0.0001;

        List<PomObrazac> zakKonto311712And321311 = zakKlasa3.stream()
                .filter(entry ->  entry.getKonto() == 311712 ||  entry.getKonto() == 321311).toList();

        if (zakKonto311712And321311.size() !=  0) {
            List<PomObrazac> ioKonto311712And321311 = ioKlasa3.stream()
                    .filter(entry ->  entry.getKonto() == 311712 ||  entry.getKonto() == 321311).toList();
            for (PomObrazac zak : zakKonto311712And321311) {
                boolean foundMatchingKonto = false;
                for (PomObrazac io : ioKonto311712And321311) {
                    if (io.getKonto().equals(zak.getKonto())) {
                        foundMatchingKonto = true;
                        if (!io.equals(zak)) {
                            throw new ObrazacException("Konto " + io.getKonto() + " u Obrascu IO ima razlicitu vrednost \n od istog konta u vec ucitanom Zakljucnom listu!\n");
                        }
                    }
                }
                if (!foundMatchingKonto) {
                    throw new ObrazacException("Konto " + zak.getKonto() + " iz Zakljucnog lista ne postoji u Obrasca IO!\n");
                }
            }
        }
    }

    void checkKlasa3InIoExistAndIsSmallerThenInZakList(List<PomObrazac> ioKlasa3, List<PomObrazac> zakKlasa3) throws ObrazacException {
        final Double tolerance = 0.0001;

        for (PomObrazac io : ioKlasa3) {
            boolean foundMatchingKonto = false;

            for (PomObrazac zak : zakKlasa3) {
                if (io.getKonto().equals(zak.getKonto())) {
                    foundMatchingKonto = true;

                    if ((zak.getSaldo() + tolerance) < io.getSaldo()) {
                        throw new ObrazacException("Konto " + io.getKonto() + " u Obrascu IO ima vecu vrednost \n od istog konta u vec ucitanom Zakljucnom listu!\n");
                    }
                }
            }
            if (!foundMatchingKonto) {
                throw new ObrazacException("Konto " + io.getKonto() + " iz Obrasca IO ne postoji u vec ucitanom Zakljucnom listu!\n");
            }
        }
    }




    public void checkIfAllKontosFromIoExistInZk(List<PomObrazac> zak, List<PomObrazac> io) throws ObrazacException {
        List<Integer> zakInt = zak.stream()
                .map(PomObrazac::getKonto)
                .collect(toList());

        List<Integer> ioInt = io.stream()
                .map(PomObrazac::getKonto)
                .collect(toList());

        Set<Integer> ioIntSet = new HashSet<>(ioInt);
        List<Integer> ioIntUnique = new ArrayList<>(ioIntSet);

        ioIntUnique.removeAll(new ArrayList<>(zakInt));
        if (!ioIntUnique.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            ioIntUnique.forEach(d -> sb.append(d).append(", "));
            throw new ObrazacException("Obrazac IO ima konta koja \n ne postoje u vec ucitanom Zakljucnom listu: \n" + sb + "\n");
        }

        List<Integer> zakInt4_7 = zakInt.stream().filter(z -> z > 400000 ).collect(toList());
        zakInt4_7.removeAll(new ArrayList<>(ioIntSet));
        if (!zakInt4_7.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            zakInt4_7.forEach(d -> sb.append(d).append(", "));
            throw new ObrazacException("Vec ucitani Zakljucni list ima konta\n koja ne postoje u obrascu IO: \n" + sb + "\n");
        }
    }

    public  List<PomObrazac> transformToUniqueList(List<PomObrazac> zak) {
        // Group by 'konto' and sum 'saldo'
        Map<Integer, Double> groupedByKonto = zak.stream()
                .collect(Collectors.groupingBy(
                        PomObrazac::getKonto,
                        Collectors.summingDouble(PomObrazac::getSaldo)
                ));

        // Map the grouped results to new PomObrazac instances
        List<PomObrazac> zakUnique = groupedByKonto.entrySet().stream()
                .map(entry -> new PomObrazac(entry.getKey(), entry.getValue()))
                .filter(entry -> entry.getKonto() >400000)
                .collect(toList());

        return zakUnique;
    }

    public void chekEqualityOfIoAndZlBySaldo(List<PomObrazac> zak, List<PomObrazac> io) throws Exception {

        List<PomObrazac> ioUnique = transformToUniqueList(io);
        List<PomObrazac> diff =  new ArrayList<>(ioUnique);
        List<PomObrazac> diff2 =  new ArrayList<>(zak);

        diff.removeAll(zak);
        diff2.removeAll(ioUnique);

        diff.addAll(diff2);
        List<PomObrazac> diffFiltered = diff.stream().filter(d -> d.getSaldo() != 0.00).collect(toList());
        Set<PomObrazac> diffSet = new HashSet<>(diffFiltered);

        if (!diffSet.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            diffSet.forEach(d -> sb.append(d.getKonto()).append(", "));
            throw new ObrazacException("Obrazac IO i vec ucitani Zakljucni list \n" +
                    "se ne slazu u kontima : " + sb + "\n");
        }
    }

    public List<PomObrazac> convertIoToPomObrazac(List<ObrazacIODTO> dtos, Integer konto) {

        List<ObrazacIODTO> dtosFiltered = fiterDtosAccordingKonto(dtos,konto);

        List<PomObrazac> pomList = dtosFiltered.stream()
                .map(i -> {
                    PomObrazac pom = new PomObrazac();
                    pom.setKonto(i.getKonto());
                    pom.setSaldo(i.getIzvrsenje());
                    return  pom;
                })
                .collect(toList());
        return makeListOfPomUniqueKontosAndSumOfSaldo(pomList);
    }

    private List<ObrazacIODTO> fiterDtosAccordingKonto(List<ObrazacIODTO> dtos, int konto) {
        return dtos.stream()
                .filter(i -> i.getKonto() > konto)
                .filter(i -> i.getKonto() % 100 != 0)
                .collect(toList());
    }

//IZMENJENA METODA  ZBOG KVARTALA 5
//    public List<PomObrazac> convertZakListInPomObrazac(Integer kvartal, Integer jbbks, Integer konto) throws Exception {
//
//        List<ZakljucniListDetails> zakljucniListDetailsList = findValidZakList(kvartal,jbbks).getStavke();
//
//        return zakljucniListDetailsList.stream()
//                .map(z -> {
//                    PomObrazac pom = new PomObrazac();
//                    pom.setKonto(z.getKONTO());
//                    pom.setSaldo(NumberUtils.roundToTwoDecimals((z.getDUGUJE_PS() - z.getPOTRAZUJE_PS()) + ( z.getDUGUJE_PR() - z.getPOTRAZUJE_PR())));
//                    return pom;
//                })
//                .filter(p -> p.getKonto() > konto )
//                .collect(toList());
//    }


    public List<PomObrazac> convertZakListInPomObrazac(Integer kvartal, Integer jbbks, Integer konto) throws Exception {

        List<ZakljucniListDetails> zakljucniListDetailsList = findValidZakList(kvartal,jbbks).getStavke();

        return zakljucniListDetailsList.stream()
                .map(z -> {
                    PomObrazac pom = new PomObrazac();
                        pom.setKonto(z.getKONTO());
                    //TODO za klase 4,5,6 poredi se io sa TP Duguje a zaklase 7,8,9 sa TP potrazuje
                        if(400000 < z.getKONTO() && z.getKONTO() < 700000) {
                            pom.setSaldo(NumberUtils.roundToTwoDecimals(z.getDUGUJE_PS()  +  z.getDUGUJE_PR()));
                        } else {
                            pom.setSaldo(NumberUtils.roundToTwoDecimals((z.getPOTRAZUJE_PS() +  z.getPOTRAZUJE_PR())));
                        }
                        return pom;
                })
                .filter(p -> p.getKonto() > konto )
                .collect(toList());
    }

    public List<PomObrazac> makeListOfPomUniqueKontosAndSumOfSaldo(List<PomObrazac> pomList) {
        return pomList.stream()
                .collect(Collectors.groupingBy(
                        PomObrazac::getKonto,
                        Collectors.summingDouble(PomObrazac::getSaldo)))
                .entrySet().stream()
                .map(entry -> new PomObrazac(entry.getKey(), entry.getValue()))
                .collect(toList());
    }

    private String checkSumOnKlasa3(Integer jbbks,MultipartFile file) throws Exception {

        Double sumFromStanjeKrta = stanjeKrtaService.getTransferedAmountOfBalancesforJbbk(jbbks);
        Double amountFromExcel = excelService.readCellOfDoubleValueByIndexes(file.getInputStream(), 40, 11);
        System.out.println("from krt: " + sumFromStanjeKrta);
        System.out.println("from excel: " + amountFromExcel);

        if (!areEqual(sumFromStanjeKrta,amountFromExcel)) {
            return "\nNe slaže se suma na unetoj klasi 3 \n" +
                    "sa stanjem na žiro racunima \n";
        }
        return "";
    }

    public void checkIfPlanAndIzvrsenjeAreZero(List<ObrazacIODTO> dtos) throws ObrazacException {
        List<ObrazacIODTO> planAndIzvrsenjeAreZero =
                dtos.stream()
                        .filter(dto -> dto.getIzvrsenje() == 0 && dto.getPlan() == 0)
                        .toList();
        if(!planAndIzvrsenjeAreZero.isEmpty()) {
            throw new ObrazacException("Imate stavku gde su izvrsenje i plan 0!" +
                    planAndIzvrsenjeAreZero.stream()
                            .map(ObrazacIODTO::toString)
                            .collect(Collectors.joining("\n")));
        }
    }

    public void checkForDuplicatesStandKlasif(List<ObrazacIODTO> list) throws ObrazacException {
        Map<String, List<ObrazacIODTO>> duplicatesMap = new HashMap<>();

        for (ObrazacIODTO item : list) {
            String key = getKeyForDuplicateCheck(item);
            if (duplicatesMap.containsKey(key)) {
                duplicatesMap.get(key).add(item);
            } else {
                List<ObrazacIODTO> itemsWithSameKey = new ArrayList<>();
                itemsWithSameKey.add(item);
                duplicatesMap.put(key, itemsWithSameKey);
            }
        }

        for (Map.Entry<String, List<ObrazacIODTO>> entry : duplicatesMap.entrySet()) {
            List<ObrazacIODTO> duplicateItems = entry.getValue();
            if (duplicateItems.size() > 1) {
                StringBuilder errorMessage = new StringBuilder("Postoje redovi sa identicnom stand. klasifikacijom :\n");
                for (ObrazacIODTO duplicateItem : duplicateItems) {
                    errorMessage.append("Programska aktivnost: ").append(duplicateItem.getRedBrojAkt()).append("\n");
                    errorMessage.append("Funkc. klasifikacija: ").append(duplicateItem.getFunkKlas()).append("\n");
                    errorMessage.append("Konto: ").append(duplicateItem.getKonto()).append("\n");
                    errorMessage.append("Izvor finansiranja: ").append(duplicateItem.getIzvorFin()).append("\n");
                }
                throw new ObrazacException(errorMessage.toString());
            }
        }
//        Set<ObrazacIODTO> set =
//                list.stream().collect(Collectors.toSet());
//        if (set.size() < list.size()) {
//                            throw new ObrazacException("Imate dupliranih standardnih klasifikacija!");
//        }
    }

    private String getKeyForDuplicateCheck(ObrazacIODTO item) {
        return item.getRedBrojAkt() + "_" + item.getFunkKlas() + "_" + item.getKonto() + "_" + item.getIzvorFin();
    }

    private String checkSumOfPrenetihSredsAgainstKonto791111(User user, Integer jbbks, String glava,
                                                           Integer kvartal, List<ObrazacIODTO> dtos) throws ObrazacException {
        var sifSekr = user.getZa_sif_sekret();
        Double date = (double)getLastDayOfKvartal(kvartal).toEpochDay() + 25569;
        var sumOfPrenetihSreds = arhbudzetService.sumUplataIzBudzetaForIndKor(sifSekr, date,glava, jbbks);
        //TODO if plan is right value, or change it with correct property
        Double sum791111 = dtos.stream()
                .filter(dto -> dto.getKonto() == 791111)
                .map(dto -> dto.getIzvrsenje())
                .mapToDouble(Double::doubleValue)
                .sum();

        System.out.println(String.format("%.2f", sum791111));
        System.out.println(String.format("%.2f", sumOfPrenetihSreds));

        if (!areEqual(sumOfPrenetihSreds, sum791111)) {
                throw new ObrazacException("Ne slaže se iznos prenetih sredstava na rashodima\n" +
                        "( " + sumOfPrenetihSreds.doubleValue() + " )\n" +
                        "sa iznosom na kontu 791111 u Excel obrascu " +
                        "( " + sum791111.doubleValue() + " )"
                        );
            }
        return "";
    }

    @Override
    public ObrazacResponse getObrazactWithDetailsForResponseById(Integer id, Integer kvartal) throws Exception {

        var zb = findObrazacById(id, kvartal);
        List<ObrazacIODTO> details =
                zb.getStavke().stream()
                        .map(mapper::toDto)
                        .collect(toList());
        ObrazacResponse response = mapper.toResponse(zb);
        response.setObrazacIODTOS(details);
        response.setObrazacType(ObrazacType.OBRAZAC_IO);
        return response;
    }

    public ZakljucniListZb findValidZakList(Integer kvartal, Integer jbbks) throws Exception {
        Optional<ZakljucniListZb> optionalZb =
                zakljucniRepository.findFirstByKojiKvartalAndJbbkIndKorOrderByVerzijaDesc(kvartal, jbbks);

        if (optionalZb.isEmpty() || optionalZb.get().getRADNA() == 0 || optionalZb.get().getSTORNO() == 1) {
            throw new Exception("Nije moguce ucitati obrazac,\nne postoji vec ucitan" +
                    "Zakljucni list.\nPrvo ucitajte Zakljucni list!");
        }
        return optionalZb.get();
    }

    public ObrazacResponse obrazacIOForResponse(Integer jbbks, Integer kvartal) {
        Optional<ObrazacIO> optionalObrazacIO =
                obrazacIOrepository.findFirstByJbbkIndKorAndKojiKvartalOrderByVerzijaDesc(jbbks, kvartal);
        if (optionalObrazacIO.isPresent()) {
            ObrazacResponse obrazacResponse = mapper.toResponse(optionalObrazacIO.get());
            obrazacResponse.setObrazacType(ObrazacType.OBRAZAC_IO);
            return obrazacResponse;
        }
        var response = new ObrazacResponse();
        response.setObrazacType(ObrazacType.OBRAZAC_IO);
        return response;
    }

    private Integer checkIfExistValidObrazacIOAndFindVersion(Integer jbbk, Integer kvartal) throws Exception {

        Optional<ObrazacIO> optionalZb =
                obrazacIOrepository.findFirstByJbbkIndKorAndKojiKvartalOrderByVerzijaDesc(jbbk, kvartal);
        if (optionalZb.isEmpty()) {
            return 1;
        }
        ObrazacIO zb = optionalZb.get();
        checkIfExistValidObrazacYet(zb);
        return zb.getVERZIJA() + 1;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoIOAfterStornoZakList(User user, Integer kvartal) throws Exception {

        var optionalIO = findLastOptionalIOForKvartal(user, kvartal);

        if (optionalIO.isEmpty() || optionalIO.get().getRADNA() == 0 || optionalIO.get().getSTORNO() == 1) {
            return "";
        }
        var io = optionalIO.get();

        io.setSTORNO(1);
        io.setRADNA(0);
        io.setSTOSIFRAD(user.getSifraradnika());
        io.setOPISSTORNO("Storniran prethodni dokument!");
        obrazacIOrepository.save(io);
        return "Obrazac IO je uspesno storniran!\n" + obrazac5Service.stornoObrAfterObrIO(user, kvartal);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoIO(Integer id, String email) {
        var user = this.getUser(email);
        var io = obrazacIOrepository.findById(id).get();
        io.setSTORNO(1);
        io.setRADNA(0);
        io.setSTOSIFRAD(user.getSifraradnika());
        obrazacIOrepository.save(io);
        return "Obrazac IO je uspesno storniran!\n" + obrazac5Service.stornoObrAfterObrIO(user, io.getKOJI_KVARTAL());
    }

    public Optional<ObrazacIO> findLastOptionalIOForKvartal(User user, Integer kvartal) {
        var jbbk = this.getJbbksIBK(user);
        return obrazacIOrepository.findFirstByJbbkIndKorAndKojiKvartalOrderByVerzijaDesc(jbbk, kvartal);
    }

    public Optional<ObrazacIO> findLastOptionalIOForKvartal(String email, Integer kvartal) {
        var jbbk = this.getJbbksIBK(email);
        return obrazacIOrepository.findFirstByJbbkIndKorAndKojiKvartalOrderByVerzijaDesc(jbbk, kvartal);
    }

    public List<ObrazacResponse> findValidObrazacToStorno(String email, Integer kvartal) throws Exception {
        ObrazacIO zb = findLastOptionalIOForKvartal(email, kvartal)
                .orElseThrow(() -> new IllegalArgumentException("Ne postoji ucitan dokument!"));
        this.isObrazacStorniran(zb);
        this.isObrazacSentToDBK(zb);
        return List.of(mapper.toResponse(zb));
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String stornoObrIOFromUser(Integer id, String email, Integer kvartal, String opis) throws Exception {

        User user = this.getUser(email);
        var zb = findObrazacById(id, kvartal);
        this.checkStatusAndStorno(zb);
        zb.setSTORNO(1);
        zb.setRADNA(0);
        zb.setSTOSIFRAD(user.getSifraradnika());
       zb.setOPISSTORNO(opis);//TODO dodati opis storno
        obrazacIOrepository.save(zb);
        return "Obrazac IO je uspesno storniran!\n" + obrazac5Service.stornoObrAfterObrIO(user, kvartal);
    }

    public List<ObrazacResponse> findValidObrazacToRaise(String email, Integer status, Integer kvartal) throws Exception {

        Integer jbbks = getJbbksIBK(email);
        ObrazacIO zb = findLastOptionalIOForKvartal(email, kvartal)
                .orElseThrow(() -> new IllegalArgumentException("Ne postoji ucitan dokument!"));
        this.isObrazacStorniran(zb);

        statusService.resolveObrazacAccordingStatus(zb, status);

        //check next
        //TODO uncoment next block after implementing obrazac 5
        Optional<Obrazac5> obrazac5 =
                obrazac5Service.findLastVersionOfObrazac5Zb(jbbks, kvartal);

        if(zb.getSTATUS() == 10) {
            if(obrazac5.isEmpty()) {
              throw  new ObrazacException("Nije moguce odobravanje obrrasca\n" +
                        "jer ne postoji ucitan Obrazac 5.\n" +
                        "Morate prethodno ucitati Obrazac 5!");
            }
        }
        //TODO uncoment next block after implementing obrazac 5

//        if (obrazac5.getSTORNO() == 1) {
//            throw new ObrazacException("Nije moguce odobravanje obrasca jer je Obrazac 5 storniran.\n" +
//                    " Morate prethodno ucitati Obrazac 5!!");
//        }
        if (obrazac5.isPresent()) {
            statusService.resolveObrazacAccordingNextObrazac(zb, obrazac5.get());
        }
        //TODO uncoment previous block after implementing obrazac 5
        // check previous
        var zakList = this.findValidZakList(kvartal, jbbks);
        statusService.resolveObrazacAccordingPreviousObrazac(zb, zakList);

        return List.of(mapper.toResponse(zb));
    }

    public String raiseStatus(Integer id, String email, Integer kvartal) throws Exception {

        User user = this.getUser(email);
        var zb = findObrazacById(id, kvartal);
        this.checkStatusAndStorno(zb);
        return String.valueOf(statusService.raiseStatusDependentOfActuallStatus(zb, user, obrazacIOrepository));
    }

    public ObrazacIO findObrazacById(Integer id, Integer kvartal) throws Exception {
        return
                obrazacIOrepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Ne postoji obrazac!"));
    }

    public String checkIfStandKlasifFromExcelExistInFinPlana(List<ObrazacIODTO> dtos,
                                                             Integer jbbkInd,
                                                             Integer kvartal) throws ObrazacException {

        List<Arhbudzet> arh = arhbudzetService.findDistinctByJbbkIndKorAndSifSekrAndVrstaPromene(jbbkInd, kvartal);
        List<ObrazacIODTO> dtosFromArh = arh.stream()
                .map(mapper::toDtoFromArh)
                .toList();

        List<ObrazacIODTO> dtosFromExcel = dtos.stream()
                .map(dto -> {
                    ObrazacIODTO newDto = new ObrazacIODTO();
                    newDto.setRedBrojAkt(dto.getRedBrojAkt());
                    newDto.setFunkKlas(dto.getFunkKlas());
                    // Apply the division by 100 to the new object
                    newDto.setKonto(dto.getKonto() / 100);
                    newDto.setIzvorFin(dto.getIzvorFin());
                    newDto.setIzvorFinPre(dto.getIzvorFinPre());
                    newDto.setPlan(dto.getPlan());
                    newDto.setIzvrsenje(dto.getIzvrsenje());
                    return newDto;
                })
                .collect(toList());

        var messageForExcel = checkIfStandKlasifBetweenExcenAndFinPlan(dtosFromExcel, dtosFromArh);
        if (messageForExcel.length() > 0) {
            messageForExcel =  "Imate u obrascu standardne klasifikacije \nkoje ne postoje u fin.planu!\n "
                             + messageForExcel + "\n";
        }
        var messageForPlan = "";//checkIfStandKlasifBetweenExcenAndFinPlan( dtosFromArh,dtos);
        if (messageForExcel.length() > 0) {
            messageForPlan =  "Imate u fin.planu standardne klasifikacije \nkoje ne postoje u obrascu!\n "
                              +  messageForPlan + "\n";
        }

        boolean clauseForCheckingInExcelStandKlas = false;//zuta greska
        boolean clauseForCheckingInFinPlanStandKlas = false;//crvena greska

        if (clauseForCheckingInExcelStandKlas && messageForExcel.length() > 0) {
            throw new ObrazacException(messageForExcel);
        }
        if (clauseForCheckingInFinPlanStandKlas && messageForPlan.length() > 0) {
            throw new ObrazacException(messageForPlan);
        }
        return messageForExcel + messageForPlan;
    }

    public String checkIfStandKlasifBetweenExcenAndFinPlan(List<ObrazacIODTO> dtos,
                                                            List<ObrazacIODTO> dtosFromArh) {

        List<ObrazacIODTO> elementsNotInArh = dtos.stream()
                .filter(element -> dtosFromArh.stream().noneMatch(arhElement -> arhElement.equals(element)))
                .toList();
        return elementsNotInArh.stream()
                .map(ObrazacIODTO::toString)
                .collect(Collectors.joining("\n"));
    }


}
