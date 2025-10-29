package IndirektniPSF.backend.IOobrazac.obrazacIODetails;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIO;
import IndirektniPSF.backend.arhbudzet.Arhbudzet;
import IndirektniPSF.backend.review.ObrazacResponse;
import IndirektniPSF.backend.review.ValidOrStorno;
import IndirektniPSF.backend.zakljucniList.ZakljucniListDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.IntStream;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class ObrazacIOMapper {

    static private final Locale SR_RS = new Locale("sr", "RS");


    public ObrazacIODetails toEntity(ObrazacIODTO obrazacIODTO) {



        Integer konto = obrazacIODTO.getKonto();
        Double dugg =
                (konto >= 400000 && konto <= 699999) ? obrazacIODTO.getPlan() : 0;
        Double potg =
                (konto < 400000 || konto > 699999) ? obrazacIODTO.getPlan() : 0;
        Double duguje =
                (konto >= 400000 && konto <= 699999) ? obrazacIODTO.getIzvrsenje() : 0;
        Double potrazuje =
                (konto < 400000 || konto > 699999) ? obrazacIODTO.getIzvrsenje() : 0;
        return
        ObrazacIODetails.builder()
                .RED_BROJ_AKT(obrazacIODTO.getRedBrojAkt())
                .FUNK_KLAS(obrazacIODTO.getFunkKlas())
                .SIN_KONTO(konto / 100)
                .KONTO(konto)
                .IZVORFIN(obrazacIODTO.getIzvorFin())
                .IZVORFIN_PRE(obrazacIODTO.getIzvorFinPre())
                .ALINEA(0)
                .DUGG(dugg)
                .POTG(potg)
                .DUGUJE(duguje)
                .POTRAZUJE(potrazuje)
                .REPUBLIKA(0.0)
                .POKRAJINA(0.0)
                .OPSTINA(0.0)
                .OOSO(0.0)
                .DONACIJE(0.0)
                .OSTALI(0.0)
                .UPARENO(0)
                .POTRAZUJE2(0.00)
                .build();
    }

        private void assignStringValue(ObrazacIODTO dto, int columnIndex, String value) {
            switch (columnIndex) {
                case 1:
                    dto.setFunkKlas(value);
                    break;
                case 3:
                    dto.setIzvorFin(value);
                    break;
                case 4:
                    dto.setIzvorFinPre(value);
                    break;
            }
        }
 //TODO
 public List<ObrazacIODTO> mapExcelToPojo(InputStream inputStream) {
     try (Workbook workbook = WorkbookFactory.create(inputStream)) {
         Sheet sheet = workbook.getSheetAt(0);

         FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
         DataFormatter formatter = new DataFormatter(new Locale("sr", "RS"));

         return readRows(sheet, formatter, evaluator);
     } catch (RuntimeException e) {
         throw e;
     } catch (Exception e) {
         throw new IllegalStateException("Podaci iz excel tabele nisu uspesno ucitani", e);
     }
 }

    private List<ObrazacIODTO> readRows(Sheet sheet, DataFormatter formatter, FormulaEvaluator evaluator) {
        List<ObrazacIODTO> dtos = new ArrayList<>();

        int startRow = 7; // 0-based index
        for (int r = startRow; r <= sheet.getLastRowNum(); r++) {
            Row row = sheet.getRow(r);
            if (row == null) break;

            // If first column is empty -> skip/continue (same behavior as your code)
            if (isBlank(row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL), formatter, evaluator)) {
                continue;
            }

            // Build DTO with validation
            ObrazacIODTO dto = new ObrazacIODTO();

            dto.setRedBrojAkt(readRequiredInt(row, r, 0, "Red broj aktivnosti", formatter, evaluator, null, null));
            dto.setFunkKlas(readRequiredString(row, r, 1, "Funkcionalna klasifikacija", formatter, evaluator, dto.getRedBrojAkt(), null));
            dto.setKonto(readRequiredInt(row, r, 2, "Konto", formatter, evaluator, dto.getRedBrojAkt(), null));
            dto.setIzvorFin(readRequiredString(row, r, 3, "Izvor finansiranja", formatter, evaluator, dto.getRedBrojAkt(), dto.getKonto()));
            dto.setIzvorFinPre(readRequiredString(row, r, 4, "Izvor finansiranja (pre)", formatter, evaluator, dto.getRedBrojAkt(), dto.getKonto()));

            // Plan / Izvrsenje: allow blank => 0.0, but if present must be a valid number
            dto.setPlan(readOptionalDecimal(row, r, 5, "Plan", formatter, evaluator, dto.getRedBrojAkt(), dto.getKonto())
                    .orElse(BigDecimal.ZERO).doubleValue());

            dto.setIzvrsenje(readOptionalDecimal(row, r, 6, "Izvršenje", formatter, evaluator, dto.getRedBrojAkt(), dto.getKonto())
                    .orElse(BigDecimal.ZERO).doubleValue());

            dtos.add(dto);
        }

        return dtos;
    }

