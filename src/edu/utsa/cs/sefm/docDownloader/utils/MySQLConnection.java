package edu.utsa.cs.sefm.docDownloader.utils;


import java.sql.*;

/**
 * Created by Rocky on 5/29/2015.
 */
public class MySQLConnection {
    String host;
    String user;
    String pass;

    public MySQLConnection(String host, String user, String pass) {
        this.host = host;
        this.user = user;
        this.pass = pass;
    }

    public static String escapeSQL(String string) {
        return string.replaceAll("'", "''");
    }

    /**
     * Execute a query on the existing connection.
     *
     * @param query SQL query.
     * @return ResultSet object in response from the database.
     * @throws SQLException
     */
    public ResultSet select(String query) throws SQLException {
        Connection con = DriverManager.getConnection(host, user, pass);
        Statement stmt = con.createStatement();
        ResultSet ret = stmt.executeQuery(query);
        stmt.close();
        con.close();
        return ret;
    }

    public int insert(String query) throws SQLException {
        Connection con = DriverManager.getConnection(host, user, pass);
        Statement stmt = con.createStatement();
        int ret = stmt.executeUpdate(query);
        stmt.close();
        con.close();
        return ret;
    }

    public int getID(String table, String key, String value) {
        try {
            Connection con = DriverManager.getConnection(host, user, pass);
            Statement stm = con.createStatement();
            ResultSet ret = stm.executeQuery("SELECT id FROM " + table + " WHERE " + key + " = '" + value + "'");
            while (ret.next())
                return ret.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
