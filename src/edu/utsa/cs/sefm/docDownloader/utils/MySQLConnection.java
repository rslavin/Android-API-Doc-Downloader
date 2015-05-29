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
        ret.close();
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
}
