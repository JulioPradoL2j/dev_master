package net.sf.l2j.tools.gui.sql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import net.sf.l2j.tools.mariadb.DatabaseFactory;

public final class SqlFileExecutor
{
    private SqlFileExecutor()
    {
    }

    public static void execute(File file) throws Exception
    {
        String sql = readAll(file);
        List<String> statements = SqlSplitter.splitStatements(sql);

        try (Connection con = DatabaseFactory.getConnection();
             Statement st = con.createStatement())
        {
            for (String s : statements)
            {
                String t = s.trim();
                if (t.isEmpty())
                    continue;
                st.execute(t);
            }
        }
    }

    private static String readAll(File f) throws Exception
    {
        StringBuilder sb = new StringBuilder((int) Math.min(Integer.MAX_VALUE, Math.max(1024, f.length())));
        try (BufferedReader br = new BufferedReader(new FileReader(f)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String t = line.trim();
                if (t.startsWith("--") || t.startsWith("#"))
                    continue;

                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }
}
