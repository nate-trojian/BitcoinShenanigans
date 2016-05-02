package probabilistic;

public class Distribution
{
    /*
     * Piece of the machine
     * Do enum for type
     * Public method calls private method with extra parameter type
     * Do function based on type
     */
    private DistType type;
    private ProbabilityBit[] probDist;
    public Distribution(DistType type)
    {
        this.type = type;
        probDist = new ProbabilityBit[type.bits()];
    }

    public ProbabilityBit[] generateDist(long maxVal)
    {
        /*long typeMax = type.maxValue();
        int bits = type.bits();
        long[] count = new long[bits];    //Count all the times we have a 0 in a bit
        for(long i=0; i<maxVal; i++)
        {
            String bitStr = padToLen(Long.toBinaryString(i%typeMax), bits);
            for(int j=0; j<bits; j++)
            {
                if(bitStr.charAt(j) == '0')
                    count[j]++;
            }
        }*/

        for(int i=0; i<type.bits(); i++)
        {
            probDist[i] = new ProbabilityBit();
        }
        return probDist;
    }

    private String padToLen(String str, int len)
    {
        String ret = str;
        while(ret.length() < len)
        {
            ret = "0"+ret;
        }
        return ret;
    }

    enum DistType
    {
        WORD(32);

        private final int bits;
        private final long maxVal;
        DistType(int bits)
        {
            this.bits = bits;
            this.maxVal = (long)Math.pow(2, bits) - 1;
        }

        public int bits()
        {
            return bits;
        }

        public long maxValue()
        {
            return maxVal;
        }
    }

    public static void main(String[] args)
    {
        System.out.println(DistType.WORD.bits());
        System.out.println(DistType.WORD.maxValue());
        Distribution dist = new Distribution(DistType.WORD);
        ProbabilityBit[] probDist = dist.generateDist(DistType.WORD.maxValue());
    }
}
