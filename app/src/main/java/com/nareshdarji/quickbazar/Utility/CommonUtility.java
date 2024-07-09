package com.nareshdarji.quickbazar.Utility;

import java.util.UUID;

public class CommonUtility {

    public static String generateUID() {
        return UUID.randomUUID().toString();
    }

}
