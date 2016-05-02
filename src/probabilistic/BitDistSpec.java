package probabilistic;

import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;

public class BitDistSpec
{
    private final double eps = 0.001d;

    @Test
    public void mapConstant()
    {
        Double[] expected = {0d, 1d, 1d, 1d, 1d};
        BitDist bit = new BitDist(30L, 5);
        assertDoubleArraysEqual(expected, bit.distribution(), eps);
    }

    @Test
    public void rRotateBy2()
    {
        Double[] arr = {0.5d, 0.3d, 0.2d, 0.7d, 0.8d};
        Double[] expected = {0.7d, 0.8d, 0.5d, 0.3d, 0.2d};
        BitDist bit = new BitDist(arr);
        bit.rRotate(2);
        assertDoubleArraysEqual(expected, bit.distribution(), eps);
    }

    @Test
    public void lRotateBy2()
    {
        Double[] arr = {0.5d, 0.3d, 0.2d, 0.7d, 0.8d};
        Double[] expected = {0.2d, 0.7d, 0.8d, 0.5d, 0.3d};
        BitDist bit = new BitDist(arr);
        bit.lRotate(2);
        assertDoubleArraysEqual(expected, bit.distribution(), eps);
    }

    @Test
    public void rShiftBy2()
    {
        Double[] arr = {0.5d, 0.3d, 0.2d, 0.7d, 0.8d};
        Double[] expected = {0.2d, 0.7d, 0.8d, 0d, 0d};
        BitDist bit = new BitDist(arr);
        bit.rShift(2);
        assertDoubleArraysEqual(expected, bit.distribution(), eps);
    }

    @Test
    public void lShiftBy2()
    {
        Double[] arr = {0.5d, 0.3d, 0.2d, 0.7d, 0.8d};
        Double[] expected = {0d, 0d, 0.5d, 0.3d, 0.2d};
        BitDist bit = new BitDist(arr);
        bit.lShift(2);
        assertDoubleArraysEqual(expected, bit.distribution(), eps);
    }

    @Test
    public void xor()
    {
        Double[] arr1 = {0.5d, 0.3d, 0.2d, 0.7d, 0.8d};
        Double[] arr2 = {0.2d, 0.7d, 0.8d, 0d, 0d};
        Double[] expected = {0.5d, 0.58d, 0.68d, 0.7d, 0.8d};
        BitDist bit1 = new BitDist(arr1);
        BitDist bit2 = new BitDist(arr2);
        BitDist bit3 = bit1.xor(bit2);
        assertDoubleArraysEqual(expected, bit3.distribution(), eps);
    }

    @Test
    public void getProbOfNum()
    {
        Double[] arr = {0.5d, 0.3d, 0.2d, 0.7d, 0.8d};
        BitDist bit = new BitDist(arr);
        assertEquals(0.0042d, bit.probForNum(5L).doubleValue(), eps);
    }

    @Test
    public void add()
    {
        Double[] arr1 = {0.5d, 0.3d, 0.2d, 0.7d, 0.8d};
        Double[] arr2 = {0.2d, 0.7d, 0.8d, 0d, 0d};
        Double[] expected = {0.5d, 0.58d, 0.68d, 0.7d, 0.8d};
        BitDist bit1 = new BitDist(arr1);
        BitDist bit2 = new BitDist(arr2);
        BitDist bit3 = bit1.add(bit2);
        assertDoubleArraysEqual(expected, bit3.distribution(), eps);
    }

    @Test
    public void addBig()
    {
        Double[] expected = new Double[32];
        Arrays.fill(expected, 0.5d);
        BitDist bit1 = new BitDist(32);
        BitDist bit2 = new BitDist(32);
        BitDist bit3 = bit1.add(bit2);
        assertDoubleArraysEqual(expected, bit3.distribution(), eps);
    }

    private void assertDoubleArraysEqual(Double[] expected, Double[] distribution, double delta)
    {
        assertEquals(expected.length, distribution.length);
        for(int i=0; i<expected.length; i++)
        {
            assertEquals(expected[i].doubleValue(), distribution[i].doubleValue(), delta);
        }
    }
}
