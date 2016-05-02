package bitcoin;

public class Block
{
    private String hash, merkRoot;
    private long time, nonce, bits, numTx;

    public Block()
    {
    }

    public void setHash(String h)
    {
        hash = h;
    }

    public String getHash()
    {
        return hash;
    }

    public void setMerkRoot(String m)
    {
        merkRoot = m;
    }

    public String getMerkRoot()
    {
        return merkRoot;
    }

    public void setTime(Long t)
    {
        time = t;
    }

    public Long getTime()
    {
        return time;
    }

    public void setNonce(Long n)
    {
        nonce = n;
    }

    public Long getNonce()
    {
        return nonce;
    }

    public void setBits(Long b)
    {
        bits = b;
    }

    public Long getBits()
    {
        return bits;
    }

    public void setNumTx(Long n)
    {
        numTx = n;
    }

    public Long getNumTx()
    {
        return numTx;
    }
}
