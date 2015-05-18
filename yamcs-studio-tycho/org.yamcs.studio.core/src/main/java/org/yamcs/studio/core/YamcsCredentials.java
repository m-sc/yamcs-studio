package org.yamcs.studio.core;

import java.util.Arrays;

public class YamcsCredentials {
	 /**
     * The username, password
     */
    private String username;
    private char[] password;


    /**
     * Constructors
     *
     * @param username
     * @param password
     */
    public YamcsCredentials(final String username, final char[] password) {

        this.username = username;
        this.password = password;
    }

    public YamcsCredentials(final String username, final String password) {
        this(username, password != null ? password.toCharArray() : null);
    }

    /**
     * getter/setter
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public String getPasswordS() {
        if(password == null) return null;
        return new String(password);
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password != null ? password.toCharArray() : null;
    }


    @Override
    public String toString() {
        String usernamepassword = "";
        usernamepassword += (username != null ? username : "null");
        usernamepassword += "/";
        usernamepassword += (password != null ? "*****" : "null");
        return usernamepassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YamcsCredentials that = (YamcsCredentials) o;

        if (!Arrays.equals(password, that.password)) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? Arrays.hashCode(password) : 0);
        return result;
    }
}