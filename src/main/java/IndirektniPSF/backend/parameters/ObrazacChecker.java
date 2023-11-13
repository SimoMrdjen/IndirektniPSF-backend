package IndirektniPSF.backend.parameters;

import IndirektniPSF.backend.obrazac5.obrazac5.Obrazac5;

public interface ObrazacChecker {

     default <T extends StatusUpdatable>void isObrazacSentToDBK(T zb) throws Exception {

        if (zb.getSTATUS() >= 20) {
            throw new Exception("Obrazac je vec poslat vasem DBK-u");
        }
    }

    default <T extends StatusUpdatable> void isObrazacStorniran(T zb) throws Exception {
        if (zb.getSTORNO() == 1) {
            throw new Exception("Obrazac je storniran , \n`morate ucitati novu verziju!");
        }
    }
}