// -------------------- typed readers with good error messages --------------------

    private Integer readRequiredInt(Row row, int r, int c, String colName,
                                    DataFormatter formatter, FormulaEvaluator evaluator,
                                    Integer redBrojAkt, Integer konto) {
        String raw = getText(row, c, formatter, evaluator);
        if (raw.isBlank()) {
            throw excelTypeError(r, c, colName, "broj (ceo)", raw, redBrojAkt, konto);
        }

        // Accept "123" only. If you want to allow "123,00" adjust here.
        try {
            // remove spaces and NBSP
            String normalized = raw.replace("\u00A0", "").replace(" ", "");
            if (!normalized.matches("[-+]?\\d+")) {
                throw new NumberFormatException("Not an integer: " + raw);
            }
            return Integer.parseInt(normalized);
        } catch (Exception e) {
            throw excelTypeError(r, c, colName, "broj (ceo)", raw, redBrojAkt, konto);
        }
    }

    private String readRequiredString(Row row, int r, int c, String colName,
                                      DataFormatter formatter, FormulaEvaluator evaluator,
                                      Integer redBrojAkt, Integer konto) {
        String raw = getText(row, c, formatter, evaluator);
        if (raw.isBlank()) {
            throw excelTypeError(r, c, colName, "tekst", raw, redBrojAkt, konto);
        }
        return raw.trim();
    }

    private Optional<BigDecimal> readOptionalDecimal(Row row, int r, int c, String colName,
                                                     DataFormatter formatter, FormulaEvaluator evaluator,
                                                     Integer redBrojAkt, Integer konto) {
        String raw = getText(row, c, formatter, evaluator);
        if (raw.isBlank()) return Optional.empty();

        try {
            return Optional.of(parseSmartDecimal(raw));
        } catch (Exception e) {
            throw excelTypeError(r, c, colName, "broj (npr. 1234,56)", raw, redBrojAkt, konto);
        }
    }

    private String getText(Row row, int c, DataFormatter formatter, FormulaEvaluator evaluator) {
        Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) return "";

        // For numeric/formula cells, DataFormatter gives the displayed value,
        // but numeric accuracy is handled by parseSmartDecimal / validation.
        return formatter.formatCellValue(cell, evaluator).trim();
    }

    private boolean isBlank(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (cell == null) return true;
        return formatter.formatCellValue(cell, evaluator).trim().isBlank();
    }

    /**
     * Handles:
     *  - "18.739.577,83"  (dot grouping, comma decimal)
     *  - "18,739,577.83"  (comma grouping, dot decimal)
     *  - "18739577,83" / "18739577.83"
     */
    private BigDecimal parseSmartDecimal(String raw) {
        String v = raw.replace("\u00A0", "").replace(" ", "");

        int lastDot = v.lastIndexOf('.');
        int lastComma = v.lastIndexOf(',');

        if (lastDot >= 0 && lastComma >= 0) {
            // choose decimal separator by the last occurrence
            if (lastComma > lastDot) {
                // "18.739.577,83"
                return new BigDecimal(v.replace(".", "").replace(",", "."));
            } else {
                // "18,739,577.83"
                return new BigDecimal(v.replace(",", ""));
            }
        }
        if (lastComma >= 0) {
            // treat comma as decimal
            return new BigDecimal(v.replace(".", "").replace(",", "."));
        }
        // only dot or plain
        return new BigDecimal(v.replace(",", ""));
    }

    private RuntimeException excelTypeError(int rowIndex0Based, int colIndex0Based, String colName,
                                            String expectedType, String rawValue,
                                            Integer redBrojAkt, Integer konto) {
        int excelRow = rowIndex0Based + 1; // Excel rows are 1-based
        String colLetter = CellReference.convertNumToColString(colIndex0Based);
        String ctx = (redBrojAkt != null || konto != null)
                ? String.format(" (redBrojAkt=%s, konto=%s)", redBrojAkt, konto)
                : "";

        String msg = String.format(
                "Neispravan tip/vrednost u Excel-u na %s%d (%s)%s. Očekivano: %s. Pronađeno: '%s'.",
                colLetter, excelRow, colName, ctx, expectedType, rawValue
        );
        return new IllegalArgumentException(msg);
    }


