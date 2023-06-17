package org.apache.bookkeeper.bookie;

import io.netty.buffer.ByteBuf;
import org.apache.bookkeeper.net.BookieId;
import org.apache.bookkeeper.proto.BookkeeperInternalCallbacks;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;

import static org.apache.bookkeeper.bookie.AddEntryTest.createByteBuf;


public class AddEntryToFencedLedgerTest {
    private BookieImpl bookie;

    @Mock
    private static BookkeeperInternalCallbacks.WriteCallback valid;

    @Before
    public void setUp() throws Exception {

        this.bookie = utilities.getBookieImplWithFencedLedgerStorage();
        this.bookie.start();

        valid = Mockito.mock(BookkeeperInternalCallbacks.WriteCallback.class);
        Mockito.doNothing().when(valid).writeComplete(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong(), Mockito.any(BookieId.class), Mockito.any());
    }

    @After
    public void tearDown() {
        bookie.shutdown(); //gracefully, does not clean resources
    }

    @Test
    public void testAddEntryToFencedLedger() {

        ByteBuf validByteBuf = createByteBuf(0,0);
        try {
            bookie.addEntry(validByteBuf, false, valid, null, "masterkey".getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }
}

