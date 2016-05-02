package bitcoin;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

import util.GenUtil;
import util.Int256;

public class TargetTest
{
    private final long MERKLE_ROOT = 0L;
    private final static String MAX_TARGET = "00000000FFFF0000000000000000000000000000000000000000000000000000";

    public static void main(String[] args)
    {
        SHA256 sha = new SHA256();
        ByteBuffer buffer = ByteBuffer.allocate(8);
        BigInteger ret;
        for(long i=1000; i<=3500; i++)
        {
            sha.init();
            buffer.putLong(i);
            sha.setData(buffer.array());
            byte[] output = sha.getDigest();
            Int256 testInt = new Int256(output);
            System.out.println("Int256\t\t" + testInt);
            BigInteger bigInt = new BigInteger(1, output);
            String outBig = GenUtil.padToLen(bigInt.toString(16), 64);
            System.out.println("Big Endian\t" + outBig);
            String outLit = GenUtil.switchEndian(outBig, 16);
            System.out.println("Little Endian\t" + outLit);
            String endTest = "";
            for(int j=0; j<outBig.length(); j++)
            {
                int loc = outBig.length()-2-j+2*(j%2);
                endTest += outBig.charAt(loc);
            }
            System.out.println("End Test\t" + endTest);
            System.out.println("Target\t\t" + MAX_TARGET);
            buffer.clear();
        }
        String header = ("01000000" +
                "81cd02ab7e569e8bcd9317e2fe99f2de44d49ab2b8851ba4a308000000000000" +
                "e320b6c2fffc8d750423db8b1eb942ae710e951ed797f7affc8892b0f1fc122b" +
                "c7f5d74d" +
                "f2b9441a" +
                 "42a14695");
        System.out.println("Header Length " + (header.length()/2));
        sha.init();
        sha.setData(GenUtil.hexStrToByteArr(header));
        // want h7 % 0xA41F32E6 = 0
        //1A8FD04D
        byte[] output = sha.getDigest();
        System.out.println("Output:");
        for(int i=0; i<output.length; i++)
        {
            System.out.print(GenUtil.padToLen(Integer.toHexString(GenUtil.unsignedByte(output[i])), 2));
        }
        System.out.println("");
        System.out.println(MAX_TARGET);
        //Rehash it
        sha.init();
        sha.setData(output);
        output = sha.getDigest();
        System.out.println("Output:");
        for(int i=0; i<output.length; i++)
        {
            System.out.print(GenUtil.padToLen(Integer.toHexString(GenUtil.unsignedByte(output[i])), 2));
        }
        System.out.println("");
        System.out.println(MAX_TARGET);
    }
}
