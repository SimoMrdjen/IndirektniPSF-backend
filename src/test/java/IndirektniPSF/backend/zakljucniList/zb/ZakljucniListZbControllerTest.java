package IndirektniPSF.backend.zakljucniList.zb;

import IndirektniPSF.backend.excel.ExcelService;
import IndirektniPSF.backend.fileUpload.FileUploadService;
import IndirektniPSF.backend.kontrole.obrazac.ObrKontrService;
import IndirektniPSF.backend.obrazac5.ppartner.PPartnerService;
import IndirektniPSF.backend.obrazac5.sekretarijat.SekretarijarService;
import IndirektniPSF.backend.security.auth.AuthenticationService;
import IndirektniPSF.backend.security.user.UserRepository;
import IndirektniPSF.backend.zakljucniList.details.ZakljucniDetailsService;
import IndirektniPSF.backend.zakljucniList.details.ZakljucniListDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = {ZakljucniListZbController.class, ZakljucniListZbService.class})
class ZakljucniListZbControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ZakljucniListZbRepository repository;

    @MockBean
    private SekretarijarService sekretarijarService;

    @MockBean
    private PPartnerService pPartnerService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ZakljucniDetailsService zakljucniDetailsService;

    @MockBean
    private ObrKontrService obrKontrService;

    @MockBean
    private ExcelService excelService;

    @MockBean
    private FileUploadService fileUploadService;

    @MockBean
    private  ZakljucniListZbService zakljucniListZbService;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    public void shouldGetZakList() throws Exception {
        // Mocking the service call
//        when(zakljucniListZbService.findValidObrazacToRaise(anyString(), anyInt(), anyInt()))
//                .thenReturn(Collections.emptyList());
//
//        // Perform the request
//        mockMvc.perform(get("/api/zakljucni_list")
//                        .param("status", String.valueOf(10))
//                        .param("kvartal", String.valueOf(1))
//                        .with(user("user").password("password").roles("USER"))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
    }

}
