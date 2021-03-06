/*********************************************************************************
 *                                                                               *
 * The MIT License                                                               *
 *                                                                               *
 * Copyright (c) 2015-2020 aoju.org and other contributors.                      *
 *                                                                               *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  *
 * of this software and associated documentation files (the "Software"), to deal *
 * in the Software without restriction, including without limitation the rights  *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell     *
 * copies of the Software, and to permit persons to whom the Software is         *
 * furnished to do so, subject to the following conditions:                      *
 *                                                                               *
 * The above copyright notice and this permission notice shall be included in    *
 * all copies or substantial portions of the Software.                           *
 *                                                                               *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR    *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,      *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE   *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER        *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, *
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN     *
 * THE SOFTWARE.                                                                 *
 ********************************************************************************/
package org.aoju.bus.health.hardware.mac;

import org.aoju.bus.health.Builder;
import org.aoju.bus.health.hardware.AbstractHardwareLayer;
import org.aoju.bus.health.hardware.AbstractSoundCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Sound card data obtained via system_profiler
 *
 * @author Kimi Liu
 * @version 5.6.9
 * @since JDK 1.8+
 */
public class MacSoundCard extends AbstractSoundCard {

    private static final String APPLE = "Apple Inc.";

    /**
     * <p>
     * Constructor for MacSoundCard.
     * </p>
     *
     * @param kernelVersion a {@link java.lang.String} object.
     * @param name          a {@link java.lang.String} object.
     * @param codec         a {@link java.lang.String} object.
     */
    public MacSoundCard(String kernelVersion, String name, String codec) {
        super(kernelVersion, name, codec);
    }

    /**
     * public method used by
     * {@link AbstractHardwareLayer} to access the
     * sound cards.
     *
     * @return List of {@link MacSoundCard} objects.
     */
    public static List<MacSoundCard> getSoundCards() {
        List<MacSoundCard> soundCards = new ArrayList<>();

        String manufacturer = APPLE;
        String kernelVersion = "AppleHDAController";
        String codec = "AppleHDACodec";

        boolean version = false;
        String versionMarker = "<key>com.apple.driver.AppleHDAController</key>";

        for (final String checkLine : Builder.readFile("/System/Library/Extensions/AppleHDA.kext/Contents/Info.plist")) {
            if (checkLine.contains(versionMarker)) {
                version = true;
                continue;
            }
            if (version) {
                kernelVersion = "AppleHDAController "
                        + Builder.getTextBetweenStrings(checkLine, "<string>", "</string>");
                version = false;
            }
        }
        soundCards.add(new MacSoundCard(kernelVersion, manufacturer, codec));

        return soundCards;
    }

}
