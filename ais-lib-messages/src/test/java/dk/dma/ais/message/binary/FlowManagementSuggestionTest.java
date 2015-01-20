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
import dk.dma.ais.message.AisPosition;
import dk.dma.ais.sentence.SentenceException;
import dk.dma.ais.sentence.Vdm;
import dk.dma.enav.model.geometry.Position;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FlowManagementSuggestionTest {

    AisMessage6 syntheticAisMessage6;

    @Before
    public void setup() {
        // Build synthetic AIS message
        syntheticAisMessage6 = syntheticAisMessage6 = new AisMessage6();
        syntheticAisMessage6.setRepeat(0);
        syntheticAisMessage6.setUserId(219000001);
        syntheticAisMessage6.setSeqNum(0);
        syntheticAisMessage6.setDestination(219019416);
        syntheticAisMessage6.setRetransmit(0);
        syntheticAisMessage6.setSpare(0);
        syntheticAisMessage6.setDac(FlowManagementSuggestion.DAC);
        syntheticAisMessage6.setFi(FlowManagementSuggestion.FI);
    }

    @Test
    public void testEncodeFlowManagementSuggestionWithActiveWaypointOnly() throws SixbitException, SentenceException {
        // Build synthetic message
        FlowManagementSuggestion asm = new FlowManagementSuggestion();
        asm.setSuggestedActiveWaypoint(new AisPosition(Position.create(55.856310, 9.866598)));
        asm.setSuggestedActiveWaypointEstimatedTimeOfArrivalUTCHour(23);
        asm.setSuggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute(59);
        syntheticAisMessage6.setAppMessage(asm);

        // Test binary encoding
        String encodedBinArray = syntheticAisMessage6.getEncoded().getBinArray().toString();

        assertEquals(154, encodedBinArray.length());
        assertEquals("000110", encodedBinArray.substring(0, 6)); // Msg ID 6
        assertEquals("00", encodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", encodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", encodedBinArray.substring(38, 40)); // // Sequence no.
        assertEquals("001101000011011111100010011000", encodedBinArray.substring(40, 70)); // Destination ID: 219019416
        assertEquals("0", encodedBinArray.substring(70, 71)); // Retransmit flag
        assertEquals("0", encodedBinArray.substring(71, 72)); // Spare
        assertEquals("0011011011", encodedBinArray.substring(72, 82)); // DAC=219
        assertEquals("000110", encodedBinArray.substring(82, 88)); // FI=6
        assertEquals("0000010110100101010011010111", encodedBinArray.substring(88, 116)); // Active WP, lon
        assertEquals("001111111110110000100111010", encodedBinArray.substring(116, 143)); // Active WP, lat
        assertEquals("10111", encodedBinArray.substring(143, 148)); // Active WP, eta hour
        assertEquals("111011", encodedBinArray.substring(148, 154)); // Active WP, eta minute

        // Test NMEA encoding
        String[] sentences = Vdm.createSentences(syntheticAisMessage6, 0);
        assertEquals(1, sentences.length);
        assertEquals("!AIVDM,1,1,0,,63@ndh@l=v9P=dH5aDmkwd9mOd,2*17", sentences[0]);
    }

    @Test
    public void testDecodeFlowManagementSuggestionWithActiveWaypointOnly() throws SixbitException, SentenceException, AisMessageException {
        // Build synthetic AIS message
        Vdm vdm = new Vdm();
        vdm.parse("!AIVDM,1,1,0,,63@ndh@l=v9P=dH5aDmkwd9mOd,2*17");

        // Test decoded bin array
        String decodedBinArray = vdm.getBinArray().toString();
        assertEquals(154, decodedBinArray.length());  // As per http://www.e-navigation.nl/content/intended-route
        assertEquals("000110", decodedBinArray.substring(0, 6)); // Msg ID 6
        assertEquals("00", decodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", decodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", decodedBinArray.substring(38, 40)); // // Sequence no.
        assertEquals("001101000011011111100010011000", decodedBinArray.substring(40, 70)); // Destination ID: 219019416
        assertEquals("0", decodedBinArray.substring(70, 71)); // Retransmit flag
        assertEquals("0", decodedBinArray.substring(71, 72)); // Spare
        assertEquals("0011011011", decodedBinArray.substring(72, 82)); // DAC=219
        assertEquals("000110", decodedBinArray.substring(82, 88)); // FI=6
        assertEquals("0000010110100101010011010111", decodedBinArray.substring(88, 116)); // Active WP, lon
        assertEquals("001111111110110000100111010", decodedBinArray.substring(116, 143)); // Active WP, lat
        assertEquals("10111", decodedBinArray.substring(143, 148)); // Active WP, eta hour
        assertEquals("111011", decodedBinArray.substring(148, 154)); // Active WP, eta minute

        // Test decoded values
        AisMessage aisMessage = AisMessage.getInstance(vdm);
        assertTrue(aisMessage instanceof AisMessage6);
        AisMessage6 msg6 = (AisMessage6) aisMessage;
        assertEquals(syntheticAisMessage6.getRepeat(), msg6.getRepeat());
        assertEquals(syntheticAisMessage6.getUserId(), msg6.getUserId());
        assertEquals(syntheticAisMessage6.getSpare(), msg6.getSpare());
        assertEquals(syntheticAisMessage6.getDac(), msg6.getDac());
        assertEquals(syntheticAisMessage6.getFi(), msg6.getFi());

        AisApplicationMessage decodedAsm = msg6.getApplicationMessage();
        assertNotNull(decodedAsm);
        assertTrue(decodedAsm instanceof FlowManagementSuggestion);
        FlowManagementSuggestion flowManagementSuggestion = (FlowManagementSuggestion) decodedAsm;

        assertEquals(55.856310, flowManagementSuggestion.getSuggestedActiveWaypoint().getLatitudeDouble(), 1e-5);
        assertEquals(9.866598, flowManagementSuggestion.getSuggestedActiveWaypoint().getLongitudeDouble(), 1e-5);
        assertEquals(23, flowManagementSuggestion.getSuggestedActiveWaypointEstimatedTimeOfArrivalUTCHour());
        assertEquals(59, flowManagementSuggestion.getSuggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute());

        assertEquals(null, flowManagementSuggestion.getSuggestedFollowingWaypoints());
    }


    @Test
    public void testEncodeFlowManagementSuggestionWith12ActiveWaypoints() throws SixbitException, SentenceException {
        // Build synthetic message
        FlowManagementSuggestion asm = new FlowManagementSuggestion();
        asm.setSuggestedActiveWaypoint(new AisPosition(Position.create(55.856310, 9.866598)));
        asm.setSuggestedActiveWaypointEstimatedTimeOfArrivalUTCHour(23);
        asm.setSuggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute(59);
        asm.addSuggestedWaypoint(new FlowManagementSuggestion.ExtendedWaypoint(new AisPosition(Position.create(55.854913, 9.887884)), 24));
        asm.addSuggestedWaypoint(new FlowManagementSuggestion.ExtendedWaypoint(new AisPosition(Position.create(55.844410, 9.980881)), 1));
        asm.addSuggestedWaypoint(new FlowManagementSuggestion.ExtendedWaypoint(new AisPosition(Position.create(55.846337, 10.012982)), 7));
        asm.addSuggestedWaypoint(new FlowManagementSuggestion.ExtendedWaypoint(new AisPosition(Position.create(55.846337, 10.028260)), 11));
        asm.addSuggestedWaypoint(new FlowManagementSuggestion.ExtendedWaypoint(new AisPosition(Position.create(55.833710, 10.035126)), 15));
        asm.addSuggestedWaypoint(new FlowManagementSuggestion.ExtendedWaypoint(new AisPosition(Position.create(55.826865, 10.054009)), 16));
        asm.addSuggestedWaypoint(new FlowManagementSuggestion.ExtendedWaypoint(new AisPosition(Position.create(55.816836, 10.060876)), 6));
        asm.addSuggestedWaypoint(new FlowManagementSuggestion.ExtendedWaypoint(new AisPosition(Position.create(55.809216, 10.076668)), 4));
        asm.addSuggestedWaypoint(new FlowManagementSuggestion.ExtendedWaypoint(new AisPosition(Position.create(55.796384, 10.125077)), 17));
        asm.addSuggestedWaypoint(new FlowManagementSuggestion.ExtendedWaypoint(new AisPosition(Position.create(55.781906, 10.262749)), 255));
        asm.addSuggestedWaypoint(new FlowManagementSuggestion.ExtendedWaypoint(new AisPosition(Position.create(55.776307, 10.269788)), 16));
        asm.addSuggestedWaypoint(new FlowManagementSuggestion.ExtendedWaypoint(new AisPosition(Position.create(55.762402, 10.272706)), 1));
        syntheticAisMessage6.setAppMessage(asm);

        // Test binary encoding
        String encodedBinArray = syntheticAisMessage6.getEncoded().getBinArray().toString();

        assertEquals(910, encodedBinArray.length());
        assertEquals("000110", encodedBinArray.substring(0, 6)); // Msg ID 6
        assertEquals("00", encodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", encodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", encodedBinArray.substring(38, 40)); // // Sequence no.
        assertEquals("001101000011011111100010011000", encodedBinArray.substring(40, 70)); // Destination ID: 219019416
        assertEquals("0", encodedBinArray.substring(70, 71)); // Retransmit flag
        assertEquals("0", encodedBinArray.substring(71, 72)); // Spare
        assertEquals("0011011011", encodedBinArray.substring(72, 82)); // DAC=219
        assertEquals("000110", encodedBinArray.substring(82, 88)); // FI=6
        assertEquals("0000010110100101010011010111", encodedBinArray.substring(88, 116)); // Active WP, lon
        assertEquals("001111111110110000100111010", encodedBinArray.substring(116, 143)); // Active WP, lat
        assertEquals("10111", encodedBinArray.substring(143, 148)); // Active WP, eta hour
        assertEquals("111011", encodedBinArray.substring(148, 154)); // Active WP, eta minute

        // Test NMEA encoding
        String[] sentences = Vdm.createSentences(syntheticAisMessage6, 0);
        assertEquals(3, sentences.length);
        assertEquals("!AIVDM,3,1,0,,63@ndh@l=v9P=dH5aDmkwd9mOd5b6fSwcg`<0ed5ROu5EP45fcsCwa>l3Pequ,0*52", sentences[0]);
        assertEquals("!AIVDM,3,2,0,,pOu9nPd5gOm3wUR47Pf1PbOtL=i05hL;SwPUd30f89ROsjnP@5jjaSwJV<8Pf,0*46", sentences[1]);
        assertEquals("!AIVDM,3,3,0,,vbROrjn?t5p5lCwDeh80g1U@Or58@4,2*0F", sentences[2]);
    }
}
