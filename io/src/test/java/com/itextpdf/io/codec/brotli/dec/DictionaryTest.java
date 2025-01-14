/* Copyright 2015 Google Inc. All Rights Reserved.

   Distributed under MIT license.
   See file LICENSE for detail or copy at https://opensource.org/licenses/MIT
*/

package com.itextpdf.io.codec.brotli.dec;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 * Tests for {@link Dictionary}.
 */
@Tag("UnitTest")
public class DictionaryTest extends ExtendedITextTest {

    @Test
    public void testGetData() {
        assertEquals(37084801881332636L, crc64(Dictionary.getData()));
    }

    private static long crc64(ByteBuffer data) {
        long crc = -1;
        for (int i = 0; i < data.capacity(); ++i) {
            long c = (crc ^ (long) (data.get(i) & 0xFF)) & 0xFF;
            for (int k = 0; k < 8; k++) {
                c = (c >>> 1) ^ (-(c & 1L) & -3932672073523589310L);
            }
            crc = c ^ (crc >>> 8);
        }
        return ~crc;
    }
}