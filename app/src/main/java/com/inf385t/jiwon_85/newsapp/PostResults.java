package com.inf385t.jiwon_85.newsapp;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by jiwon_85 on 11/5/15.
 */
public class PostResults {

    private ParseObject parseObject = null;

    public void setParseObject(ParseObject parseObject) { this.parseObject = parseObject; }

    public ParseObject getParseObject() { return parseObject; }

}
