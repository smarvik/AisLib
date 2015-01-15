package dk.dma.ais.message.binary;

import dk.dma.ais.binary.SixbitException;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage6;
import dk.dma.ais.message.AisMessageException;
import dk.dma.ais.message.AisPosition;
import dk.dma.ais.sentence.SentenceException;
import dk.dma.ais.sentence.Vdm;
import dk.dma.enav.model.geometry.Position;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TacticalVoyagePlanInquiryTest {

    AisMessage6 syntheticAisMessage6;

    @Before
    public void setup() {
        // Build synthetic AIS message
        syntheticAisMessage6 = createSyntheticAisMessage6();
    }

    @Test
    public void testEncodeWithNoArea() throws SixbitException, SentenceException {
        // Test binary encoding
        String encodedBinArray = syntheticAisMessage6.getEncoded().getBinArray().toString();
        assertEquals(88, encodedBinArray.length());
        validateBinaryEncoding(encodedBinArray);

        // Test NMEA encoding
        String[] sentences = Vdm.createSentences(syntheticAisMessage6, 0);
        assertEquals(1, sentences.length);
        assertEquals("!AIVDM,1,1,0,,63@ndh@l=v9P=dD,2*61", sentences[0]);
    }

    @Test
    public void testDecodeWithNoArea() throws SixbitException, SentenceException, AisMessageException {
        // Build synthetic AIS message
        Vdm vdm = new Vdm();
        vdm.parse("!AIVDM,1,1,0,,63@ndh@l=v9P=dD,2*61");

        // Test decoded bin array
        String decodedBinArray = vdm.getBinArray().toString();
        validateBinaryEncoding(decodedBinArray);

        // Test decoded values
        AisMessage aisMessage = AisMessage.getInstance(vdm);
        assertTrue(aisMessage instanceof AisMessage6);
        AisMessage6 msg6 = (AisMessage6) aisMessage;
        assertEquals(syntheticAisMessage6.getRepeat(), msg6.getRepeat());
        assertEquals(syntheticAisMessage6.getUserId(), msg6.getUserId());
        assertEquals(syntheticAisMessage6.getSeqNum(), msg6.getSeqNum());
        assertEquals(syntheticAisMessage6.getDestination(), msg6.getDestination());
        assertEquals(syntheticAisMessage6.getRetransmit(), msg6.getRetransmit());
        assertEquals(syntheticAisMessage6.getSpare(), msg6.getSpare());
        assertEquals(syntheticAisMessage6.getDac(), msg6.getDac());
        assertEquals(syntheticAisMessage6.getFi(), msg6.getFi());

        AisApplicationMessage decodedAsm = msg6.getApplicationMessage();
        assertNotNull(decodedAsm);
        assertTrue(decodedAsm instanceof TacticalVoyagePlanInquiry);
        TacticalVoyagePlanInquiry decodedTacticalVoyagePlanInquiry = (TacticalVoyagePlanInquiry) decodedAsm;
        assertNull(decodedTacticalVoyagePlanInquiry.getControlledAreaNorthWest());
        assertNull(decodedTacticalVoyagePlanInquiry.getControlledAreaSouthEast());
    }

    @Test
    public void testEncodeWithArea() throws Exception {
        // Build synthetic AIS message
        TacticalVoyagePlanInquiry asm = new TacticalVoyagePlanInquiry();
        asm.setControlledAreaNorthWest(new AisPosition(Position.create(55.8341, 9.9462)));
        asm.setControlledAreaSouthEast(new AisPosition(Position.create(55.8111, 9.9932)));
        syntheticAisMessage6.setAppMessage(asm);

        // Test binary encoding
        String binArray = syntheticAisMessage6.getEncoded().getBinArray().toString();
        assertEquals(198, binArray.length());
        validateBinaryEncoding(binArray);
        assertEquals("0001111111110010110100101100", binArray.substring(88, 116));  // North
        assertEquals("000010110110111110110010000", binArray.substring(116, 143));  // East
        assertEquals("0001111111101111011101000100", binArray.substring(143, 171)); // South
        assertEquals("000010110110000111101101000", binArray.substring(171, 198));  // West

        // Test NMEA encoding
        String[] sentences = Vdm.createSentences(syntheticAisMessage6, 0);
        assertEquals(1, sentences.length);
        assertEquals("!AIVDM,1,1,0,,63@ndh@l=v9P=dDOte;0egdP?us`PFhu`,0*32", sentences[0]);
    }

    @Test
    public void testDecodeWithArea() throws Exception {
        // Build synthetic AIS message
        Vdm vdm = new Vdm();
        vdm.parse("!AIVDM,1,1,0,,63@ndh@l=v9P=dDOte;0egdP?us`PFhu`,0*32");

        // Test decoded values
        AisMessage aisMessage = AisMessage.getInstance(vdm);
        assertTrue(aisMessage instanceof AisMessage6);
        AisMessage6 msg6 = (AisMessage6) aisMessage;
        assertEquals(syntheticAisMessage6.getRepeat(), msg6.getRepeat());
        assertEquals(syntheticAisMessage6.getUserId(), msg6.getUserId());
        assertEquals(syntheticAisMessage6.getSeqNum(), msg6.getSeqNum());
        assertEquals(syntheticAisMessage6.getDestination(), msg6.getDestination());
        assertEquals(syntheticAisMessage6.getRetransmit(), msg6.getRetransmit());
        assertEquals(syntheticAisMessage6.getSpare(), msg6.getSpare());
        assertEquals(syntheticAisMessage6.getDac(), msg6.getDac());
        assertEquals(syntheticAisMessage6.getFi(), msg6.getFi());

        AisApplicationMessage decodedAsm = msg6.getApplicationMessage();
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
        AisMessage6 syntheticAisMessage6 = new AisMessage6();
        syntheticAisMessage6.setRepeat(0);
        syntheticAisMessage6.setUserId(219000001);
        syntheticAisMessage6.setSeqNum(0);
        syntheticAisMessage6.setDestination(219019416);
        syntheticAisMessage6.setRetransmit(0);
        syntheticAisMessage6.setSpare(0);
        syntheticAisMessage6.setDac(TacticalVoyagePlanInquiry.DAC);
        syntheticAisMessage6.setFi(TacticalVoyagePlanInquiry.FI);
        return syntheticAisMessage6;
    }

    private void validateBinaryEncoding(String binArray) {
        assertEquals("000110", binArray.substring(0, 6)); // Msg ID 6
        assertEquals("00", binArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", binArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", binArray.substring(38, 40)); // // Sequence no.
        assertEquals("001101000011011111100010011000", binArray.substring(40, 70)); // Destination ID: 219019416
        assertEquals("0", binArray.substring(70, 71)); // Retransmit flag
        assertEquals("0", binArray.substring(71, 72)); // Spare
        assertEquals("0011011011", binArray.substring(72, 82)); // DAC=219
        assertEquals("000101", binArray.substring(82, 88)); // FI=5
    }

}
