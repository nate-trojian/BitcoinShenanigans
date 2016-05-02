package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EncodeGenerator
{
    //Procedurally generate terms for double encoded message
    //First 16 elements previous message
    private String[][] words;

    public EncodeGenerator()
    {
        words = new String[64][32];
        init();
        generateWords();
    }

    public void init()
    {
        for(int i=0; i<16; i++)
        {
            for(int j=0; j<32; j++)
            {
                words[i][j] = "w(" + i + "," + j + ")";
            }
        }
    }

    //TODO Redo to match recursive definition, strings getting to big
    public void generateWords()
    {
        String s0 = "", s1 = "";
        for(int i=16; i<64; i++)
        {
            for(int j=0; j<32; j++)
            {
                //Generate s0
                s0 = "( ";
                s0 += "w(" + (i-15) + "," + (j+33-7)%32 + ")";  //Right rotate 7
                s0 += " xor ";
                s0 += " w(" + (i-15) + "," + (j+33-18)%32 + ")";  //Right rotate 18
                s0 += (j>=3?" xor w(" + (i-15) + "," + (j-3) + ")":"");  //Right shift 3
                s0 += " )";

                //Generate s1
                s1 = "( ";
                s1 += "w(" + (i-2) + "," + (j+33-17)%32 + ")";  //Right rotate 17
                s1 += " xor ";
                s1 += " w(" + (i-2) + "," + (j+33-19)%32 + ")";  //Right rotate 19
                s1 += (j>=10?" xor w(" + (i-2) + "," + (j-10) + ")":"");  //Right shift 10
                s1 += " )";

                words[i][j] = "(" + delimSep(" + ", s0, s1, "w(" + (i-16) + "," + j + ")", "w(" + (i-7) + "," + j + ")") + ")";
            }
        }
    }

    private String delimSep(String delim, String...strings)
    {
        String ret = "";
        for(int i=0; i<strings.length-1; i++)
        {
            ret += strings[i];
            ret += delim;
        }
        ret += strings[strings.length-1];
        return ret;
    }

    public void printWordBitWise(int wordNum)
    {
        for(int j=0; j<32; j++)
        {
            System.out.println("Bit " + j + ": " + genFullString(words[wordNum][j]));
        }
    }

    public String genFullString(String s)
    {
        String ret = s;
        Pattern p = Pattern.compile("-?\\d+");
        Matcher m = p.matcher(s);
        int word = -1, bit = -1;
        while(m.find())
        {
            if(word == -1)
            {
                word = Integer.parseInt(m.group());
            }
            else
            {
                if(word >= 16)
                {
                    //System.out.println("Replacing " + word);
                    bit = Integer.parseInt(m.group());
                    ret = ret.replace("w("+word+","+bit+")", genFullString(words[word][bit]));
                }
                word = -1;
            }
        }
        return ret;
    }

    public static void main(String[] args)
    {
        EncodeGenerator eg = new EncodeGenerator();
        eg.printWordBitWise(16);
    }
}