// chat GPT version
//    public List<ObrazacIODTO> mapExcelToPojo(InputStream inputStream) {
//        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
//            Sheet sheet = workbook.getSheetAt(0);
//
//            DataFormatter formatter = new DataFormatter();
//            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
//
//            int startRow = 7;
//            int lastRow = sheet.getLastRowNum();
//
//            return IntStream.rangeClosed(startRow, lastRow)
//                    .mapToObj(sheet::getRow)
//                    .takeWhile(row -> row != null && !isBlankRowStart(row, formatter, evaluator))
//                    .map(row -> toDto(row, formatter, evaluator))
//                    .toList();
//
//        } catch (Exception e) {
//            throw new IllegalStateException("Podaci iz excel tabele nisu uspesno ucitani", e);
//        }
//    }

    private ObrazacIODTO toDto(Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
        ObrazacIODTO dto = new ObrazacIODTO();

        dto.setRedBrojAkt(readInt(row.getCell(0), formatter, evaluator).orElse(null));
        dto.setFunkKlas(readString(row.getCell(1), formatter, evaluator).orElse(null));
        dto.setKonto(readInt(row.getCell(2), formatter, evaluator).orElse(null));
        dto.setIzvorFin(readString(row.getCell(3), formatter, evaluator).orElse(null));
        dto.setIzvorFinPre(readString(row.getCell(4), formatter, evaluator).orElse(null));

        // IMPORTANT: plan/izvrsenje can be NUMERIC or STRING like "11.753.353,93"
        dto.setPlan(readDouble(row.getCell(5), formatter, evaluator).orElse(0.0));
        dto.setIzvrsenje(readDouble(row.getCell(6), formatter, evaluator).orElse(0.0));

        return dto;
    }

    private boolean isBlankRowStart(Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
        // stop when first column is blank
        return readString(row.getCell(0), formatter, evaluator)
                .map(String::trim)
                .map(String::isEmpty)
                .orElse(true);
    }

    private Optional<String> readString(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (cell == null) return Optional.empty();
        String v = formatter.formatCellValue(cell, evaluator);
        return (v == null || v.trim().isEmpty()) ? Optional.empty() : Optional.of(v.trim());
    }

    private Optional<Integer> readInt(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        return readDouble(cell, formatter, evaluator).map(d -> (int) Math.round(d));
    }

    private Optional<Double> readDouble(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        return readString(cell, formatter, evaluator).map(this::parseLocalizedNumber);
    }

    private double parseLocalizedNumber(String raw) {
        String s = raw.replace("\u00A0", " ").trim();

        // 1) Try sr-RS parsing (handles 11.753.353,93)
        try {
            NumberFormat nf = NumberFormat.getInstance(SR_RS);
            nf.setGroupingUsed(true);
            return nf.parse(s).doubleValue();
        } catch (ParseException ignored) {
            // 2) Fallback normalize: remove grouping '.', decimal ',' -> '.'
            String normalized = s.replace(".", "").replace(",", ".");
            return Double.parseDouble(normalized);
        }
    }
