package net.sf.l2j.tools.gui.sql;

import java.util.ArrayList;
import java.util.List;

public final class SqlSplitter
{
    private SqlSplitter()
    {
    }

    public static List<String> splitStatements(String sql)
    {
        List<String> out = new ArrayList<>();
        if (sql == null || sql.isEmpty())
            return out;

        StringBuilder cur = new StringBuilder();
        boolean inSingle = false;
        boolean inDouble = false;

        for (int i = 0; i < sql.length(); i++)
        {
            char c = sql.charAt(i);

            if (c == '\'' && !inDouble)
                inSingle = !inSingle;
            else if (c == '"' && !inSingle)
                inDouble = !inDouble;

            if (c == ';' && !inSingle && !inDouble)
            {
                out.add(cur.toString());
                cur.setLength(0);
            }
            else
            {
                cur.append(c);
            }
        }

        if (cur.length() > 0)
            out.add(cur.toString());

        return out;
    }
}
