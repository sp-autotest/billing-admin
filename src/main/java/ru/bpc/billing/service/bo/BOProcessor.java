package ru.bpc.billing.service.bo;

import ru.bpc.billing.service.ISystem;

import java.io.File;
import java.io.IOException;

/**
 * User: Krainov
 * Date: 15.08.14
 * Time: 11:48
 */
public interface BOProcessor extends ISystem {

    public BOProcessingResult process(File file) throws IOException;
}
