package com.clouiotech.pda.demo.interfaces;

import java.io.Serializable;

/**
 * Created by roka on 24/01/17.
 */

public interface QrCodeDetectListener extends Serializable {
    void onQrCodeDetected(String data);
}
