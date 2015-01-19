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
import dk.dma.ais.message.AisMessage8;
import dk.dma.ais.message.AisMessageException;
import dk.dma.ais.message.AisPosition;
import dk.dma.ais.sentence.SentenceException;
import dk.dma.ais.sentence.Vdm;
import dk.dma.enav.model.geometry.Position;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TacticalVoyagePlanTest {

    AisMessage8 syntheticAisMessage8;

    @Before
    public void setup() {
        // Build synthetic AIS message
        syntheticAisMessage8 = new AisMessage8();
        syntheticAisMessage8.setRepeat(0);
        syntheticAisMessage8.setUserId(219000001);
        syntheticAisMessage8.setSpare(0);
        syntheticAisMessage8.setDac(TacticalVoyagePlan.DAC);
        syntheticAisMessage8.setFi(TacticalVoyagePlan.FI);
    }

    @Test
    public void testEncodeCancelTacticalVoyagePlan() throws SixbitException, SentenceException {
        // Build synthetic message
        TacticalVoyagePlan asm = new TacticalVoyagePlan();
        syntheticAisMessage8.setAppMessage(asm);

        // Test binary encoding
        String encodedBinArray = syntheticAisMessage8.getEncoded().getBinArray().toString();

        assertEquals(56, encodedBinArray.length());
        assertEquals("001000", encodedBinArray.substring(0, 6)); // Msg ID 8
        assertEquals("00", encodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", encodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", encodedBinArray.substring(38, 40)); // Spare
        assertEquals("0011011011", encodedBinArray.substring(40, 50)); // DAC=219
        assertEquals("000100", encodedBinArray.substring(50, 56)); // FI=4

        // Test NMEA encoding
        String[] sentences = Vdm.createSentences(syntheticAisMessage8, 0);
        assertEquals(1, sentences.length);
        assertEquals("!AIVDM,1,1,0,,83@ndh@ni0,4*0D", sentences[0]);
    }

    @Test
    public void testDecodeCancelTacticalVoyagePlan() throws SixbitException, SentenceException, AisMessageException {
        // Build synthetic AIS message
        Vdm vdm = new Vdm();
        vdm.parse("!AIVDM,1,1,0,,83@ndh@ni0,4*0D");

        // Test decoded bin array
        String decodedBinArray = vdm.getBinArray().toString();
        assertEquals(56, decodedBinArray.length());  // As per http://www.e-navigation.nl/content/intended-route
        assertEquals("001000", decodedBinArray.substring(0, 6)); // Msg ID 8
        assertEquals("00", decodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", decodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", decodedBinArray.substring(38, 40)); // Spare
        assertEquals("0011011011", decodedBinArray.substring(40, 50)); // DAC=219
        assertEquals("000100", decodedBinArray.substring(50, 56)); // FI=4

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
        assertTrue(decodedAsm instanceof TacticalVoyagePlan);
        TacticalVoyagePlan tacticalVoyagePlan = (TacticalVoyagePlan) decodedAsm;
        assertEquals(null, tacticalVoyagePlan.getFollowingWaypoints());
    }

    @Test
    public void testEncodeTacticalVoyagePlanWithActiveWaypointOnly() throws SixbitException, SentenceException {
        // Build synthetic message
        TacticalVoyagePlan asm = new TacticalVoyagePlan();
        asm.setActiveWaypoint(new AisPosition(Position.create(55.856310, 9.866598)));
        asm.setActiveWaypointEstimatedTimeOfArrivalUTCHour(23);
        asm.setActiveWaypointEstimatedTimeOfArrivalUTCMinute(59);
        asm.setActiveWaypointTurnCircleRadius(255);
        syntheticAisMessage8.setAppMessage(asm);

        // Test binary encoding
        String encodedBinArray = syntheticAisMessage8.getEncoded().getBinArray().toString();

        assertEquals(130, encodedBinArray.length());
        assertEquals("001000", encodedBinArray.substring(0, 6)); // Msg ID 8
        assertEquals("00", encodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", encodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", encodedBinArray.substring(38, 40)); // Spare
        assertEquals("0011011011", encodedBinArray.substring(40, 50)); // DAC=219
        assertEquals("000100", encodedBinArray.substring(50, 56)); // FI=4
        assertEquals("0000010110100101010011010111", encodedBinArray.substring(56, 84)); // lon = 9.866598
        assertEquals("001111111110110000100111010", encodedBinArray.substring(84, 111)); // lat = 55.856310
        assertEquals("10111", encodedBinArray.substring(111, 116)); // hour = 23
        assertEquals("111011", encodedBinArray.substring(116, 122)); // minute = 59
        assertEquals("11111111", encodedBinArray.substring(122, 130)); // tcr = 255

        // Test NMEA encoding
        String[] sentences = Vdm.createSentences(syntheticAisMessage8, 0);
        assertEquals(1, sentences.length);
        assertEquals("!AIVDM,1,1,0,,83@ndh@ni0FUCG?vhWEvwt,2*5A", sentences[0]);
    }

    @Test
    public void testDecodeTacticalVoyagePlanWithActiveWaypointOnly() throws SixbitException, SentenceException, AisMessageException {
        // Build synthetic AIS message
        Vdm vdm = new Vdm();
        vdm.parse("!AIVDM,1,1,0,,83@ndh@ni0FUCG?vhWEvwt,2*5A");

        // Test decoded bin array
        String decodedBinArray = vdm.getBinArray().toString();
        assertEquals(130, decodedBinArray.length());  // As per http://www.e-navigation.nl/content/intended-route
        assertEquals("001000", decodedBinArray.substring(0, 6)); // Msg ID 8
        assertEquals("00", decodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", decodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", decodedBinArray.substring(38, 40)); // Spare
        assertEquals("0011011011", decodedBinArray.substring(40, 50)); // DAC=219
        assertEquals("000100", decodedBinArray.substring(50, 56)); // FI=4
        assertEquals("0000010110100101010011010111", decodedBinArray.substring(56, 84)); // lon = 9.866598
        assertEquals("001111111110110000100111010", decodedBinArray.substring(84, 111)); // lat = 55.856310
        assertEquals("10111", decodedBinArray.substring(111, 116)); // hour = 23
        assertEquals("111011", decodedBinArray.substring(116, 122)); // minute = 59
        assertEquals("11111111", decodedBinArray.substring(122, 130)); // tcr = 255

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
        assertTrue(decodedAsm instanceof TacticalVoyagePlan);
        TacticalVoyagePlan tacticalVoyagePlan = (TacticalVoyagePlan) decodedAsm;

        assertEquals(55.856310, tacticalVoyagePlan.getActiveWaypoint().getLatitudeDouble(), 1e-5);
        assertEquals(9.866598, tacticalVoyagePlan.getActiveWaypoint().getLongitudeDouble(), 1e-5);
        assertEquals(23, tacticalVoyagePlan.getActiveWaypointEstimatedTimeOfArrivalUTCHour());
        assertEquals(59, tacticalVoyagePlan.getActiveWaypointEstimatedTimeOfArrivalUTCMinute());
        assertEquals(255, tacticalVoyagePlan.getActiveWaypointTurnCircleRadius());

        assertEquals(null, tacticalVoyagePlan.getFollowingWaypoints());
    }

    @Test
    public void testEncodeTacticalVoyagePlanWithActiveWaypointAnd12FollowingWaypoints() throws SixbitException, SentenceException {
        // Build synthetic message
        TacticalVoyagePlan asm = new TacticalVoyagePlan();
        asm.setActiveWaypoint(new AisPosition(Position.create(55.856310, 9.866598)));
        asm.setActiveWaypointEstimatedTimeOfArrivalUTCHour(23);
        asm.setActiveWaypointEstimatedTimeOfArrivalUTCMinute(59);
        asm.setActiveWaypointTurnCircleRadius(255);
        asm.addFollowingWaypoint(new TacticalVoyagePlan.ExtendedWaypoint(new AisPosition(Position.create(55.854913, 9.887884)), 24, 127));
        asm.addFollowingWaypoint(new TacticalVoyagePlan.ExtendedWaypoint(new AisPosition(Position.create(55.844410, 9.980881)), 1, 127));
        asm.addFollowingWaypoint(new TacticalVoyagePlan.ExtendedWaypoint(new AisPosition(Position.create(55.846337, 10.012982)), 7, 127));
        asm.addFollowingWaypoint(new TacticalVoyagePlan.ExtendedWaypoint(new AisPosition(Position.create(55.846337, 10.028260)), 11, 127));
        asm.addFollowingWaypoint(new TacticalVoyagePlan.ExtendedWaypoint(new AisPosition(Position.create(55.833710, 10.035126)), 15, 127));
        asm.addFollowingWaypoint(new TacticalVoyagePlan.ExtendedWaypoint(new AisPosition(Position.create(55.826865, 10.054009)), 16, 127));
        asm.addFollowingWaypoint(new TacticalVoyagePlan.ExtendedWaypoint(new AisPosition(Position.create(55.816836, 10.060876)), 6, 127));
        asm.addFollowingWaypoint(new TacticalVoyagePlan.ExtendedWaypoint(new AisPosition(Position.create(55.809216, 10.076668)), 4, 127));
        asm.addFollowingWaypoint(new TacticalVoyagePlan.ExtendedWaypoint(new AisPosition(Position.create(55.796384, 10.125077)), 17, 127));
        asm.addFollowingWaypoint(new TacticalVoyagePlan.ExtendedWaypoint(new AisPosition(Position.create(55.781906, 10.262749)), 255, 127));
        asm.addFollowingWaypoint(new TacticalVoyagePlan.ExtendedWaypoint(new AisPosition(Position.create(55.776307, 10.269788)), 16, 127));
        asm.addFollowingWaypoint(new TacticalVoyagePlan.ExtendedWaypoint(new AisPosition(Position.create(55.762402, 10.272706)), 1, 127));
        syntheticAisMessage8.setAppMessage(asm);

        // Test binary encoding
        String encodedBinArray = syntheticAisMessage8.getEncoded().getBinArray().toString();

        assertEquals(982, encodedBinArray.length());
        assertEquals("001000", encodedBinArray.substring(0, 6)); // Msg ID 8
        assertEquals("00", encodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", encodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", encodedBinArray.substring(38, 40)); // Spare
        assertEquals("0011011011", encodedBinArray.substring(40, 50)); // DAC=219
        assertEquals("000100", encodedBinArray.substring(50, 56)); // FI=4
        assertEquals("0000010110100101010011010111", encodedBinArray.substring(56, 84)); // lon = 9.866598
        assertEquals("001111111110110000100111010", encodedBinArray.substring(84, 111)); // lat = 55.856310
        assertEquals("10111", encodedBinArray.substring(111, 116)); // hour = 23
        assertEquals("111011", encodedBinArray.substring(116, 122)); // minute = 59
        assertEquals("11111111", encodedBinArray.substring(122, 130)); // tcr = 255

        // Test NMEA encoding
        String[] sentences = Vdm.createSentences(syntheticAisMessage8, 0);
        assertEquals(3, sentences.length);
        assertEquals("!AIVDM,3,1,0,,83@ndh@ni0FUCG?vhWEvwt5b6fSwcg`<?p;K1HWwAEH1OhFrge?vTs@>wPequ,0*7D", sentences[0]);
        assertEquals("!AIVDM,3,2,0,,pOu9nPew1Kou@wqHQ1sv2p62awiho47t5hL;SwPUd3?p;R2HWvte`4OhG;:V?,0*27", sentences[1]);
        assertEquals("!AIVDM,3,3,0,,ubHhRwPfvbROrjn?uw1N1M4wm;L23v2t6E1w`DQ0Gt,2*1B", sentences[2]);
    }

    @Test
    public void testDecodeTacticalVoyagePlanWithActiveWaypointAnd12FollowingWaypoints() throws SixbitException, SentenceException, AisMessageException {
        // Build synthetic AIS message
        Vdm vdm = new Vdm();
        vdm.parse("!AIVDM,3,1,0,,83@ndh@ni0FUCG?vhWEvwt5b6fSwcg`<?p;K1HWwAEH1OhFrge?vTs@>wPequ,0*7D");
        vdm.parse("!AIVDM,3,2,0,,pOu9nPew1Kou@wqHQ1sv2p62awiho47t5hL;SwPUd3?p;R2HWvte`4OhG;:V?,0*27");
        vdm.parse("!AIVDM,3,3,0,,ubHhRwPfvbROrjn?uw1N1M4wm;L23v2t6E1w`DQ0Gt,2*1B");

        // Test decoded bin array
        String decodedBinArray = vdm.getBinArray().toString();
        assertEquals(982, decodedBinArray.length());  // As per http://www.e-navigation.nl/content/intended-route
        assertEquals("001000", decodedBinArray.substring(0, 6)); // Msg ID 8
        assertEquals("00", decodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", decodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", decodedBinArray.substring(38, 40)); // Spare
        assertEquals("0011011011", decodedBinArray.substring(40, 50)); // DAC=219
        assertEquals("000100", decodedBinArray.substring(50, 56)); // FI=4
        assertEquals("0000010110100101010011010111", decodedBinArray.substring(56, 84)); // lon = 9.866598
        assertEquals("001111111110110000100111010", decodedBinArray.substring(84, 111)); // lat = 55.856310
        assertEquals("10111", decodedBinArray.substring(111, 116)); // hour = 23
        assertEquals("111011", decodedBinArray.substring(116, 122)); // minute = 59
        assertEquals("11111111", decodedBinArray.substring(122, 130)); // tcr = 255

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
        assertTrue(decodedAsm instanceof TacticalVoyagePlan);
        TacticalVoyagePlan tacticalVoyagePlan = (TacticalVoyagePlan) decodedAsm;

        assertEquals(55.856310, tacticalVoyagePlan.getActiveWaypoint().getLatitudeDouble(), 1e-5);
        assertEquals(9.866598, tacticalVoyagePlan.getActiveWaypoint().getLongitudeDouble(), 1e-5);
        assertEquals(23, tacticalVoyagePlan.getActiveWaypointEstimatedTimeOfArrivalUTCHour());
        assertEquals(59, tacticalVoyagePlan.getActiveWaypointEstimatedTimeOfArrivalUTCMinute());
        assertEquals(255, tacticalVoyagePlan.getActiveWaypointTurnCircleRadius());

        List<TacticalVoyagePlan.ExtendedWaypoint> followingWaypoints = tacticalVoyagePlan.getFollowingWaypoints();

        assertNotNull(followingWaypoints);
        assertEquals(12, followingWaypoints.size());

        assertExtendedWaypoint(followingWaypoints.get(0), 55.854913f, 9.887884f, 24, 127);
        assertExtendedWaypoint(followingWaypoints.get(1), 55.844410f, 9.980881f, 1, 127);
        assertExtendedWaypoint(followingWaypoints.get(2), 55.846337f, 10.012982f, 7, 127);
        assertExtendedWaypoint(followingWaypoints.get(3), 55.846337f, 10.028260f, 11, 127);
        assertExtendedWaypoint(followingWaypoints.get(4), 55.833710f, 10.035126f, 15, 127);
        assertExtendedWaypoint(followingWaypoints.get(5), 55.826865f, 10.054009f, 16, 127);
        assertExtendedWaypoint(followingWaypoints.get(6), 55.816836f, 10.060876f, 6, 127);
        assertExtendedWaypoint(followingWaypoints.get(7), 55.809216f, 10.076668f, 4, 127);
        assertExtendedWaypoint(followingWaypoints.get(8), 55.796384f, 10.125077f, 17, 127);
        assertExtendedWaypoint(followingWaypoints.get(9), 55.781906f, 10.262749f, 255, 127);
        assertExtendedWaypoint(followingWaypoints.get(10), 55.776307f, 10.269788f, 16, 127);
        assertExtendedWaypoint(followingWaypoints.get(11), 55.762402f, 10.272706f, 1, 127);
    }

    private static void assertExtendedWaypoint(TacticalVoyagePlan.ExtendedWaypoint wp, float latitude, float longitude, int eta, int tcr) {
        assertEquals(latitude, wp.getWaypoint().getLatitudeDouble(), 1e-5);
        assertEquals(longitude, wp.getWaypoint().getLongitudeDouble(), 1e-5);
        assertEquals(eta, wp.getEstimatedTimeOfArrival());
        assertEquals(tcr, wp.getTurnCircleRadius());
    }
}
