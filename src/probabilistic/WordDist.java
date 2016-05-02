package probabilistic;

import java.util.Arrays;

public class WordDist extends BitDist
{
    private final static int WORD_SIZE = 32;
    public WordDist()
    {
        super(WORD_SIZE);
    }

    public WordDist(long num)
    {
        super(num, WORD_SIZE);
    }

    public WordDist(Double... tempArr)
    {
        super(Arrays.copyOf(tempArr, WORD_SIZE));
    }
}
