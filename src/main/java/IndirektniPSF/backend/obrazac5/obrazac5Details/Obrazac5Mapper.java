package IndirektniPSF.backend.obrazac5.obrazac5Details;

import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetails;
import IndirektniPSF.backend.obrazac5.Obrazac5DTO;
import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5;
import IndirektniPSF.backend.review.ObrazacResponse;
import IndirektniPSF.backend.review.ValidOrStorno;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.stream.IntStream;


@Component
public class Obrazac5Mapper {
    public Obrazac5details mapDtoToEntity(Obrazac5DTO dto) {
        var konto = (dto.getKonto() != null ? dto.getKonto() : 0);
        return Obrazac5details.builder()
                .verzija(1)
                .koji_kvartal(0)
                .sif_rac(1)
                .oznakaop(dto.getOznakOp())
                .konto(konto)
                .opis(dto.getOpis())
                .planprihoda(nz(dto.getPlanPrihoda()))
                .republika(nz(dto.getRepublika()))
                .pokrajina(nz(dto.getPokrajina()))
                .opstina(nz(dto.getOpstina()))
                .ooso(nz(dto.getOoso()))
                .donacije(nz(dto.getDonacije()))
                .ostali(nz(dto.getOstali()))
                .godplan(nz(dto.getPlanPrihoda()))
                .izvrsenje(nz(dto.getIzvrsenje()))
                .unosio(0)
                .dinarski(1)
                .kvplan(0.0)
                .rep_b(0.0)
                .pok_b(0.0)
                .ops_b(0.0)
                .ooso_b(0.0)
                .dona_b(0.0)
                .ost_b(0.0)
                .izvrs_bit(0.0)
                .izvrs_sop(0.0)
                .za_unos(1)
                .tip_obrazca(5)
                .nivo_konsolidacije(0)
                .build();
    }
    private static double nz(Double v) { return v == null ? 0d : v; }
    private static int nz(Integer v) { return v == null ? 0 : v; }


//    public List<Obrazac5DTO> mapExcelToPojo(InputStream inputStream) throws Exception {
//        List<Obrazac5DTO> dtos = new ArrayList<>();
//        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
//            Sheet sheet = workbook.getSheetAt(0);
//            int i = 26; // Start reading from the 26th row (assuming 0-based index)
//            int consecutiveBlankRows = 0;
//            int allowedBlankRows = 3; // End reading after 3 blank rows
//
//            while (consecutiveBlankRows < allowedBlankRows && i < 480) {
//                Row row = sheet.getRow(i);
//                if (row == null || row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) {
//                    consecutiveBlankRows++;
//                    i++;
//                    continue;
//                } else {
//                    consecutiveBlankRows = 0;
//                }
//                Obrazac5DTO dto = new Obrazac5DTO();
//                dto.setOznakOp(getIntegerValueFromCell(row.getCell(0)));
//                dto.setKonto(getIntegerValueFromCell(row.getCell(1)));
//                dto.setOpis(row.getCell(2).getStringCellValue());
//                dto.setPlanPrihoda(getDoubleValueFromCell(row.getCell(3)));
//                dto.setIzvrsenje(getDoubleValueFromCell(row.getCell(4)));
//                dto.setRepublika(getDoubleValueFromCell(row.getCell(5)));
//                dto.setPokrajina(getDoubleValueFromCell(row.getCell(6)));
//                dto.setOpstina(getDoubleValueFromCell(row.getCell(7)));
//                dto.setOoso(getDoubleValueFromCell(row.getCell(8)));
//                dto.setDonacije(getDoubleValueFromCell(row.getCell(9)));
//                dto.setOstali(getDoubleValueFromCell(row.getCell(10)));
//                dtos.add(dto);
//                i++;
//            }
//        } catch (IllegalStateException e) {
//            throw new IllegalStateException(e.getMessage(), e);
//        } catch (Exception e) {
//            throw new Exception("Podaci iz excel tabele nisu uspesno ucitani", e);
//        }
//        return dtos;
//    }


    // TODO nova metoda zbog novog obrasca gornja je zakomentarisana jer je vezana za
    //  stari obrazac, a novi obrazac ima drugaciji raspored i broj kolona, tako da bi bilo komplikovano da se koristi stara metoda

