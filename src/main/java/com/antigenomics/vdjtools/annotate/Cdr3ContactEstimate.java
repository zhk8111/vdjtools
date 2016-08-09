/*
 * Copyright (c) 2015, Bolotin Dmitry, Chudakov Dmitry, Shugay Mikhail
 * (here and after addressed as Inventors)
 * All Rights Reserved
 *
 * Permission to use, copy, modify and distribute any part of this program for
 * educational, research and non-profit purposes, by non-profit institutions
 * only, without fee, and without a written agreement is hereby granted,
 * provided that the above copyright notice, this paragraph and the following
 * three paragraphs appear in all copies.
 *
 * Those desiring to incorporate this work into commercial products or use for
 * commercial purposes should contact the Inventors using one of the following
 * email addresses: chudakovdm@mail.ru, chudakovdm@gmail.com
 *
 * IN NO EVENT SHALL THE INVENTORS BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
 * SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
 * ARISING OUT OF THE USE OF THIS SOFTWARE, EVEN IF THE INVENTORS HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * THE SOFTWARE PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE INVENTORS HAS
 * NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, OR
 * MODIFICATIONS. THE INVENTORS MAKES NO REPRESENTATIONS AND EXTENDS NO
 * WARRANTIES OF ANY KIND, EITHER IMPLIED OR EXPRESS, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE, OR THAT THE USE OF THE SOFTWARE WILL NOT INFRINGE ANY
 * PATENT, TRADEMARK OR OTHER RIGHTS.
 */

package com.antigenomics.vdjtools.annotate;

import com.milaboratory.core.sequence.AminoAcidSequence;

public class Cdr3ContactEstimate implements AaProperty {
    private final float[][] values;
    private final float defaultValue;
    private final int offset;

    public Cdr3ContactEstimate(float[][] values, float defaultValue) {
        this.values = values;
        this.defaultValue = defaultValue;

        if (values.length != AminoAcidSequence.ALPHABET.size()) {
            throw new IllegalArgumentException("Number of rows in value matrix should " +
                    "be equal to AminoAcidSequence.ALPHABET size.");
        }

        int m = values[0].length;

        for (int i = 1; i < values.length; i++) {
            if (values[i].length != m) {
                throw new IllegalArgumentException("Value matrix should have rows of the same length " +
                        "(by definition of a matrix, isn't it).");
            }
        }

        this.offset = m / 2;

        if (m <= 0 || m % 2 == 0) {
            throw new IllegalArgumentException("Number of columns in value matrix should be odd and greater than 0.");
        }

        for (int i = 0; i < values.length; i++) {
            float[] vv = values[i];
            for (int j = 0; j < vv.length; j++) {
                values[i][j] = -(float) Math.log(1.0 - values[i][j]);
            }
        }
    }

    @Override
    public String getName() {
        return "cdr3contact";
    }

    @Override
    public float compute(AminoAcidSequence sequence, int pos) {
        int len = sequence.size();
        int bin = offset + (pos - len / 2);
        if (bin < 0 || bin >= values[0].length) {
            return defaultValue;
        }
        return values[sequence.codeAt(pos)][bin];
    }
}
