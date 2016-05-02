package probabilistic;

import java.util.ArrayList;
import java.util.Arrays;

import util.GenUtil;

public class BitDist
{
    private Long MAX_VAL;
    private ArrayList<Double> arr; //TODO this doesn't need to be an ArrayList

    public BitDist(int n)
    {
        MAX_VAL = (long) Math.pow(2, n) - 1L;
        arr = new ArrayList<Double>(n);
        for(int i=0; i<n; i++)
        {
            arr.add(0.5d);
        }
    }

    public BitDist(long num, int n)
    {
        MAX_VAL = (long) Math.pow(2, n) - 1L;
        arr = new ArrayList<Double>(n);
        Long rem, temp = num;
        for(int i=0; i<n; i++)
        {
            rem = temp % 2L;
            if(temp > 0) temp /= 2L;
            arr.add(rem.doubleValue());
        }
    }

    public BitDist(Double... tempArr)
    {
        MAX_VAL = (long) Math.pow(2, tempArr.length) - 1L;
        arr = new ArrayList<Double>();
        for(Double temp: tempArr)
        {
            arr.add(temp);
        }
    }

    public Double[] distribution()
    {
        return arr.toArray(new Double[0]);
    }

    public Double probForBit(int n)
    {
        return arr.get(n);
    }

    public Double probForNum(Long num)
    {
        Double res = 1d;
        Long temp = num;
        Long rem;
        for(int i=0; i<arr.size(); i++)
        {
            rem = temp % 2L;
            if(temp > 0) temp /= 2L;
            res *= (rem==1?arr.get(i):1d-arr.get(i));
        }
        return res;
    }

    public int size()
    {
        return arr.size();
    }

    public void rRotate(int n)
    {
        int offset = arr.size() - n % arr.size();
        if (offset > 0)
        {
            Double[] copy = arr.toArray(new Double[0]);
            for (int i = 0; i < arr.size(); i++)
            {
                int j = (i + offset) % arr.size();
                arr.set(i, copy[j]);
            }
        }
    }

    public void lRotate(int n)
    {
        int offset = arr.size() - n % arr.size();
        if (offset > 0)
        {
            Double[] copy = arr.toArray(new Double[0]);
            for (int i = 0; i < arr.size(); i++)
            {
                int j = (i - offset + arr.size()) % arr.size();
                arr.set(i, copy[j]);
            }
        }
    }

    public void lShift(int n)
    {
        //Need to shift elements to the right, because little endian
        //11010 >> 2 = 00110
        //{0,1,0,1,1} >> 2 = {0,1,1,0,0}
        int offset = n % arr.size();
        if (offset > 0)
        {
            Double[] copy = Arrays.copyOfRange(arr.toArray(new Double[0]), 0, offset+1);
            for(int i = 0; i<offset; i++)
            {
                arr.set(i, 0d);
            }
            for(int i=0; i<arr.size()-offset; i++)
            {
                arr.set(i+offset, copy[i]);
            }
        }
    }

    public void rShift(int n)
    {
        //Shift elements to the left, because little endian
        //11010 << 2 = 01000
        //{0,1,0,1,1} << 2 = {0,0,0,1,0}
        int offset = n % arr.size();
        if (offset > 0)
        {
            Double[] copy = Arrays.copyOfRange(arr.toArray(new Double[0]), offset, arr.size());
            for(int i=0; i<arr.size()-offset; i++)
            {
                arr.set(i, copy[i]);
            }
            for(int i=arr.size()-offset; i<arr.size(); i++)
            {
                arr.set(i, 0d);
            }
        }
    }

    public BitDist xor(BitDist other)
    {
        assert(other.size() == size());
        Double[] tempArr = new Double[arr.size()];
        Double[] otherDist = other.distribution();
        for(int i=0; i<arr.size(); i++)
        {
            tempArr[i] = arr.get(i)*(1-otherDist[i]) + (1-arr.get(i))*otherDist[i];
        }
        return new BitDist(tempArr);
    }

    public BitDist and(BitDist other)
    {
        assert(other.size() == size());
        Double[] tempArr = new Double[arr.size()];
        Double[] otherDist = other.distribution();
        for(int i=0; i<arr.size(); i++)
        {
            tempArr[i] = arr.get(i)*otherDist[i];
        }
        return new BitDist(tempArr);
    }

    public BitDist not()
    {
        Double[] tempArr = new Double[arr.size()];
        for(int i=0; i<arr.size(); i++)
        {
            tempArr[i] = 1-arr.get(i);
        }
        return new BitDist(tempArr);
    }

    /*TODO too long, need to compact it
    public BitDist add(BitDist other)
    {
        assert(other.size() == size());
        //ArrayList<Double> check = new ArrayList<Double>();
        Double[] tempArr = new Double[arr.size()];
        Arrays.fill(tempArr, 0d);
        //Gotta do it the long way
        //System.out.println(MAX_VAL);
        for(long i=1; i<= MAX_VAL; i++)
        {
            //Get prob of number occurring with mod
            Double resForNum = 0d;
            for(long j=0; j<=i; j++)
            {
                //System.out.println(j + " " + (i-j));
                resForNum += this.probForNum(j) * other.probForNum(i-j);
            }
            //System.out.println("Wrap Around");
            //When i == MAX_VAL, no wrap around possible
            if(i != MAX_VAL)
                for(long j=i+1; j<=MAX_VAL; j++)
                {
                    //System.out.println(j + " " + (i+MAX_VAL+1-j));
                    resForNum += (this.probForNum(j) * other.probForNum(i+MAX_VAL+1-j));
                }

            //System.out.println(GenUtil.padToLen(Long.toBinaryString(i), arr.size()) + " " + resForNum);
            //check.add(resForNum);
            //convert num to bits, add to tempArr
            //Just go through bit array
            Long temp = i;
            Long rem;
            int count = 0;
            while(temp > 0)
            {
                rem = temp % 2;
                temp /= 2;
                if(rem == 1) tempArr[count] += resForNum;
                count++;
            }
        }
//        Double checkSum = this.probForNum(0L) * other.probForNum(0L);
//        for(long j=1; j<=MAX_VAL; j++)
//        {
//            checkSum += this.probForNum(j) * other.probForNum(MAX_VAL + 1 - j);
//        }
//        System.out.println(checkSum);
//        for(Double d: check)
//            checkSum += d;
//        System.out.println(checkSum);
        return new BitDist(tempArr);
    }
    */

    public BitDist add(BitDist other)
    {
        return this.xor(other);
    }

    public BitDist clone()
    {
        return new BitDist(this.distribution());
    }

    public String toString()
    {
        String ret = "{";
        if(arr.size() > 0) ret += arr.get(0);
        for(int i=1; i< arr.size(); i++)
        {
            ret += "," + arr.get(i);
        }
        ret += "}";
        return ret;
    }
}
