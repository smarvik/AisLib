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

import dk.dma.ais.binary.BinArray;
import dk.dma.ais.binary.SixbitEncoder;
import dk.dma.ais.binary.SixbitException;
import dk.dma.ais.message.AisPosition;

import java.util.LinkedList;
import java.util.List;

/**
 * ASM for addressed flow management suggestion
 */
public class FlowManagementSuggestion extends AisApplicationMessage {

    public static final int DAC = 219;
    public static final int FI = 6;

    private AisPosition suggestedActiveWaypoint;
    private int suggestedActiveWaypointEstimatedTimeOfArrivalUTCHour;
    private int suggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute;
    private List<ExtendedWaypoint> suggestedFollowingWaypoints;

    public FlowManagementSuggestion() {
        super(DAC, FI);
    }

    public FlowManagementSuggestion(BinArray binArray) throws SixbitException {
        super(DAC, FI, binArray);
        if (binArray.hasMoreBits()) {
            suggestedActiveWaypoint = new AisPosition();
            suggestedActiveWaypoint.setRawLongitude(binArray.getVal(28));
            suggestedActiveWaypoint.setRawLatitude(binArray.getVal(27));
            suggestedActiveWaypointEstimatedTimeOfArrivalUTCHour = (int) binArray.getVal(5);
            suggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute = (int) binArray.getVal(6);
            while (binArray.hasMoreBits()) {
                ExtendedWaypoint suggestedWaypoint = new ExtendedWaypoint();
                AisPosition waypoint = new AisPosition();
                waypoint.setRawLongitude(binArray.getVal(28));
                waypoint.setRawLatitude(binArray.getVal(27));
                suggestedWaypoint.setWaypoint(waypoint);
                suggestedWaypoint.setEstimatedTimeOfArrival((int) binArray.getVal(8));
                addSuggestedWaypoint(suggestedWaypoint);
            }
        }
    }

    @Override
    public SixbitEncoder getEncoded() {
        SixbitEncoder encoder = new SixbitEncoder();
        if (suggestedActiveWaypoint != null) {
            encoder.addVal(suggestedActiveWaypoint.getRawLongitude(), 28);
            encoder.addVal(suggestedActiveWaypoint.getRawLatitude(), 27);
            encoder.addVal(suggestedActiveWaypointEstimatedTimeOfArrivalUTCHour, 5);
            encoder.addVal(suggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute, 6);
            if (suggestedFollowingWaypoints != null) {
                for (int i = 0; i < suggestedFollowingWaypoints.size(); i++) {
                    encoder.addVal(suggestedFollowingWaypoints.get(i).getWaypoint().getRawLongitude(), 28);
                    encoder.addVal(suggestedFollowingWaypoints.get(i).getWaypoint().getRawLatitude(), 27);
                    encoder.addVal(suggestedFollowingWaypoints.get(i).getEstimatedTimeOfArrival(), 8);
                }
            }
        }
        return encoder;
    }

    @Override
    public void parse(BinArray binArray) throws SixbitException {
        System.out.println(binArray.toString());
    }

    public AisPosition getSuggestedActiveWaypoint() {
        return suggestedActiveWaypoint;
    }

    public void setSuggestedActiveWaypoint(AisPosition suggestedActiveWaypoint) {
        this.suggestedActiveWaypoint = suggestedActiveWaypoint;
    }

    public int getSuggestedActiveWaypointEstimatedTimeOfArrivalUTCHour() {
        return suggestedActiveWaypointEstimatedTimeOfArrivalUTCHour;
    }

    public void setSuggestedActiveWaypointEstimatedTimeOfArrivalUTCHour(int suggestedActiveWaypointEstimatedTimeOfArrivalUTCHour) {
        this.suggestedActiveWaypointEstimatedTimeOfArrivalUTCHour = suggestedActiveWaypointEstimatedTimeOfArrivalUTCHour;
    }

    public int getSuggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute() {
        return suggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute;
    }

    public void setSuggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute(int suggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute) {
        this.suggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute = suggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute;
    }

    public List<ExtendedWaypoint> getSuggestedFollowingWaypoints() {
        return suggestedFollowingWaypoints;
    }

    public void addSuggestedWaypoint(ExtendedWaypoint waypoint) {
        if (suggestedFollowingWaypoints == null) {
            suggestedFollowingWaypoints = new LinkedList<>();
        }
        suggestedFollowingWaypoints.add(waypoint);
    }

    @Override
    public String toString() {
        return "FlowManagementSuggestion{" +
                "suggestedFollowingWaypoints=" + suggestedFollowingWaypoints +
                ", suggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute=" + suggestedActiveWaypointEstimatedTimeOfArrivalUTCMinute +
                ", suggestedActiveWaypointEstimatedTimeOfArrivalUTCHour=" + suggestedActiveWaypointEstimatedTimeOfArrivalUTCHour +
                ", suggestedActiveWaypoint=" + suggestedActiveWaypoint +
                "} " + super.toString();
    }

    public static class ExtendedWaypoint {
        private ExtendedWaypoint() {
        }

        public ExtendedWaypoint(AisPosition waypoint, int estimatedTimeOfArrival) {
            this.waypoint = waypoint;
            this.estimatedTimeOfArrival = estimatedTimeOfArrival;
        }

        @Override
        public String toString() {
            return "ExtendedWaypoint{" +
                    "waypoint=" + waypoint +
                    ", estimatedTimeOfArrival=" + estimatedTimeOfArrival +
                    '}';
        }

        public AisPosition getWaypoint() {
            return waypoint;
        }

        public int getEstimatedTimeOfArrival() {
            return estimatedTimeOfArrival;
        }

        public void setWaypoint(AisPosition waypoint) {
            this.waypoint = waypoint;
        }

        public void setEstimatedTimeOfArrival(int estimatedTimeOfArrival) {
            this.estimatedTimeOfArrival = estimatedTimeOfArrival;
        }

        private AisPosition waypoint;       // Waypoint position
        private int estimatedTimeOfArrival; // Minutes relative to previous waypoint
    }

}
