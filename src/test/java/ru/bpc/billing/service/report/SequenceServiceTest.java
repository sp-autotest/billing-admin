package ru.bpc.billing.service.report;

import org.junit.Test;
import ru.bpc.billing.AbstractTest;

/**
 * User: Krainov
 * Date: 11.04.2016
 * Time: 15:46
 */
public class SequenceServiceTest extends AbstractTest {

    @Test
    public void testExist() {
        assertNotNull(sequenceService);
    }

    @Test
    public void testNextForUtrnno() {
        long n1 = sequenceService.nextForUtrnno();
        assertTrue(n1 > 10000);
    }
}
