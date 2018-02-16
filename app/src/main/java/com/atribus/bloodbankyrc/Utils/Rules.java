package com.atribus.bloodbankyrc.Utils;

import java.util.Arrays;

/**
 * Created by root on 12/2/18.
 */

public class Rules {

    String Userblood;

    String Onegative[] = new String[]{"AB+", "AB-", "A+", "A-", "B+", "B-", "O+", "O-"};
    String Opostive[] = new String[]{"AB+", "A+", "B+", "O+"};

    String Bnegative[] = new String[]{"AB+", "AB-", "B+", "B-"};
    String Bpositive[] = new String[]{"AB+", "B+"};

    String Anegative[] = new String[]{"AB+", "AB-", "A+", "A-"};
    String Apositive[] = new String[]{"AB+", "A+"};

    String ABnegative[] = new String[]{"AB+", "AB-"};
    String ABpositive[] = new String[]{"AB+"};


    Rules() {

    }

    boolean rules(String userblood, String requiredblood) {
        switch (userblood) {
            case "O-":
                if (Arrays.asList(Onegative).contains(requiredblood))
                    return true;
                break;

            case "O+":
                if (Arrays.asList(Opostive).contains(requiredblood))
                    return true;
                break;

            case "B-":
                if (Arrays.asList(Bnegative).contains(requiredblood))
                    return true;
                break;

            case "B+":
                if (Arrays.asList(Bpositive).contains(requiredblood))
                    return true;
                break;

            case "A-":
                if (Arrays.asList(Anegative).contains(requiredblood))
                    return true;
                break;

            case "A+":
                if (Arrays.asList(Apositive).contains(requiredblood))
                    return true;
                break;

            case "AB-":
                if (Arrays.asList(ABnegative).contains(requiredblood))
                    return true;
                break;

            case "AB+":
                if (Arrays.asList(ABpositive).contains(requiredblood))
                    return true;
                break;

          default:
              return false;
        }
return false;

    }

}

