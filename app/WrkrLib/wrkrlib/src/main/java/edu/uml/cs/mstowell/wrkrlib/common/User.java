package edu.uml.cs.mstowell.wrkrlib.common;

import org.json.JSONArray;

/**
 * Web server User object
 */
public class User {

    public int id;
    public String email;
    public String name;
    public int exercises;
    public JSONArray timestamps;

    public User() { }
}
