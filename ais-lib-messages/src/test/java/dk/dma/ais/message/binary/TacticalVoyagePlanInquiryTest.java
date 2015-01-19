/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dk.dma.ais.message.binary;

import dk.dma.ais.binary.SixbitException;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage6;
import dk.dma.ais.message.AisMessageException;
import dk.dma.ais.sentence.SentenceException;
import dk.dma.ais.sentence.Vdm;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TacticalVoyagePlanInquiryTest {

    AisMessage6 syntheticAisMessage6;

    @Before
    public void setup() {
        // Build synthetic AIS message
        syntheticAisMessage6 = createSyntheticAisMessage6();
    }

    @Test
    public void testEncode() throws SixbitException, SentenceException {
        // Build synthetic AIS message
        TacticalVoyagePlanInquiry asm = new TacticalVoyagePlanInquiry();
        asm.setDuration(240);
        syntheticAisMessage6.setAppMessage(asm);

        // Test binary encoding
        String encodedBinArray = syntheticAisMessage6.getEncoded().getBinArray().toString();
        assertEquals(96, encodedBinArray.length());
        assertEquals("000110", encodedBinArray.substring(0, 6)); // Msg ID 6
        assertEquals("00", encodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", encodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", encodedBinArray.substring(38, 40)); // // Sequence no.
        assertEquals("001101000011011111100010011000", encodedBinArray.substring(40, 70)); // Destination ID: 219019416
        assertEquals("0", encodedBinArray.substring(70, 71)); // Retransmit flag
        assertEquals("0", encodedBinArray.substring(71, 72)); // Spare
        assertEquals("0011011011", encodedBinArray.substring(72, 82)); // DAC=219
        assertEquals("000101", encodedBinArray.substring(82, 88)); // FI=5
        assertEquals("11110000", encodedBinArray.substring(88, 96)); // Duration = 240

        // Test NMEA encoding
        String[] sentences = Vdm.createSentences(syntheticAisMessage6, 0);
        assertEquals(1, sentences.length);
        assertEquals("!AIVDM,1,1,0,,63@ndh@l=v9P=dGh,0*08", sentences[0]);
    }

    @Test
    public void testDecode() throws SixbitException, SentenceException, AisMessageException {
        // Build synthetic AIS message
        Vdm vdm = new Vdm();
        vdm.parse("!AIVDM,1,1,0,,63@ndh@l=v9P=dGh,0*08");

        // Test decoded bin array
        String decodedBinArray = vdm.getBinArray().toString();
        assertEquals(96, decodedBinArray.length());
        assertEquals("000110", decodedBinArray.substring(0, 6)); // Msg ID 6
        assertEquals("00", decodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", decodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", decodedBinArray.substring(38, 40)); // // Sequence no.
        assertEquals("001101000011011111100010011000", decodedBinArray.substring(40, 70)); // Destination ID: 219019416
        assertEquals("0", decodedBinArray.substring(70, 71)); // Retransmit flag
        assertEquals("0", decodedBinArray.substring(71, 72)); // Spare
        assertEquals("0011011011", decodedBinArray.substring(72, 82)); // DAC=219
        assertEquals("000101", decodedBinArray.substring(82, 88)); // FI=5
        assertEquals("11110000", decodedBinArray.substring(88, 96)); // Duration = 240

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
        assertEquals(240, decodedTacticalVoyagePlanInquiry.getDuration());
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

}
