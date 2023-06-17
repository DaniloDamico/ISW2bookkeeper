package org.apache.bookkeeper.bookie;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.bookkeeper.net.BookieId;
import org.apache.bookkeeper.proto.BookkeeperInternalCallbacks.WriteCallback;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class AddEntryTest {
    private BookieImpl bookie;
    ByteBuf entry;
    boolean ackBeforeSync;
    WriteCallback cb;
    Object ctx;
    byte[] masterKey;
    boolean exception;

    @Mock
    private static WriteCallback valid;

    @Before
    public void setUp() throws Exception {

        this.bookie = utilities.getBookieImpl();

        this.bookie.start();

        valid = Mockito.mock(WriteCallback.class);

        Mockito.doNothing().when(valid).writeComplete(Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong(), Mockito.any(BookieId.class), Mockito.any());
    }

    @After
    public void tearDown() {
        bookie.shutdown(); //gracefully, does not clean resources
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters(){

        //public void addEntry(ByteBuf entry, boolean ackBeforeSync, WriteCallback cb, Object ctx, byte[] masterKey)

        // Category Partition
        // ByteBuf è il ledgerID: valid, empty, null, invalid
        // due volte lo stesso ID?
        ByteBuf validByteBuf = createByteBuf(0,0);

        ByteBuf emptyBuf = Unpooled.EMPTY_BUFFER;
        ByteBuf invalidBuf = Unpooled.copiedBuffer("invalid".getBytes());

        ByteBuf validByteBuf2 = createByteBuf(0, 1);
        ByteBuf validByteBuf3 = createByteBuf(0, 2);
        ByteBuf validByteBuf4 = createByteBuf(0, 3);
        ByteBuf validByteBuf5 = createByteBuf(0, 4);
        ByteBuf validByteBuf6 = createByteBuf(0, 5);

        //fence ledgerID
        ByteBuf invalidEntry = createByteBuf(0, -1);
        ByteBuf invalidLedger = createByteBuf(-1, 6);

        // boolean ackBeforeSync: true, false

        // WriteCallback: valid, null

        // Object: valid, null
        Object validObj = new Object();

        // byte[]: valid, empty, null
        byte[] validKey = "validKey".getBytes();
        byte[] emptyKey = new byte[0];



        return Arrays.asList(new Object[][]{
                //ByteBuf,      ackBeforeSync,  WriteCallback,  Object,     byte[],     expectedException
                {validByteBuf,  true,           valid,          validObj,   validKey,   false},             // tutto valido
                {validByteBuf,  true,           valid,          validObj,   validKey,   true},              // inserisco un id già presente -> fallimento
                {validByteBuf2, false,          valid,          validObj,   validKey,   false},             // ackBeforeSync = false
                {emptyBuf,      true,           valid,          validObj,   validKey,   true},              // ByteBuf vuoto
                {invalidBuf,    true,           valid,          validObj,   validKey,   true},              // ByteBuf non valido
                {null,          true,           valid,          validObj,   validKey,   true},              // ByteBuf null
                {validByteBuf3, true,           null,           validObj,   validKey,   false},             // WriteCallback null
                {validByteBuf4, true,           valid,          null,       validKey,   false},             // Object null
                {validByteBuf5, true,           valid,          validObj,   emptyKey,   false},             // byte[] empty
                {validByteBuf6, true,           valid,          validObj,   null,       true},              // byte[] null
                {invalidEntry,  true,           valid,          validObj,   null,       true},              // ByteBuf entry non valido
                {invalidLedger, true,           valid,          validObj,   null,       true},              // ByteBuf ledger non valido
        });
    }

    public AddEntryTest(ByteBuf entry, boolean ackBeforeSync, WriteCallback cb, Object ctx, byte[] masterKey, boolean exception){
        this.entry = entry;
        this.ackBeforeSync = ackBeforeSync;
        this.cb = cb;
        this.ctx = ctx;
        this.masterKey = masterKey;
        this.exception = exception;
    }

    @Test
    public void testAddEntry() {
        try {
            bookie.addEntry(entry, ackBeforeSync, valid, ctx, masterKey);

            //bookie.readEntry(ledger, entry);
            long firstNumber = entry.getLong(0);
            long secondNumber = entry.getLong(8);
            String expected = "ledger: " + firstNumber + "entry: " + secondNumber;
            System.out.println(expected);

            ByteBuf byteBuf = this.bookie.readEntry(firstNumber, secondNumber);

            byte[] data = new byte[byteBuf.readableBytes() - 16];
            byteBuf.getBytes(16, data);
            String dataString = new String(data);

            Assert.assertEquals(expected, dataString);

        } catch (Exception e) {
            e.printStackTrace();
            assert(exception);
            return;
        }
        assert(!exception);
    }

    protected static ByteBuf createByteBuf(long ledger, long entry){
        byte[] data = ("ledger: " + ledger + "entry: " + entry).getBytes();
        ByteBuf validByteBuf = Unpooled.buffer(8 + 8 + data.length);
        validByteBuf.writeLong(ledger);
        validByteBuf.writeLong(entry);
        validByteBuf.writeBytes(data);
        return validByteBuf;
    }

}