    public List<Obrazac5DTO> mapExcelToPojo(InputStream inputStream) throws Exception {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = pickSheet(workbook); // <-- BITNO
            DataFormatter formatter = new DataFormatter();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            // A kolona: dozvoli i "5.002" / "5,002" ako se nekad pojavi grupisanje
            Pattern aPattern = Pattern.compile("^[0-9][0-9\\s\\.,]*$");

            return IntStream.range(26, Math.min(480, sheet.getLastRowNum() + 1))
                    .mapToObj(sheet::getRow)
                    .filter(Objects::nonNull)
                    .filter(r -> isValidDataRow(r, formatter, evaluator, aPattern))
                    .map(r -> mapRowToDto(r, formatter, evaluator))
                    .toList();

        } catch (Exception e) {
            // Ako možeš, loguj e da vidiš root-cause
            throw new Exception("Podaci iz excel tabele nisu uspesno ucitani", e);
        }
    }

    private Sheet pickSheet(Workbook workbook) {
        // probaj po imenu (novi/stari template), fallback na prvi
        return Optional.ofNullable(workbook.getSheet("OBRAZACIB"))
                .or(() -> Optional.ofNullable(workbook.getSheet("Obr5")))
                .orElse(workbook.getSheetAt(0));
    }

    private boolean isValidDataRow(Row row,
                                   DataFormatter formatter,
                                   FormulaEvaluator evaluator,
                                   Pattern aPattern) {

        Cell c0 = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (c0 == null) return false;

        // Ako je numerička ćelija → validan podatak red
        if (c0.getCellType() == CellType.NUMERIC) return true;
        if (c0.getCellType() == CellType.FORMULA) {
            CellValue cv = evaluator.evaluate(c0);
            return cv != null && cv.getCellType() == CellType.NUMERIC;
        }

        // STRING fallback
        String s = formatter.formatCellValue(c0, evaluator).trim();
        if (s.isEmpty() || !aPattern.matcher(s).matches()) return false;

        // ukloni separatore i proveri da ostaju samo cifre
        String digits = s.replace("\u00A0", "").replace(" ", "").replace(".", "").replace(",", "");
        return digits.chars().allMatch(Character::isDigit);
    }

    private Obrazac5DTO mapRowToDto(Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
        Obrazac5DTO dto = new Obrazac5DTO();

        dto.setOznakOp(readInt(row.getCell(0), formatter, evaluator));
        dto.setKonto(readInt(row.getCell(1), formatter, evaluator));
        dto.setOpis(readString(row.getCell(2), formatter, evaluator));

        dto.setPlanPrihoda(readDouble(row.getCell(3), formatter, evaluator));
        dto.setIzvrsenje(readDouble(row.getCell(4), formatter, evaluator));
        dto.setRepublika(readDouble(row.getCell(5), formatter, evaluator));
        dto.setPokrajina(readDouble(row.getCell(6), formatter, evaluator));
        dto.setOpstina(readDouble(row.getCell(7), formatter, evaluator));
        dto.setOoso(readDouble(row.getCell(8), formatter, evaluator));
        dto.setDonacije(readDouble(row.getCell(9), formatter, evaluator));
        dto.setOstali(readDouble(row.getCell(10), formatter, evaluator));

        return dto;
    }

    private Integer readInt(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        Double d = readDouble(cell, formatter, evaluator);
        return d == null ? null : d.intValue();
    }

    private Double readDouble(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();

            case FORMULA: {
                CellValue cv = evaluator.evaluate(cell);
                if (cv == null) return null;
                if (cv.getCellType() == CellType.NUMERIC) return cv.getNumberValue();
                if (cv.getCellType() == CellType.STRING) return parseLocaleNumber(cv.getStringValue());
                return null;
            }

            case STRING:
                return parseLocaleNumber(cell.getStringCellValue());

            case BLANK:
            default:
                String s = formatter.formatCellValue(cell, evaluator).trim();
                return s.isEmpty() ? null : parseLocaleNumber(s);
        }
    }

    private Double parseLocaleNumber(String raw) {
        if (raw == null) return null;
        String s = raw.replace("\u00A0", "").trim();
        if (s.isEmpty()) return null;

        // ukloni razmake
        s = s.replace(" ", "");

        // Ako ima i '.' i ',' -> poslednji separator je decimalni, drugi je hiljadarski
        int lastDot = s.lastIndexOf('.');
        int lastComma = s.lastIndexOf(',');

        if (lastDot >= 0 && lastComma >= 0) {
            if (lastComma > lastDot) {         // "1.234,56"
                s = s.replace(".", "").replace(",", ".");
            } else {                           // "1,234.56"
                s = s.replace(",", "");
            }
        } else if (lastComma >= 0) {           // "1234,56"
            s = s.replace(",", ".");
        }
        // else: "1234.56" ili "1234"

        // ukloni eventualne nenumeričke tragove (npr. "–" ili tekst)
        if (!s.matches("^-?\\d+(\\.\\d+)?$")) return null;

        return Double.valueOf(s);
    }

    private String readString(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        return cell == null ? "" : formatter.formatCellValue(cell, evaluator).trim();
    }
    // TODO nova metoda zbog novog obrasca

    private Integer getIntegerValueFromCell(Cell cell) {
        return cell != null && cell.getCellType() == CellType.NUMERIC ? (int) cell.getNumericCellValue() : null;
    }

    private Double getDoubleValueFromCell(Cell cell) {

        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return 0.00; // Return default value if cell is null
        }

        if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA ) {
            return cell.getNumericCellValue();
        }

        throw new IllegalStateException("Obrazac nije moguće učitati jer imate \n" +
                "u redu " + (cell.getRowIndex() + 1) + " a koloni broj " + (cell.getColumnIndex() + 1) +
                " polje koje nema numeričku vrednost. (Greška je verovatno nastala prilikom kopiranja!)\n" +
                " Ispravite polje i proverite ostala!");
    }


    public ObrazacResponse toResponse(Obrazac5 zb) {
        LocalDate date = LocalDate.ofEpochDay(zb.getDatum_org() - 25569);
        return ObrazacResponse.builder()
                .id(zb.getGen_mysql())
                .date(date)
                .kvartal(zb.getKoji_kvartal())
                //.year(zb.())
                .version(zb.getVerzija())
                .status(zb.getSTATUS())
                .jbbk(zb.getJbbk_ind_kor())
                .storno(zb.getSTORNO() == 0 ? ValidOrStorno.VALIDAN : ValidOrStorno.STORNIRAN)
                .build();
    }

    public Obrazac5DTO toDto(Obrazac5details obrazac5details) {
        return Obrazac5DTO.builder()
                .oznakOp(obrazac5details.getOznakaop())
                .konto(obrazac5details.getKonto())
                .opis(obrazac5details.getOpis())
                .planPrihoda(obrazac5details.getPlanprihoda())
                .izvrsenje(obrazac5details.getIzvrsenje())
                .republika(obrazac5details.getRepublika())
                .pokrajina(obrazac5details.getPokrajina())
                .opstina(obrazac5details.getOpstina())
                .ooso(obrazac5details.getOoso())
                .donacije(obrazac5details.getDonacije())
                .ostali(obrazac5details.getOstali())
                .build();
    }

    //vraca agregiranu listu po s
    public List<Obrazac5details> mapIOtoObr5(List<ObrazacIODetails> stavke) {
        var obr5FromIO = stavke.stream().map( st ->
                (Obrazac5details.builder()
                        .konto(st.getSIN_KONTO() * 100)
                        .republika(st.getREPUBLIKA())
                        .pokrajina(st.getPOKRAJINA())
                        .opstina(st.getOPSTINA())
                        .ooso(st.getOOSO())
                        .donacije(st.getDONACIJE())
                        .ostali(st.getOSTALI())
                        .build())
        ).collect(Collectors.toList());
        return aggregateByKonto(obr5FromIO);
    }

    public List<Obrazac5details> aggregateByKonto(List<Obrazac5details> detailsList) {
        return detailsList.stream()
                .collect(Collectors.groupingBy(
                        Obrazac5details::getKonto, // Group by 'konto'
                        Collectors.reducing(new Obrazac5details(), (a, b) -> {
                            Obrazac5details result = new Obrazac5details();
                            result.setKonto(a.getKonto() != null ? a.getKonto() : b.getKonto());
                            result.setPokrajina((a.getPokrajina() != null ? a.getPokrajina() : 0.0) +
                                    (b.getPokrajina() != null ? b.getPokrajina() : 0.0));
                            result.setRepublika((a.getRepublika() != null ? a.getRepublika() : 0.0) +
                                    (b.getRepublika() != null ? b.getRepublika() : 0.0));
                            result.setOpstina((a.getOpstina() != null ? a.getOpstina() : 0.0) +
                                    (b.getOpstina() != null ? b.getOpstina() : 0.0));
                            result.setOoso((a.getOoso() != null ? a.getOoso() : 0.0) +
                                    (b.getOoso() != null ? b.getOoso() : 0.0));
                            result.setDonacije((a.getDonacije() != null ? a.getDonacije() : 0.0) +
                                    (b.getDonacije() != null ? b.getDonacije() : 0.0));
                            result.setOstali((a.getOstali() != null ? a.getOstali() : 0.0) +
                                    (b.getOstali() != null ? b.getOstali() : 0.0));
                            return result;
                        })
                ))
                .values()
                .stream()
                .toList();
    }
}
