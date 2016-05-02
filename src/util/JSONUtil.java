package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("rawtypes")
public class JSONUtil
{
    public static HashMap getJSONBlock(URL url)
    {
        try
        {
            return getJSONBlock(url.openStream(), false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static HashMap getJSONBlock(URL url, boolean debug)
    {
        try
        {
            return getJSONBlock(url.openStream(), debug);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap getJSONBlock(File f)
    {
        try
        {
            return getJSONBlock(new FileInputStream(f), false);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static HashMap getJSONBlock(File f, boolean debug)
    {
        try
        {
            return getJSONBlock(new FileInputStream(f), debug);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap getJSONBlock(InputStream in)
    {
        return getJSONBlock(in, false);
    }

    private static HashMap getJSONBlock(InputStream in, boolean debug)
    {
        JSONParser parser = new JSONParser();
        ContainerFactory factory = new ContainerFactory() {

            @Override
            public List creatArrayContainer()
            {
                return new ArrayList();
            }

            @Override
            public Map createObjectContainer()
            {
                return new HashMap();
            }

        };
        try
        {
            HashMap json = (HashMap) parser.parse(new InputStreamReader(in), factory);
            if(debug)
            {
                Iterator iter = json.entrySet().iterator();
                while(iter.hasNext())
                {
                    Map.Entry entry = (Map.Entry)iter.next();
                    System.out.println(entry.getKey() + "=>" + entry.getValue());
                    System.out.println(entry.getKey().getClass() + "=>" + entry.getValue().getClass());
                }
            }
            return json;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean writeJSONToFile(HashMap map, File out)
    {
        try
        {
            FileWriter writer = new FileWriter(out);
            JSONObject.writeJSONString(map, writer);
            writer.close();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean writeJSONToFile(String json, File out)
    {
        try
        {
            FileWriter writer = new FileWriter(out);
            writer.append(json);
            writer.close();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) throws FileNotFoundException
    {
        File out = new File("data/test.out");
        if(JSONUtil.writeJSONToFile(JSONUtil.getJSONBlock(HTMLUtil.getBlockJSON(125552), true), out))
            JSONUtil.getJSONBlock(out, true);
    }
}
