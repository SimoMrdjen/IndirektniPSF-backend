package IndirektniPSF.backend.parameters;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class StatusServiceTest {

    @InjectMocks
    StatusService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void raiseStatusDependentOfActuallStatus() {
    }

    @Test
    void resolveObrazacAccordingStatus() {
    }

    @Test
    void resolveObrazacAccordingNextObrazac() {
    }

    @Test
    void resolveObrazacAccordingPreviousObrazac() {
    }
}