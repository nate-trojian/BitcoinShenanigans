package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ExpansionGenerator
{
    private HashMap<String, MultiSet<Integer>> varRoundUsage;
    private LinkedList<ExpandRule> rules;
    private HashMap<String, ArrayList<Integer>> constants;

    private final Comparator<Integer> descend = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2)
        {
            return o2.compareTo(o1);
        }
    };

    public ExpansionGenerator(int numVar, int numRules, String... text)
    {
        this(Arrays.copyOfRange(text, 0, numVar), Arrays.copyOfRange(text, numVar, text.length));
        assert(numVar+2*numRules == text.length);
    }

    public ExpansionGenerator(String[] varsArr, String[] rulesArr)
    {
        varRoundUsage = new HashMap<String, MultiSet<Integer>>();
        for(int i=0; i<varsArr.length; i++)
        {
            varRoundUsage.put(varsArr[i], new MultiSet<Integer>());
        }

        rules = new LinkedList<ExpandRule>();
        ExpandRule rule;
        String comp = "";
        for(int i=0; i<rulesArr.length; i+=2)
        {
            rule = new ExpandRule(rulesArr[i]);
            comp = rulesArr[i+1].replace(" ", "");
            if(comp.equals(""))
            {
                rule.doWhile(null);
            }
            else if(comp.contains(">"))
            {
                if(comp.contains("="))
                {
                    String[] components = comp.split(">=");
                    if(GenUtil.isNumeric(components[1]))
                    {
                        rule.doWhile(new Comparision(components[0], CompareType.GREATER_THAN_EQUAL, Integer.valueOf(components[1])));
                    }
                    else
                    {
                        rule.doWhile(new Comparision(components[0], CompareType.GREATER_THAN_EQUAL, components[1]));
                    }
                }
                else
                {
                    String[] components = comp.split(">");
                    if(GenUtil.isNumeric(components[1]))
                    {
                        rule.doWhile(new Comparision(components[0], CompareType.GREATER_THAN, Integer.valueOf(components[1])));
                    }
                    else
                    {
                        rule.doWhile(new Comparision(components[0], CompareType.GREATER_THAN, components[1]));
                    }
                }
            }
            else if(comp.contains("<"))
            {
                if(comp.contains("="))
                {
                    String[] components = comp.split("<=");
                    if(GenUtil.isNumeric(components[1]))
                    {
                        rule.doWhile(new Comparision(components[0], CompareType.LESS_THAN_EQUAL, Integer.valueOf(components[1])));
                    }
                    else
                    {
                        rule.doWhile(new Comparision(components[0], CompareType.LESS_THAN_EQUAL, components[1]));
                    }
                }
                else
                {
                    String[] components = comp.split("<");
                    if(GenUtil.isNumeric(components[1]))
                    {
                        rule.doWhile(new Comparision(components[0], CompareType.LESS_THAN, Integer.valueOf(components[1])));
                    }
                    else
                    {
                        rule.doWhile(new Comparision(components[0], CompareType.LESS_THAN, components[1]));
                    }
                }
            }
            else if(comp.contains("="))
            {
                String[] components = comp.split("=");
                if(GenUtil.isNumeric(components[1]))
                {
                    rule.doWhile(new Comparision(components[0], CompareType.EQUAL, Integer.valueOf(components[1])));
                }
                else
                {
                    rule.doWhile(new Comparision(components[0], CompareType.EQUAL, components[1]));
                }
            }
            else
            {
                //Something went wrong
                System.out.println("Shit");
            }
            rules.add(rule);
        }
    }

    public void defineConst(String... constant)
    {
        constants = new HashMap<String, ArrayList<Integer>>();
        ArrayList<Integer> list;
        for(String line: constant)
        {
            //Ex: h_0_, k_0-64_, ch_0,16,32_
            //Yes I could have done this better
            //No I don't care
            list = new ArrayList<Integer>();
            String[] parts = line.split("_");
            int num1, num2;
            if(parts[1].contains(","))
            {
                String[] parts2 = parts[1].split(",");
                for(String lineParts: parts2)
                {
                    int dashIndex = lineParts.indexOf('-');
                    if(dashIndex == -1)
                    {
                        num1 = Integer.parseInt(lineParts);
                        list.add(num1);
                    }
                    else
                    {
                        num1 = Integer.parseInt(lineParts.substring(0, dashIndex));
                        num2 = Integer.parseInt(lineParts.substring(dashIndex+1));
                        for(int i=num1; i<=num2; i++)
                        {
                            list.add(i);
                        }
                    }
                }
            }
            else
            {
                int dashIndex = parts[1].indexOf('-');
                if(dashIndex == -1)
                {
                    num1 = Integer.parseInt(parts[1]);
                    list.add(num1);
                }
                else
                {
                    num1 = Integer.parseInt(parts[1].substring(0, dashIndex));
                    num2 = Integer.parseInt(parts[1].substring(dashIndex+1));
                    for(int i=num1; i<=num2; i++)
                    {
                        list.add(i);
                    }
                }
            }
            constants.put(parts[0], list);
        }
    }

    public void seedVariable(String var, int... vals)
    {
        assert(varRoundUsage.containsKey(var));
        for(int val: vals)
        {
            varRoundUsage.get(var).add(val);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void seedFromJSON(HashMap json)
    {
        Iterator it = json.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry entry = (Map.Entry)it.next();
            ArrayList<HashMap<String,Long>> rounds = (ArrayList<HashMap<String,Long>>) entry.getValue();
            for(HashMap<String,Long> round: rounds)
            {
                int rNum = round.get("round").intValue();
                int num = round.get("numOcc").intValue();
                int[] tempArr = new int[num];
                Arrays.fill(tempArr, rNum);
                this.seedVariable((String)entry.getKey(), tempArr);
            }
        }
    }

    public void generate()
    {
         boolean done = false;
         int failed;
         while(!done)
         {
             failed = 0;
             for(ExpandRule rule: rules)
             {
                 if(!rule.applyRule())
                     failed++;
             }
             //System.out.println(failed + " " + rules.size());
             if(failed == rules.size())
                 done = true;

             getState();
             System.out.println("\n");
         }
    }

    public void getState()
    {
        getState(null, null);
    }

    public void getState(Comparator<String> c1, Comparator<Integer> c2)
    {
        String[] arrKey = varRoundUsage.keySet().toArray(new String[0]);
        if(c1 != null)
        {
            Arrays.sort(arrKey, c1);
        }
        String line = "";
        for(String key: arrKey)
        {
            line += key + ": ";
            Integer[] arrObj = varRoundUsage.get(key).toArray(new Integer[0]);
            if(c2 != null)
            {
                Arrays.sort(arrObj, c2);
            }
            for(Integer round: arrObj)
            {
                line += "(" + round + ", " + varRoundUsage.get(key).getMult(round) + "), ";
            }
            line = line.substring(0, line.length()-2);
            System.out.println(line);
            line = "";
        }
    }

    public String genJSONRes(Comparator<String> c1, Comparator<Integer> c2)
    {
        String[] arrKey = varRoundUsage.keySet().toArray(new String[0]);
        if(c1 != null)
        {
            Arrays.sort(arrKey, c1);
        }
        String ret = "{\n";
        String line = "";
        for(String key: arrKey)
        {
            line += "\"" + key + "\": [";
            Integer[] arrObj = varRoundUsage.get(key).toArray(new Integer[0]);
            if(c2 != null)
            {
                Arrays.sort(arrObj, c2);
            }
            for(Integer round: arrObj)
            {
                line += "{\"round\":" + round + ", \"numOcc\":" + varRoundUsage.get(key).getMult(round) + "}, ";
            }
            line = line.substring(0, line.length()-2);
            ret += line + "]";
            line = ",\n";
        }
        ret += "}";
        return ret;
    }

    public Comparator<Integer> getDescComp()
    {
        return descend;
    }

    class ExpandRule
    {
        // h_i_ = d_i-4_ + s1_i-4_ + ch_i-4_ + w_i-4_ + k_i-4_ + h_i-4_
        private String inputVar;
        private String roundVar;
        private HashMap<String, Integer> output;
        private Comparision compare;
        public ExpandRule(String rule)
        {
            output = new HashMap<String, Integer>();
            rule = rule.replace(" ", "");
            String[] equation = rule.split("=");
            String[] input = equation[0].split("_");
            inputVar = input[0];
            roundVar = input[1];
            String[] subSplit = equation[1].split("_");
            for(int i=0; i<subSplit.length; i+=2)
            {
                boolean negate = false;
                String var = subSplit[i];
                if(var.charAt(0) == '-')
                {
                    negate = true;
                    var = var.substring(1);
                }
                else if(var.charAt(0) == '+')
                {
                    var = var.substring(1);
                }
                String change = subSplit[i+1];
                //Add ability for function on roundVar here
                assert(change.startsWith(roundVar));
                output.put(var, (negate?-1*Integer.parseInt(change.substring(roundVar.length())): Integer.parseInt(change.substring(roundVar.length()))));
                //System.out.println(var + " " + output.get(var));
            }
        }

        public void doWhile(Comparision comp)
        {
            compare = comp;
        }

        public boolean applyRule()
        {
            return (varRoundUsage.get(inputVar).size()>0?applyRule(varRoundUsage.get(inputVar).get(0)):false);
        }

        public boolean applyRule(Integer roundVarVal)
        {
            boolean apply = true;
            if(compare == null)
            {
            }
            else if(compare.isVar1Round())
            {
                apply = compare.check(roundVarVal);
            }
            else
            {
                apply = compare.check();
            }

            if(apply)
            {
                varRoundUsage.get(inputVar).remove(roundVarVal);
                for(String key: output.keySet())
                {
                    GenUtil.insert(varRoundUsage.get(key),roundVarVal + output.get(key), descend);
                }
            }
            return apply;
        }

        public String getInputVar()
        {
            return inputVar;
        }

        public Comparision getCompare()
        {
            return compare;
        }
    }

    enum CompareType
    {
        GREATER_THAN(">"),
        GREATER_THAN_EQUAL(">="),
        EQUAL("="),
        LESS_THAN_EQUAL("<="),
        LESS_THAN("<");

        private String op;

        CompareType(String op)
        {
            this.op = op;
        }

        public String getOp()
        {
            return op;
        }
    }

    class Comparision
    {
        private String var1, var2;
        private CompareType comp;
        private boolean var1Round, var2Num;
        public Comparision(String var1, CompareType comp, int var2)
        {
            this.var1 = var1;
            this.comp = comp;
            this.var2 = String.valueOf(var2);
            this.var1Round = !varRoundUsage.containsKey(var1);
            this.var2Num = true;
        }
        public Comparision(String var1, CompareType comp, String var2)
        {
            assert(!GenUtil.isNumeric(var1));
            this.var1 = var1;
            this.comp = comp;
            this.var2 = var2;
            this.var1Round = !varRoundUsage.containsKey(var1);
            this.var2Num = false;
        }

        public boolean isVar1Round()
        {
            return var1Round;
        }

        public boolean isVar2Num()
        {
            return var2Num;
        }

        public boolean check()
        {
            if(var1Round) return false;
            if(var2Num)
                return check(varRoundUsage.get(var1).get(0), Integer.valueOf(var2));
            else
                return check(varRoundUsage.get(var1).get(0), varRoundUsage.get(var2).get(0));
        }

        public boolean check(int var1Val)
        {
            if(varRoundUsage.containsKey(var2))
            {
                return check(var1Val, varRoundUsage.get(var2).get(0));
            }
            return check(var1Val, Integer.valueOf(var2));
        }

        public boolean check(int var1Val, int var2Val)
        {
            //System.out.println(var1Val + " " + var2Val);
            int i = var1Val - var2Val;
            switch(comp)
            {
                case GREATER_THAN:
                    return i > 0;
                case GREATER_THAN_EQUAL:
                    return i >= 0;
                case EQUAL:
                    return i == 0;
                case LESS_THAN_EQUAL:
                    return i <= 0;
                case LESS_THAN:
                    return i < 0;
            }
            return false;
        }
    }

    class MultiSet<T> extends ArrayList<T>
    {
        private static final long serialVersionUID = -196195481775965545L;
        //Don't need full functionality
        HashMap<T, Integer> set;
        public MultiSet()
        {
            set = new HashMap<T, Integer>();
        }

        public boolean add(T obj)
        {
            if(super.contains(obj))
            {
                set.put(obj, set.get(obj)+1);
                return true;
            }
            else
            {
                boolean ret = super.add(obj);
                if(ret)
                    set.put(obj, 1);
                return ret;
            }
        }

        public void add(int index, T obj)
        {
            if(super.contains(obj))
            {
                set.put(obj, set.get(obj)+1);
            }
            else
            {
                super.add(index, obj);
                if(super.contains(obj))
                    set.put(obj, 1);
            }
        }

        public Integer getMult(T obj)
        {
            return (set.containsKey(obj)?set.get(obj):-1);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ExpansionGenerator fromJSON(HashMap json) throws Exception
    {
        String[] varsArr = null, rulesArr = null;
        Object varsObj = json.get("variables");
        if(varsObj == null || !(varsObj instanceof ArrayList))
        {
            throw new Exception("No variables");
        }
        else
        {
            ArrayList<String> varsList = (ArrayList<String>)varsObj;
            if(varsList.size()==0)
                throw new Exception("No variables");
            varsArr = varsList.toArray(new String[0]);
        }

        Object rulesObj = json.get("rules");
        if(rulesObj == null || !(rulesObj instanceof ArrayList))
        {
            throw new Exception("No rules");
        }
        else
        {
            ArrayList<HashMap<String, String>> rulesList = (ArrayList<HashMap<String,String>>)rulesObj;
            if(rulesList.size()==0)
                throw new Exception("No rules");
            rulesArr = new String[rulesList.size()*2];
            for(int i=0; i<rulesList.size(); i++)
            {
                rulesArr[2*i] = rulesList.get(i).get("rule");
                rulesArr[2*i+1] = rulesList.get(i).get("cond");
            }
        }
        return new ExpansionGenerator(varsArr, rulesArr);
    }

    public static void main(String[] args) throws Exception
    {
        String path = "data/expand";
        File in = new File(path+".in");
        File out = new File(path+".out");
        //TODO Test if full rules matches condensed rules
        //TODO Need to fix expansion first - awk
        /*ExpansionGenerator eg = new ExpansionGenerator(14, 2, "a", "b", "c", "d", "e", "f", "g", "h", "s0", "s1", "ch", "maj", "w", "k",
                "h_i_ = d_i-4_ + s1_i-4_ + ch_i-4_ + w_i-4_ + k_i-4_ + h_i-4_", "i >= 4",
                "d_i_ = s1_i-4_ + ch_i-4_ + w_i-4_ + k_i-4_ + h_i-4_ + s0_i-4_ + maj_i-4_", "i >= 4");*/
        /*ExpansionGenerator eg = new ExpansionGenerator(16, 10, "a", "b", "c", "d", "e", "f", "g", "h", "s0", "s1", "ch", "maj", "w", "k", "temp1", "temp2",
                "temp1_i_ = h_i-1_ + s1_i-1_ + ch_i-1_ + k_i-1_ + w_i-1_", "i>=1",
                "temp2_i_ = s0_i-1_ + maj_i-1_", "i>=1",
                "h_i_ = g_i-1_", "i>=1",
                "g_i_ = f_i-1_", "i>=1",
                "f_i_ = e_i-1_", "i>=1",
                "e_i_ = d_i-1_ + temp1_i-1_", "i>=1",
                "d_i_ = c_i-1_", "i>=1",
                "c_i_ = b_i-1_", "i>=1",
                "b_i_ = a_i-1_", "i>=1",
                "a_i_ = temp1_i-1_ + temp2_i-1_", "i>=1");*/
        ExpansionGenerator eg = ExpansionGenerator.fromJSON(JSONUtil.getJSONBlock(in));
        eg.seedVariable("a", 64);
        //eg.seedFromJSON(JSONUtil.getJSONBlock(new File("data/prevData.in")));
        eg.generate();
        eg.getState(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2)
            {
                if(o1.length() == o2.length())
                {
                    return o1.compareToIgnoreCase(o2);
                }
                return o1.length() - o2.length();
            }
        }, eg.descend);

        String result = eg.genJSONRes(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2)
            {
                if(o1.length() == o2.length())
                {
                    return o1.compareToIgnoreCase(o2);
                }
                return o1.length() - o2.length();
            }
        }, eg.descend);
        //JSONUtil.writeJSONToFile(result, out);
    }
}
