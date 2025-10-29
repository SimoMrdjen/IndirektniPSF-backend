package IndirektniPSF.backend.zakljucniList.zb;

import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIORepository;
import IndirektniPSF.backend.IOobrazac.obrazacIO.ObrazacIOService;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIODetailService;
import IndirektniPSF.backend.IOobrazac.obrazacIODetails.ObrazacIOMapper;
import IndirektniPSF.backend.arhbudzet.ArhbudzetService;
import IndirektniPSF.backend.excel.ExcelService;
import IndirektniPSF.backend.exceptions.ObrazacException;
import IndirektniPSF.backend.glavaSvi.GlavaSviService;
import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5Service;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.parameters.StatusService;
import IndirektniPSF.backend.security.user.UserRepository;
import IndirektniPSF.backend.zakljucniList.ZakljucniListDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZakljucniListZbServiceTest {

    @InjectMocks
    ZakljucniListZbService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldNotThrowWhenCheckIfKonto999999Exist() throws ObrazacException {
        var dto = ZakljucniListDto.builder()
                .konto("999998")
                .build();
        var dtos = List.of(dto);
        assertDoesNotThrow(() -> service.checkIfKonto999999Exist(dtos));
    }

    @Test
    void shouldThrowWhenKonto999999Exists() {
        var dto = ZakljucniListDto.builder()
                .konto("999999")
                .build();
        var dtos = List.of(dto);

        ObrazacException exception = assertThrows(ObrazacException.class, () -> service.checkIfKonto999999Exist(dtos));
        assertEquals("U obrascu imate konto 999999!", exception.getMessage());
    }
}