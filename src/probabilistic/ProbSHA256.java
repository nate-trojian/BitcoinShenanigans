package probabilistic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ProbSHA256
{
    private BitDist Ch(BitDist a, BitDist b, BitDist c)
    {
        return (a.and(b)).xor((a.not()).xor(c));
    }

    private BitDist Maj(BitDist a, BitDist b, BitDist c)
    {
        return (a.and(b)).xor(a.and(c)).xor(b.and(c));
    }

    private BitDist SHR(BitDist x, int n)
    {
        BitDist ret = x.clone();
        ret.rShift(n);
        return ret;
    }

    private BitDist ROTR(BitDist x, int n)
    {
        BitDist ret = x.clone();
        ret.rRotate(n);
        return ret;
    }

    private BitDist SIG0(BitDist x)
    {
        return ROTR(x, 2).xor(ROTR(x, 13)).xor(ROTR(x, 22));
    }

    private BitDist SIG1(BitDist x)
    {
        return ROTR(x, 6).xor(ROTR(x, 11)).xor(ROTR(x, 25));
    }

    private BitDist sig0(BitDist x)
    {
        return ROTR(x, 7).xor(ROTR(x, 18)).xor(SHR(x, 3));
    }

    private BitDist sig1(BitDist x)
    {
        return ROTR(x, 17).xor(ROTR(x, 19)).xor(SHR(x, 10));
    }

    // Constants "K"
    private static final long K[] = {
        1116352408L, 1899447441L, 3049323471L, 3921009573L, 961987163L,
        1508970993L, 2453635748L, 2870763221L, 3624381080L, 310598401L,
        607225278L, 1426881987L, 1925078388L, 2162078206L, 2614888103L,
        3248222580L, 3835390401L, 4022224774L, 264347078L, 604807628L,
        770255983L, 1249150122L, 1555081692L, 1996064986L, 2554220882L,
        2821834349L, 2952996808L, 3210313671L, 3336571891L, 3584528711L,
        113926993L, 338241895L, 666307205L, 773529912L, 1294757372L,
        1396182291L, 1695183700L, 1986661051L, 2177026350L, 2456956037L,
        2730485921L, 2820302411L, 3259730800L, 3345764771L, 3516065817L,
        3600352804L, 4094571909L, 275423344L, 430227734L, 506948616L,
        659060556L, 883997877L, 958139571L, 1322822218L, 1537002063L,
        1747873779L, 1955562222L, 2024104815L, 2227730452L, 2361852424L,
        2428436474L, 2756734187L, 3204031479L, 3329325298L
    };

    private BitDist KDist[] = new WordDist[K.length];
    private BitDist digest[] = new BitDist[8];
    private BitDist data[];

    public ProbSHA256()
    {
        initDigest();
        initK();
    }

    public void initDigest()
    {
        //Initializing everything as WordDist ensures size is correct from start
        digest[0] = new WordDist(1779033703L);
        digest[1] = new WordDist(3144134277L);
        digest[2] = new WordDist(1013904242L);
        digest[3] = new WordDist(2773480762L);
        digest[4] = new WordDist(1359893119L);
        digest[5] = new WordDist(2600822924L);
        digest[6] = new WordDist(528734635L);
        digest[7] = new WordDist(1541459225L);
    }

    public void initK()
    {
        for(int i=0; i<K.length; i++)
        {
            KDist[i] = new WordDist(K[i]);
        }
    }

    public void setData(WordDist input[])
    {
        System.out.println("Input " + input.length);
        if(input.length == 0)
            input = new WordDist[0]; //Useless, yes I know
        //Pad the data
        data = doDataPad(input);
    }

    //TODO Too many raw numbers, needs more constants, MOAR CAPS
    private BitDist[] doDataPad(WordDist input[])
    {
        ArrayList<BitDist> ret = new ArrayList<BitDist>();
        for(int i=0; i<input.length; i++)
        {
            ret.add(input[i]);
        }

        //Each element of input is 4 bytes
        long l = input.length*4*8; //length is measured in bits
        int fill = (int) (448 - ((l+1) % 512)); //Because data.length mod 32 = 0 , always gonna have fill
        if(fill < 0)
        {
            fill = 448 - fill;
        }

        System.out.println("Data pad "+ fill);

        if(fill != 0)
        {
            //Need at least one WordDist, 1 falls on new byte
            Double[] tempArr = new Double[32];
            Arrays.fill(tempArr, 0d);
            tempArr[31] = 1d; // Account for 1, needs to be in highest bit
            WordDist tempBit = new WordDist(tempArr);
            fill -= 31; //Should be multiple of 32 now
            ret.add(tempBit);
            tempArr[31] = 0d; //Set it back
            while(fill > 0)
            {
                tempBit = new WordDist(tempArr);
                ret.add(tempBit);
                fill -= 32;
            }
        }

        ret.add(new WordDist(l >> 32)); //Upper half
        ret.add(new WordDist(l & 0xFFFFFFFF)); //Lower half

        // we are replacing data here with a new padded version
        return ret.toArray(new BitDist[0]);
    }


    /*
     * method called to get the SHA1 digest of the input
     */
    //TODO The constants, they're back
    public BitDist[] getDigest()
    {
        BitDist roundBlock[];
        if (data.length > 16)
        {
            //More than 1 round
            int n = data.length / 64;
            System.out.println("Rounds: " + n + ", Data: " + data.length);
            for (int i = 0; i < n; i++)
            {
                roundBlock = Arrays.copyOfRange(data, i*16, (i+1)*16);
                transform(roundBlock);
            }
        }
        else
        {
            //Just one round
            System.out.println("Rounds: " + 1 + ", Data: " + data.length);
            transform(data);
        }
        return digest;
    }

    /*
     * this is the method that actually performs the digest and returns the
     * result
     */
    private void transform(BitDist block[])
    {
        System.out.println("Round block");
        for(BitDist r: block)
        {
            System.out.println(r.size() + " " + r);
        }

        // first, break into blocks and process one by one
        BitDist A = digest[0];
        BitDist B = digest[1];
        BitDist C = digest[2];
        BitDist D = digest[3];
        BitDist E = digest[4];
        BitDist F = digest[5];
        BitDist G = digest[6];
        BitDist H = digest[7];

        // doing the message schedule
        System.out.println("Start W ");
        BitDist W[] = new BitDist[64];
        for (int i = 0; i < 16; i++)
        {
            W[i] = block[i];
            // System.out.println("W: " + Integer.toHexString(W[i]) + "\n");
        }
        for (int i = 16; i < 64; i++)
        {
            W[i] = sig1(W[i - 2]).xor(W[i - 7]).xor(sig0(W[i - 15])).xor(W[i - 16]);
        }
        System.out.println("W done");

        //Do Rounds Here
        for(int t = 0; t < 64; t++)
        {
            System.out.println("Compression Round " + t);
            System.out.println(A);
            System.out.println(B);
            System.out.println(C);
            System.out.println(D);
            System.out.println(E);
            System.out.println(F);
            System.out.println(G);
            System.out.println(H);

            BitDist T1 = H.xor(SIG1(E)).xor(Ch(E, F, G)).xor(KDist[t]).xor(W[t]);
            BitDist T2 = SIG0(A).xor(Maj(A, B, C));
            H = G;
            G = F;
            F = E;
            E = D.xor(T1);
            D = C;
            C = B;
            B = A;
            A = T1.xor(T2);
        }

        digest[0] = digest[0].xor(A);
        digest[1] = digest[1].xor(B);
        digest[2] = digest[2].xor(C);
        digest[3] = digest[3].xor(D);
        digest[4] = digest[4].xor(E);
        digest[5] = digest[5].xor(F);
        digest[6] = digest[6].xor(G);
        digest[7] = digest[7].xor(H);
    }

    public static void main(String[] args)
    {
        ProbSHA256 prob = new ProbSHA256();
        //This is where we get to tweak values
        //{0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d},
//        Double[][] oldData = new Double[][]{
//                {0.2d,0.5d,0.2d,0.5d,0.2d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d},
//                {0.5d,0.2d,0.5d,0.2d,0.5d,0.2d,0.5d,0.5d,0.5d,0.2d,0.5d,0.5d,0.7d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d},
//                {0.5d,0.5d,0.2d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d},
//                {0.5d,0.5d,0.5d,0.2d,0.7d,0.5d,0.5d,0.7d,0.5d,0.5d,0.5d,0.1d,0.5d,0.5d,0.1d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d},
//                {0.5d,0.5d,0.5d,0.5d,0.2d,0.7d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d},
//                {0.5d,0.3d,0.5d,0.5d,0.5d,0.2d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d},
//                {0.5d,0.5d,0.1d,0.5d,0.5d,0.5d,0.2d,0.5d,0.5d,0.5d,0.1d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d},
//                {0.5d,0.5d,0.3d,0.5d,0.5d,0.5d,0.5d,0.2d,0.5d,0.3d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d,0.5d}
//        };

        Double[][] oldData = new Double[][] {
                randomFill(new Double[32]),
                randomFill(new Double[32]),
                randomFill(new Double[32]),
                randomFill(new Double[32]),
                randomFill(new Double[32]),
                randomFill(new Double[32]),
                randomFill(new Double[32]),
                randomFill(new Double[32])
        };

        WordDist[] bitData = new WordDist[]{
                new WordDist(oldData[0]), new WordDist(oldData[1]), new WordDist(oldData[2]), new WordDist(oldData[3]),
                new WordDist(oldData[4]), new WordDist(oldData[5]), new WordDist(oldData[6]), new WordDist(oldData[7])
        };
        prob.setData(bitData);
        BitDist[] ret = prob.getDigest();
        System.out.println("Output");
        for(BitDist r: ret)
        {
            System.out.println(r.size() + " " + r);
        }
    }

    public static Double[] randomFill(Double[] arr)
    {
        Random r = new Random();
        for(int i=0; i<arr.length; i++)
            arr[i] = r.nextDouble();
        return arr;
    }
}