//    public List<ObrazacIODTO> mapExcelToPojo(InputStream inputStream) {
//        List<ObrazacIODTO> dtos = new ArrayList<>();
//        DataFormatter formatter = new DataFormatter();
//        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
//            Sheet sheet = workbook.getSheetAt(0);
//            int i = 7;
//            while (i <= sheet.getLastRowNum()) {
//                Row row = sheet.getRow(i);
//                if (row == null) {
//                    break;
//                }
//                ObrazacIODTO dto = new ObrazacIODTO();
//
//                Cell cell0 = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
//                if (cell0 != null && cell0.getCellType() != CellType.BLANK) {
//                    dto.setRedBrojAkt(Integer.parseInt(formatter.formatCellValue(cell0)));
//                } else {
//                    i++;
//                    continue;
//                }
//                dto.setFunkKlas(formatter.formatCellValue(row.getCell(1)));
//
//                dto.setKonto(Integer.valueOf(formatter.formatCellValue(row.getCell(2))));
//
//                dto.setIzvorFin(formatter.formatCellValue(row.getCell(3)));
//                dto.setIzvorFinPre(formatter.formatCellValue(row.getCell(4)));
//
//                Cell planCell = row.getCell(5);
//                if (planCell != null && planCell.getCellType() == CellType.NUMERIC) {
//                    dto.setPlan(planCell.getNumericCellValue());
//                }
//
//                Cell izvrsenjeCell = row.getCell(6);
//                if (izvrsenjeCell != null && izvrsenjeCell.getCellType() == CellType.NUMERIC) {
//                    dto.setIzvrsenje(izvrsenjeCell.getNumericCellValue());
//                }
//
//                dtos.add(dto);
//                i++;
//            }
//        } catch (Exception e) {
//            throw new IllegalStateException("Podaci iz excel tabele nisu uspesno ucitani", e);
//        }
//        return dtos;
//    }

    public ObrazacResponse toResponse(ObrazacIO zb) {
        LocalDate date = LocalDate.ofEpochDay(zb.getDATUM_DOK() - 25569);
        return ObrazacResponse.builder()
                .id(zb.getGEN_MYSQL())
                .date(date)
                .kvartal(zb.getKOJI_KVARTAL())
                //.year(zb.())
                .version(zb.getVERZIJA())
                .status(zb.getSTATUS())
                .jbbk(zb.getJBBK_IND_KOR())
                .storno(zb.getSTORNO() == 0 ? ValidOrStorno.VALIDAN : ValidOrStorno.STORNIRAN)
                .build();
    }

    public ObrazacIODTO toDto(ObrazacIODetails ioDetails) {

        return ObrazacIODTO.builder()
                .redBrojAkt(ioDetails.getRED_BROJ_AKT())
                .funkKlas(ioDetails.getFUNK_KLAS())
                .konto(ioDetails.getKONTO())
                .izvorFin(ioDetails.getIZVORFIN())
                .izvorFinPre(ioDetails.getIZVORFIN_PRE())
                .plan(ioDetails.getDUGG() + ioDetails.getPOTG())
                .izvrsenje(ioDetails.getDUGUJE() + ioDetails.getPOTRAZUJE())
                .build();

    }

    public ObrazacIODTO toDtoFromArh(Arhbudzet arhbudzet) {
        return ObrazacIODTO.builder()
                .redBrojAkt(arhbudzet.getRedBrojAkt())
                .funkKlas(arhbudzet.getFunkKlas().toString())
                .konto(arhbudzet.getSinKonto())
                .izvorFin(arhbudzet.getIzvor().getIZVORFIN())
                .build();
    }

    public ObrazacIODTO toDtoFromArhWithPlan(Arhbudzet arh) {
        var dto = toDtoFromArh(arh);
        dto.setPlan(arh.getDuguje());
        return dto;
    }

}
