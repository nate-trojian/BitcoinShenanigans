package util;

import java.math.BigInteger;

public class Int256 implements Comparable<Int256>
{
    private int[] bytes; //Stores in Big Endian
    private String strRep;

    public Int256(String num, boolean bigEndian)
    {
        bytes = new int[64];
        if(num.length() > 64)
        {
            //Truncate it
            num = num.substring(0, 64);
        }
        num = GenUtil.padToLen(num, 64);
        if(!bigEndian)
            num = GenUtil.switchEndian(num, 16);
        for(int i=0; i<num.length(); i++)
        {
            bytes[i] = Integer.parseInt(num.charAt(i)+"", 16);
        }
        strRep = num;
    }

    public Int256(String num)
    {
        this(num, true);
    }

    public Int256(byte[] num)
    {
        bytes = new int[64];
        strRep = "";
        for(int i=0; i<32; i++)
        {
            bytes[2*i] = (num[i] & 0xF0) >>> 4;
            strRep += Integer.toHexString(bytes[2*i]);
            bytes[2*i + 1] = num[i] & 0x0F;
            strRep += Integer.toHexString(bytes[2*i + 1]);
        }
    }

    public BigInteger getBigInt()
    {
        return new BigInteger(strRep, 16);
    }

    public String littleEndian()
    {
        return GenUtil.switchEndian(strRep, 16);
    }

    public String bigEndian()
    {
        return strRep;
    }

    public String toString()
    {
        return strRep;
    }

    @Override
    public int compareTo(Int256 o)
    {
        for(int i=0; i<bytes.length; i++)
        {
            if(bytes[i] < o.bytes[i])
                return -1;
            else if(bytes[i] > o.bytes[i])
                return 1;
        }
        return 0;
    }
}
