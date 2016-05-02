package probabilistic;

public class ProbabilityBit
{
    //Define our probabilities
    float[] prob;

    public ProbabilityBit()
    {
        this(.5f);
    }

    public ProbabilityBit(float zeroProb)
    {
        prob = new float[2];
        prob[0] = zeroProb;
        prob[1] = 1-zeroProb;
    }
}
