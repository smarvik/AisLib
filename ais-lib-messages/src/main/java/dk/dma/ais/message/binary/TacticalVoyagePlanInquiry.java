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

/**
 * ASM for suggesting a route to a vessel
 */
public class TacticalVoyagePlanInquiry extends AisApplicationMessage {

    public static final int DAC = 219;
    public static final int FI = 5;

    private AisPosition controlledAreaNorthWest;
    private AisPosition controlledAreaSouthEast;

    public TacticalVoyagePlanInquiry() {
        super(DAC, FI);
    }

    public TacticalVoyagePlanInquiry(BinArray binArray) throws SixbitException {
        super(DAC, FI, binArray);

        if (binArray.hasMoreBits()) {
            final long northRaw = binArray.getVal(28);
            final long eastRaw = binArray.getVal(27);
            final long southRaw = binArray.getVal(28);
            final long westRaw = binArray.getVal(27);
            controlledAreaNorthWest = new AisPosition(northRaw, westRaw);
            controlledAreaSouthEast = new AisPosition(southRaw, eastRaw);
        }
    }

    public AisPosition getControlledAreaNorthWest() {
        return controlledAreaNorthWest;
    }

    public void setControlledAreaNorthWest(AisPosition controlledAreaNorthWest) {
        this.controlledAreaNorthWest = controlledAreaNorthWest;
    }

    public AisPosition getControlledAreaSouthEast() {
        return controlledAreaSouthEast;
    }

    public void setControlledAreaSouthEast(AisPosition controlledAreaSouthEast) {
        this.controlledAreaSouthEast = controlledAreaSouthEast;
    }

    @Override
    public SixbitEncoder getEncoded() {
        SixbitEncoder encoder = new SixbitEncoder();

        if (controlledAreaNorthWest != null && controlledAreaSouthEast != null) {
            encoder.addVal(controlledAreaNorthWest.getRawLatitude(), 28);  // N
            encoder.addVal(controlledAreaSouthEast.getRawLongitude(), 27); // E
            encoder.addVal(controlledAreaSouthEast.getRawLatitude(), 28);  // S
            encoder.addVal(controlledAreaNorthWest.getRawLongitude(), 27); // W
        }
        return encoder;
    }

    @Override
    public void parse(BinArray binArray) throws SixbitException {
        System.out.println(binArray.toString());
        /*
        this.waypoints = new ArrayList<>();
        this.msgLinkId = (int) binArray.getVal(10);
        this.routeType = (int) binArray.getVal(5);
        super.parse(binArray);
        */
    }

    @Override
    public String toString() {
        return "TacticalVoyagePlanInquiry{" +
                "controlledAreaNorthWest=" + controlledAreaNorthWest +
                ", controlledAreaSouthEast=" + controlledAreaSouthEast +
                "} " + super.toString();
    }

}
