package dk.dma.ais.message.binary;

import dk.dma.ais.binary.SixbitException;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage8;
import dk.dma.ais.message.AisMessageException;
import dk.dma.ais.message.AisPosition;
import dk.dma.ais.sentence.SentenceException;
import dk.dma.ais.sentence.Vdm;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BroadcastIntendedRouteTest {

    AisMessage8 syntheticAisMessage8;

    @Before
    public void setup() {
        // Build synthetic AIS message
        syntheticAisMessage8 = new AisMessage8();
        syntheticAisMessage8.setRepeat(0);
        syntheticAisMessage8.setUserId(219000001);
        syntheticAisMessage8.setSpare(0);
        syntheticAisMessage8.setDac(BroadcastIntendedRoute.DAC);
        syntheticAisMessage8.setFi(BroadcastIntendedRoute.FI);
    }

    @Test
    public void testEncodeCancelRoute() throws SixbitException, SentenceException {
        // Build synthetic message
        BroadcastIntendedRoute asm = new BroadcastIntendedRoute();
        asm.setDuration(60);
        asm.setStartDay(1);
        asm.setStartHour(2);
        asm.setStartMin(30);
        asm.setStartMonth(8);
        asm.setWaypointCount(0);
        asm.setWaypoints(Collections.<AisPosition> emptyList());
        syntheticAisMessage8.setAppMessage(asm);

        // Test binary encoding
        String encodedBinArray = syntheticAisMessage8.getEncoded().getBinArray().toString();

        System.out.println(encodedBinArray);

        assertEquals(99, encodedBinArray.length());  // As per http://www.e-navigation.nl/content/intended-route
        assertEquals("001000", encodedBinArray.substring(0, 6)); // Msg ID 8
        assertEquals("00", encodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", encodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", encodedBinArray.substring(38, 40)); // Spare
        assertEquals("0011011011", encodedBinArray.substring(40, 50)); // DAC=219
        assertEquals("000001", encodedBinArray.substring(50, 56)); // FI=1

        // Test NMEA encoding
        String[] sentences = Vdm.createSentences(syntheticAisMessage8, 0);
        assertEquals(1, sentences.length);
        assertEquals("!AIVDM,1,1,0,,83@ndh@nhH29p03h0,3*53", sentences[0]);
    }

    @Test
    public void testDecodeCancelRoute() throws SixbitException, SentenceException, AisMessageException {
        // Build synthetic AIS message
        Vdm vdm = new Vdm();
        vdm.parse("!AIVDM,1,1,0,,83@ndh@nhH29p03h0,3*53");

        // Test decoded bin array
        String decodedBinArray = vdm.getBinArray().toString();
        assertEquals(99, decodedBinArray.length());  // As per http://www.e-navigation.nl/content/intended-route
        assertEquals("001000", decodedBinArray.substring(0, 6)); // Msg ID 8
        assertEquals("00", decodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", decodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", decodedBinArray.substring(38, 40)); // Spare
        assertEquals("0011011011", decodedBinArray.substring(40, 50)); // DAC=219
        assertEquals("000001", decodedBinArray.substring(50, 56)); // FI=1

        // Test decoded values
        AisMessage aisMessage = AisMessage.getInstance(vdm);
        assertTrue(aisMessage instanceof AisMessage8);
        AisMessage8 msg8 = (AisMessage8) aisMessage;
        assertEquals(syntheticAisMessage8.getRepeat(), msg8.getRepeat());
        assertEquals(syntheticAisMessage8.getUserId(), msg8.getUserId());
        assertEquals(syntheticAisMessage8.getSpare(), msg8.getSpare());
        assertEquals(syntheticAisMessage8.getDac(), msg8.getDac());
        assertEquals(syntheticAisMessage8.getFi(), msg8.getFi());

        AisApplicationMessage decodedAsm = msg8.getApplicationMessage();
        assertNotNull(decodedAsm);
        assertTrue(decodedAsm instanceof BroadcastIntendedRoute);
        BroadcastIntendedRoute broadcastIntendedRoute = (BroadcastIntendedRoute) decodedAsm;
        assertEquals(60, broadcastIntendedRoute.getDuration());
    }

}
