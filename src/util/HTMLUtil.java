package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class HTMLUtil
{
    public static URL getBlockJSON(int blockNum)
    {
        try
        {
            URL url = new URL("http://blockexplorer.com/b/" + blockNum);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            int index;
            while((index = (line = br.readLine()).indexOf("rawblock")) == -1)
            {
            }
            line = line.substring(index-1,index+73);
            return new URL("http://blockexplorer.com" + line);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args)
    {
        System.out.println(HTMLUtil.getBlockJSON(125552));
    }
}
