package IndirektniPSF.backend.IOobrazac.obrazacIODetails;

import IndirektniPSF.backend.IOobrazac.ObrazacIODTO;
import IndirektniPSF.backend.arhbudzet.Arhbudzet;
import IndirektniPSF.backend.izvor.Izvor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class ObrazacIOMapperTest {

    @InjectMocks
    ObrazacIOMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @AfterEach
    void tearDown() {
    }

//    @Test
//    void testtoDtoFromArh() {
//
//        Arhbudzet arh = new Arhbudzet(
//                1,
//                "Nalog001",
//                100L,
//                5000.0,
//                101,
//                4001,
//                20200101.0,
//                "Glava01",
//                5,
//                new Izvor("01","budz"),
//                10,
//                200,
//                1
//        );
//        ObrazacIODTO dto =  ObrazacIODTO.builder()
//                .izvorFin("01")
//                .funkKlas("200")
//                .konto(4001)
//                .redBrojAkt(10)
//                .build();
//
//        System.out.println(mapper.toDtoFromArh(arh));
//        assertEquals(dto,mapper.toDtoFromArh(arh));
//    }
}