package dk.dma.ais.message.binary;

import dk.dma.ais.binary.BinArray;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage6;
import dk.dma.ais.message.AisPosition;
import dk.dma.ais.sentence.Vdm;
import dk.dma.enav.model.geometry.Position;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TacticalVoyagePlanInquiryTest {

    @Test
    public void testEncodeWithNoArea() throws Exception {
        // Build synthetic AIS message
        AisMessage6 msg6 = createSyntheticAisMessage6();

        // Test binary encoding
        String encodedBinArray = msg6.getEncoded().getBinArray().toString();
        assertEquals(88, encodedBinArray.length());
        validateEncoding(encodedBinArray);

        // Test NMEA encoding
        String[] sentences = Vdm.createSentences(msg6, 0);
        assertEquals(1, sentences.length);
        assertEquals("!AIVDM,1,1,0,,63@ndh@l=v9P=dD,2*61", sentences[0]);

        // Test NMEA decoding
        Vdm vdm = new Vdm();
        int incomplete = vdm.parse(sentences[0]);
        assertEquals(0, incomplete);

        // Test decoded bin array
        BinArray decodedBinArray = vdm.getBinArray();
        assertEquals(encodedBinArray.toString(), decodedBinArray.toString());

        // Test decoded values
        AisMessage aisMessage = AisMessage.getInstance(vdm);
        assertTrue(aisMessage instanceof AisMessage6);
        AisMessage6 decodedMsg6 = (AisMessage6) aisMessage;
        assertEquals(msg6.getRepeat(), decodedMsg6.getRepeat());
        assertEquals(msg6.getUserId(), decodedMsg6.getUserId());
        assertEquals(msg6.getSeqNum(), decodedMsg6.getSeqNum());
        assertEquals(msg6.getDestination(), decodedMsg6.getDestination());
        assertEquals(msg6.getRetransmit(), decodedMsg6.getRetransmit());
        assertEquals(msg6.getSpare(), decodedMsg6.getSpare());
        assertEquals(msg6.getDac(), decodedMsg6.getDac());
        assertEquals(msg6.getFi(), decodedMsg6.getFi());

        AisApplicationMessage decodedAsm = decodedMsg6.getApplicationMessage();
        assertNotNull(decodedAsm);
        assertTrue(decodedAsm instanceof TacticalVoyagePlanInquiry);
        TacticalVoyagePlanInquiry decodedTacticalVoyagePlanInquiry = (TacticalVoyagePlanInquiry) decodedAsm;
        assertNull(decodedTacticalVoyagePlanInquiry.getControlledAreaNorthWest());
        assertNull(decodedTacticalVoyagePlanInquiry.getControlledAreaSouthEast());
    }

    @Test
    public void testEncodeWithArea() throws Exception {
        // Build synthetic AIS message
        AisMessage6 msg6 = createSyntheticAisMessage6();
        TacticalVoyagePlanInquiry asm = new TacticalVoyagePlanInquiry();
        asm.setControlledAreaNorthWest(new AisPosition(Position.create(55.8341, 9.9462)));
        asm.setControlledAreaSouthEast(new AisPosition(Position.create(55.8111, 9.9932)));
        msg6.setAppMessage(asm);

        // Test binary encoding
        String binArray = msg6.getEncoded().getBinArray().toString();
        assertEquals(198, binArray.length());
        validateEncoding(binArray);
        assertEquals("0001111111110010110100101100", binArray.substring(88, 116));  // North
        assertEquals("000010110110111110110010000", binArray.substring(116, 143));  // East
        assertEquals("0001111111101111011101000100", binArray.substring(143, 171)); // South
        assertEquals("000010110110000111101101000", binArray.substring(171, 198));  // West

        // Test NMEA encoding
        String[] sentences = Vdm.createSentences(msg6, 0);
        assertEquals(1, sentences.length);
        assertEquals("!AIVDM,1,1,0,,63@ndh@l=v9P=dDOte;0egdP?us`PFhu`,0*32", sentences[0]);

        // Test NMEA decoding
        Vdm vdm = new Vdm();
        int incomplete = vdm.parse(sentences[0]);

        assertEquals(0, incomplete);
        AisMessage aisMessage = AisMessage.getInstance(vdm);
        assertTrue(aisMessage instanceof AisMessage6);
        AisMessage6 decodedMsg6 = (AisMessage6) aisMessage;
        assertEquals(msg6.getRepeat(), decodedMsg6.getRepeat());
        assertEquals(msg6.getUserId(), decodedMsg6.getUserId());
        assertEquals(msg6.getSeqNum(), decodedMsg6.getSeqNum());
        assertEquals(msg6.getDestination(), decodedMsg6.getDestination());
        assertEquals(msg6.getRetransmit(), decodedMsg6.getRetransmit());
        assertEquals(msg6.getSpare(), decodedMsg6.getSpare());
        assertEquals(msg6.getDac(), decodedMsg6.getDac());
        assertEquals(msg6.getFi(), decodedMsg6.getFi());

        AisApplicationMessage decodedAsm = decodedMsg6.getApplicationMessage();
        assertNotNull(decodedAsm);
        assertTrue(decodedAsm instanceof TacticalVoyagePlanInquiry);
        TacticalVoyagePlanInquiry decodedTacticalVoyagePlanInquiry = (TacticalVoyagePlanInquiry) decodedAsm;

        AisPosition decodedNorthWest = decodedTacticalVoyagePlanInquiry.getControlledAreaNorthWest();
        assertNotNull(decodedNorthWest);
        assertEquals(55.8341, decodedNorthWest.getLatitudeDouble(), 1e-12);
        assertEquals(9.9462, decodedNorthWest.getLongitudeDouble(), 1e-12);

        AisPosition decodedSouthEast = decodedTacticalVoyagePlanInquiry.getControlledAreaSouthEast();
        assertNotNull(decodedSouthEast);
        assertEquals(55.8111, decodedSouthEast.getLatitudeDouble(), 1e-12);
        assertEquals(9.9932, decodedSouthEast.getLongitudeDouble(), 1e-12);
    }

    private AisMessage6 createSyntheticAisMessage6() {
        AisMessage6 msg6 = new AisMessage6();
        msg6.setRepeat(0);
        msg6.setUserId(219000001);
        msg6.setSeqNum(0);
        msg6.setDestination(219019416);
        msg6.setRetransmit(0);
        msg6.setSpare(0);
        msg6.setDac(TacticalVoyagePlanInquiry.DAC);
        msg6.setFi(TacticalVoyagePlanInquiry.FI);
        return msg6;
    }

    private void validateEncoding(String encodedBinArray) {
        assertEquals("000110", encodedBinArray.substring(0, 6)); // Msg ID 6
        assertEquals("00", encodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", encodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", encodedBinArray.substring(38, 40)); // // Sequence no.
        assertEquals("001101000011011111100010011000", encodedBinArray.substring(40, 70)); // Destination ID: 219019416
        assertEquals("0", encodedBinArray.substring(70, 71)); // Retransmit flag
        assertEquals("0", encodedBinArray.substring(71, 72)); // Spare
        assertEquals("0011011011", encodedBinArray.substring(72, 82)); // DAC=219
        assertEquals("000101", encodedBinArray.substring(82, 88)); // FI=5
    }

}
