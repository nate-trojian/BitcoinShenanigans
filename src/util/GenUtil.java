package util;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;

public class GenUtil
{
    public static String padToLen(String str, int len)
    {
        String ret = str;
        while(ret.length()<len)
            ret = "0"+ret;
        return ret;
    }

    public static String switchEndian(String str, int radix)
    {
        String ret = "";
        int byteSize = (int) (Math.log10(256)/Math.log10(radix));
        for(int i=0; i<str.length(); i++)
        {
            int loc = (str.length() - byteSize) - i + 2*(i%byteSize);
            ret += str.charAt(loc);
        }
        return ret;
    }

    public static byte[] hexStrToByteArr(String str)
    {
        ByteBuffer buffer = ByteBuffer.allocate(str.length()/2);
        for(int i=0; i<str.length(); i+=2)
        {
            Byte temp;
            Byte hi = Byte.valueOf(""+str.charAt(i), 16);
            temp = (byte) ((hi & 0xFF) << 4);
            Byte low = Byte.valueOf(""+str.charAt(i+1), 16);
            temp = (byte) ((temp | low) & 0xFF);
            buffer.put(temp);
        }
        return buffer.array();
    }

    public static int unsignedByte(byte b)
    {
        return (b & 0xFF);
    }

    public static long unsignedInt(int i)
    {
        return i & 0xffffffffl;
    }

    public static boolean isNumeric(String s)
    {
        try
        {
            Integer.valueOf(s);
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    public static <T> void insert(ArrayList<T> list, T elem, Comparator<T> c)
    {
        boolean added = false;
        for(int i=0; i< list.size(); i++)
        {
            if(c.compare(elem, list.get(i)) <= 0)
            {
                list.add(i, elem);
                added = true;
                break;
            }
        }
        if(!added)
            list.add(elem);
    }
}