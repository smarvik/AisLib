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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
        asm.setStartMonth(0);
        asm.setStartDay(0);
        asm.setStartHour(0);
        asm.setStartMin(0);
        asm.setDuration(0);
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
        assertEquals("0000", encodedBinArray.substring(56, 60)); // UTC month
        assertEquals("00000", encodedBinArray.substring(60, 65)); // UTC day
        assertEquals("00000", encodedBinArray.substring(65, 70)); // UTC hour
        assertEquals("000000", encodedBinArray.substring(70, 76)); // UTC minute
        assertEquals("000000000000000000", encodedBinArray.substring(76, 94)); // Duration
        assertEquals("00000", encodedBinArray.substring(94, 99)); // No. of waypoints

        // Test NMEA encoding
        String[] sentences = Vdm.createSentences(syntheticAisMessage8, 0);
        assertEquals(1, sentences.length);
        assertEquals("!AIVDM,1,1,0,,83@ndh@nh@0000000,3*4B", sentences[0]);
    }

    @Test
    public void testDecodeCancelRoute() throws SixbitException, SentenceException, AisMessageException {
        // Build synthetic AIS message
        Vdm vdm = new Vdm();
        vdm.parse("!AIVDM,1,1,0,,83@ndh@nh@0000000,3*4B");

        // Test decoded bin array
        String decodedBinArray = vdm.getBinArray().toString();
        assertEquals(99, decodedBinArray.length());  // As per http://www.e-navigation.nl/content/intended-route
        assertEquals("001000", decodedBinArray.substring(0, 6)); // Msg ID 8
        assertEquals("00", decodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", decodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", decodedBinArray.substring(38, 40)); // Spare
        assertEquals("0011011011", decodedBinArray.substring(40, 50)); // DAC=219
        assertEquals("000001", decodedBinArray.substring(50, 56)); // FI=1
        assertEquals("0000", decodedBinArray.substring(56, 60)); // UTC month
        assertEquals("00000", decodedBinArray.substring(60, 65)); // UTC day
        assertEquals("00000", decodedBinArray.substring(65, 70)); // UTC hour
        assertEquals("000000", decodedBinArray.substring(70, 76)); // UTC minute
        assertEquals("000000000000000000", decodedBinArray.substring(76, 94)); // Duration
        assertEquals("00000", decodedBinArray.substring(94, 99)); // No. of waypoints

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
        assertEquals(0, broadcastIntendedRoute.getDuration());
        assertEquals(0, broadcastIntendedRoute.getWaypointCount());
    }

    @Test
    public void testEncodeWith4Waypoints() throws SixbitException, SentenceException {
        // Build synthetic message
        BroadcastIntendedRoute asm = new BroadcastIntendedRoute();
        asm.setStartMonth(1);
        asm.setStartDay(16);
        asm.setStartHour(12);
        asm.setStartMin(29);
        asm.setDuration(30);
        asm.setWaypointCount(4);
        List<AisPosition> waypoints = new LinkedList<>();
        Collections.addAll(waypoints,
            new AisPosition(Position.create(55.846578, 10.025599)),
            new AisPosition(Position.create(55.828263, 10.049975)),
            new AisPosition(Position.create(55.811868, 10.071840)),
            new AisPosition(Position.create(55.796335, 10.125227))
        );
        asm.setWaypoints(waypoints);
        syntheticAisMessage8.setAppMessage(asm);

        // Test binary encoding
        String encodedBinArray = syntheticAisMessage8.getEncoded().getBinArray().toString();

        System.out.println(encodedBinArray);

        assertEquals(99 + 4*55, encodedBinArray.length());  // As per http://www.e-navigation.nl/content/intended-route
        assertEquals("001000", encodedBinArray.substring(0, 6)); // Msg ID 8
        assertEquals("00", encodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", encodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", encodedBinArray.substring(38, 40)); // Spare
        assertEquals("0011011011", encodedBinArray.substring(40, 50)); // DAC=219
        assertEquals("000001", encodedBinArray.substring(50, 56)); // FI=1
        assertEquals("0001", encodedBinArray.substring(56, 60)); // UTC month
        assertEquals("10000", encodedBinArray.substring(60, 65)); // UTC day
        assertEquals("01100", encodedBinArray.substring(65, 70)); // UTC hour
        assertEquals("011101", encodedBinArray.substring(70, 76)); // UTC minute
        assertEquals("000000000000011110", encodedBinArray.substring(76, 94)); // Duration
        assertEquals("00100", encodedBinArray.substring(94, 99)); // No. of waypoints
        assertEquals("0000010110111100100101111111", encodedBinArray.substring(99, 127)); // Longitude of WP0
        assertEquals("001111111110100101001101011", encodedBinArray.substring(127, 154)); // Latitude of WP0
        assertEquals("0000010111000000001010100001", encodedBinArray.substring(154, 182)); // Longitude of WP1
        assertEquals("001111111110001111101111110", encodedBinArray.substring(182, 209)); // Latitude of WP1
        assertEquals("0000010111000011010111100000", encodedBinArray.substring(209, 237)); // Longitude of WP2
        assertEquals("001111111101111100100010001", encodedBinArray.substring(237, 264)); // Latitude of WP2
        assertEquals("0000010111001011001100000000", encodedBinArray.substring(264, 292)); // Longitude of WP3
        assertEquals("001111111101101010010101001", encodedBinArray.substring(292, 319)); // Latitude of WP3

        // Test NMEA encoding
        String[] sentences = Vdm.createSentences(syntheticAisMessage8, 0);
        assertEquals(1, sentences.length);
        assertEquals("!AIVDM,1,1,0,,83@ndh@nhAPil01pP;NBwWwBVd5h2`CwSst2pJt1wgTA1Ldh0wnaDP,5*12", sentences[0]);
    }

    @Test
    public void testDecodeWith4Waypoints() throws SixbitException, SentenceException, AisMessageException {
        // Build synthetic AIS message
        Vdm vdm = new Vdm();
        vdm.parse("!AIVDM,1,1,0,,83@ndh@nhAPil01pP;NBwWwBVd5h2`CwSst2pJt1wgTA1Ldh0wnaDP,5*12");

        // Test decoded bin array
        String decodedBinArray = vdm.getBinArray().toString();
        assertEquals(99 + 4*55, decodedBinArray.length());  // As per http://www.e-navigation.nl/content/intended-route
        assertEquals("001000", decodedBinArray.substring(0, 6)); // Msg ID 8
        assertEquals("00", decodedBinArray.substring(6, 8)); // Repeat indicator
        assertEquals("001101000011011010110011000001", decodedBinArray.substring(8, 38)); // Source ID: 219000001
        assertEquals("00", decodedBinArray.substring(38, 40)); // Spare
        assertEquals("0011011011", decodedBinArray.substring(40, 50)); // DAC=219
        assertEquals("000001", decodedBinArray.substring(50, 56)); // FI=1
        assertEquals("0001", decodedBinArray.substring(56, 60)); // UTC month
        assertEquals("10000", decodedBinArray.substring(60, 65)); // UTC day
        assertEquals("01100", decodedBinArray.substring(65, 70)); // UTC hour
        assertEquals("011101", decodedBinArray.substring(70, 76)); // UTC minute
        assertEquals("000000000000011110", decodedBinArray.substring(76, 94)); // Duration
        assertEquals("00100", decodedBinArray.substring(94, 99)); // No. of waypoints
        assertEquals("0000010110111100100101111111", decodedBinArray.substring(99, 127)); // Longitude of WP0
        assertEquals("001111111110100101001101011", decodedBinArray.substring(127, 154)); // Latitude of WP0
        assertEquals("0000010111000000001010100001", decodedBinArray.substring(154, 182)); // Longitude of WP1
        assertEquals("001111111110001111101111110", decodedBinArray.substring(182, 209)); // Latitude of WP1
        assertEquals("0000010111000011010111100000", decodedBinArray.substring(209, 237)); // Longitude of WP2
        assertEquals("001111111101111100100010001", decodedBinArray.substring(237, 264)); // Latitude of WP2
        assertEquals("0000010111001011001100000000", decodedBinArray.substring(264, 292)); // Longitude of WP3
        assertEquals("001111111101101010010101001", decodedBinArray.substring(292, 319)); // Latitude of WP3

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
        assertEquals(1, broadcastIntendedRoute.getStartMonth());
        assertEquals(16, broadcastIntendedRoute.getStartDay());
        assertEquals(12, broadcastIntendedRoute.getStartHour());
        assertEquals(29, broadcastIntendedRoute.getStartMin());
        assertEquals(30, broadcastIntendedRoute.getDuration());
        assertEquals(4, broadcastIntendedRoute.getWaypointCount());
        List<AisPosition> waypoints = broadcastIntendedRoute.getWaypoints();
        assertEquals(4, waypoints.size());
        assertEquals(new AisPosition(Position.create(55.846578, 10.025599)), waypoints.get(0));
        assertEquals(new AisPosition(Position.create(55.828263, 10.049975)), waypoints.get(1));
        assertEquals(new AisPosition(Position.create(55.811868, 10.071840)), waypoints.get(2));
        assertEquals(new AisPosition(Position.create(55.796335, 10.125227)), waypoints.get(3));
    }
}
