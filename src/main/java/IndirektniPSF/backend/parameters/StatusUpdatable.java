package IndirektniPSF.backend.parameters;

public interface StatusUpdatable {
    Integer getSTATUS();
    void setSTATUS(Integer status);
    void setPODIGAO_STATUS(Integer sifraradnika);
    void setPOSLAO_NAM(Integer sifraradnika);

    Integer getSTORNO();
}
