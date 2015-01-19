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
 * ASM for broadcasting tactical voyageplan
 */
public class TacticalVoyagePlan extends AisApplicationMessage {

    public static final int DAC = 219;
    public static final int FI = 4;

    private AisPosition activeWaypoint;
    private int activeWaypointEstimatedTimeOfArrivalUTCHour;
    private int activeWaypointEstimatedTimeOfArrivalUTCMinute;
    private int activeWaypointTurnCircleRadius; // 1/100 nautical mile
    private List<ExtendedWaypoint> followingWaypoints;

    public TacticalVoyagePlan() {
        super(DAC, FI);
    }

    public TacticalVoyagePlan(BinArray binArray) throws SixbitException {
        super(DAC, FI, binArray);
        if (binArray.hasMoreBits()) {
            activeWaypoint = new AisPosition();
            activeWaypoint.setRawLongitude(binArray.getVal(28));
            activeWaypoint.setRawLatitude(binArray.getVal(27));
            activeWaypointEstimatedTimeOfArrivalUTCHour = (int) binArray.getVal(5);
            activeWaypointEstimatedTimeOfArrivalUTCMinute = (int) binArray.getVal(6);
            activeWaypointTurnCircleRadius = (int) binArray.getVal(8);
            while (binArray.hasMoreBits()) {
                ExtendedWaypoint followingWaypoint = new ExtendedWaypoint();
                AisPosition waypoint = new AisPosition();
                waypoint.setRawLongitude(binArray.getVal(28));
                waypoint.setRawLatitude(binArray.getVal(27));
                followingWaypoint.setWaypoint(waypoint);
                followingWaypoint.setEstimatedTimeOfArrival((int) binArray.getVal(8));
                followingWaypoint.setTurnCircleRadius((int) binArray.getVal(8));
                addFollowingWaypoint(followingWaypoint);
            }
        }
    }

    @Override
    public SixbitEncoder getEncoded() {
        SixbitEncoder encoder = new SixbitEncoder();
        if (activeWaypoint != null) {
            encoder.addVal(activeWaypoint.getRawLongitude(), 28);
            encoder.addVal(activeWaypoint.getRawLatitude(), 27);
            encoder.addVal(activeWaypointEstimatedTimeOfArrivalUTCHour, 5);
            encoder.addVal(activeWaypointEstimatedTimeOfArrivalUTCMinute, 6);
            encoder.addVal(activeWaypointTurnCircleRadius, 8);
            if (followingWaypoints != null) {
                for (int i = 0; i < followingWaypoints.size(); i++) {
                    encoder.addVal(followingWaypoints.get(i).getWaypoint().getRawLongitude(), 28);
                    encoder.addVal(followingWaypoints.get(i).getWaypoint().getRawLatitude(), 27);
                    encoder.addVal(followingWaypoints.get(i).getEstimatedTimeOfArrival(), 8);
                    encoder.addVal(followingWaypoints.get(i).getTurnCircleRadius(), 8);
                }
            }
        }
        return encoder;
    }

    @Override
    public void parse(BinArray binArray) throws SixbitException {
        System.out.println(binArray.toString());
    }

    public AisPosition getActiveWaypoint() {
        return activeWaypoint;
    }

    public int getActiveWaypointEstimatedTimeOfArrivalUTCHour() {
        return activeWaypointEstimatedTimeOfArrivalUTCHour;
    }

    public int getActiveWaypointEstimatedTimeOfArrivalUTCMinute() {
        return activeWaypointEstimatedTimeOfArrivalUTCMinute;
    }

    public void setActiveWaypoint(AisPosition activeWaypoint) {
        this.activeWaypoint = activeWaypoint;
    }

    public void setActiveWaypointEstimatedTimeOfArrivalUTCHour(int activeWaypointEstimatedTimeOfArrivalUTCHour) {
        this.activeWaypointEstimatedTimeOfArrivalUTCHour = activeWaypointEstimatedTimeOfArrivalUTCHour;
    }

    public void setActiveWaypointEstimatedTimeOfArrivalUTCMinute(int activeWaypointEstimatedTimeOfArrivalUTCMinute) {
        this.activeWaypointEstimatedTimeOfArrivalUTCMinute = activeWaypointEstimatedTimeOfArrivalUTCMinute;
    }

    public void setActiveWaypointTurnCircleRadius(int activeWaypointTurnCircleRadius) {
        this.activeWaypointTurnCircleRadius = activeWaypointTurnCircleRadius;
    }

    public int getActiveWaypointTurnCircleRadius() {
        return activeWaypointTurnCircleRadius;
    }

    public List<ExtendedWaypoint> getFollowingWaypoints() {
        return followingWaypoints;
    }

    public void addFollowingWaypoint(ExtendedWaypoint waypoint) {
        if (followingWaypoints == null) {
            followingWaypoints = new LinkedList<>();
        }
        followingWaypoints.add(waypoint);
    }

    @Override
    public String toString() {
        return "TacticalVoyagePlan{" +
                "activeWaypoint=" + activeWaypoint +
                ", activeWaypointEstimatedTimeOfArrivalUTCHour=" + activeWaypointEstimatedTimeOfArrivalUTCHour +
                ", activeWaypointEstimatedTimeOfArrivalUTCMinute=" + activeWaypointEstimatedTimeOfArrivalUTCMinute +
                ", activeWaypointTurnCircleRadius=" + activeWaypointTurnCircleRadius +
                ", followingWaypoints=" + followingWaypoints +
                ", encoded=" + getEncoded() +
                "} " + super.toString();
    }

    public static class ExtendedWaypoint {
        private ExtendedWaypoint() {
        }

        public ExtendedWaypoint(AisPosition waypoint, int estimatedTimeOfArrival, int turnCircleRadius) {
            this.waypoint = waypoint;
            this.estimatedTimeOfArrival = estimatedTimeOfArrival;
            this.turnCircleRadius = turnCircleRadius;
        }

        @Override
        public String toString() {
            return "ExtendedWaypoint{" +
                    "waypoint=" + waypoint +
                    ", estimatedTimeOfArrival=" + estimatedTimeOfArrival +
                    ", turnCircleRadius=" + turnCircleRadius +
                    '}';
        }

        public AisPosition getWaypoint() {
            return waypoint;
        }

        public int getEstimatedTimeOfArrival() {
            return estimatedTimeOfArrival;
        }

        public int getTurnCircleRadius() {
            return turnCircleRadius;
        }

        public void setWaypoint(AisPosition waypoint) {
            this.waypoint = waypoint;
        }

        public void setEstimatedTimeOfArrival(int estimatedTimeOfArrival) {
            this.estimatedTimeOfArrival = estimatedTimeOfArrival;
        }

        public void setTurnCircleRadius(int turnCircleRadius) {
            this.turnCircleRadius = turnCircleRadius;
        }

        private AisPosition waypoint;       // Waypoint position
        private int estimatedTimeOfArrival; // Minutes relative to previous waypoint
        private int turnCircleRadius;     // 1/100 nautical miles
    }

}
