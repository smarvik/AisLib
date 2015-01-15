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

/**
 * ASM for suggesting a route to a vessel
 */
public class TacticalVoyagePlanInquiry extends AisApplicationMessage {

    public static final int DAC = 219;
    public static final int FI = 5;

    private int duration;

    public TacticalVoyagePlanInquiry() {
        super(DAC, FI);
    }

    public TacticalVoyagePlanInquiry(BinArray binArray) throws SixbitException {
        super(DAC, FI, binArray);
        duration = (int) binArray.getVal(8);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public SixbitEncoder getEncoded() {
        SixbitEncoder encoder = new SixbitEncoder();
        encoder.addVal(duration, 8);
        return encoder;
    }

    @Override
    public void parse(BinArray binArray) throws SixbitException {
        System.out.println(binArray.toString());
    }

    @Override
    public String toString() {
        return "TacticalVoyagePlanInquiry{" +
                "duration=" + duration +
                "} " + super.toString();
    }

}
