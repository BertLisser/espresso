/**
 * Copyright (c) 2018, BertLisser, Centrum Wiskunde & Informatica (CWI) All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided with
 * the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package espresso;

import java.io.PrintWriter;
import java.util.HashMap;

// import netscape.javascript.JSObject;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

//RECT (new Eval() {public String eval(WebEngine we, String id) {return rect(we, id);}})    

class DomUpdate {
    
    public interface Eval {
        public String eval(WebEngine we, String id);
    }
    
    enum Op {
         H1((we, id)    -> htmlEl("h1",we, id))
         ,H2((we, id)    -> htmlEl("h2",we, id))
         ,H3((we, id)    -> htmlEl("h3",we, id))
         ,H4((we, id)    -> htmlEl("h4",we, id))
         ,DIV ((we, id)    -> htmlEl("div",we, id))
         ,P ((we, id)    -> htmlEl("p",we, id))
         ,SPAN ((we, id)    -> htmlEl("span",we, id))
         ,UL((we, id)          -> htmlEl("ul", we, id))
         ,OL((we, id)          -> htmlEl("ol", we, id))
         ,LI((we, id)          -> htmlEl("li", we, id))
         ,TABLE((we, id)          -> htmlEl("table", we, id))
         ,TR((we, id)          -> htmlEl("tr", we, id))
         ,TD((we, id)          -> htmlEl("td", we, id))
        ,BUTTON ((we, id) -> button(we, id))
        ,TEXTAREA ((we, id) -> textarea(we, id))
        ,INPUT ((we, id) -> input(we, id))
        ,REMOVECHILDS ((we, id) -> removechilds(we, id))
        ,SVG ((we, id)    -> svgEl("svg", we, id))
        ,RECT ((we, id)   -> svgEl("rect", we, id))
        ,POLYGON ((we, id)   -> svgEl("polygon", we, id))
        ,LINE ((we, id)   -> svgEl("line", we, id))
        ,CIRCLE ((we, id)   -> svgEl("circle", we, id))
        ,ELLIPSE ((we, id)   -> svgEl("ellipse", we, id))
        ,G((we, id)         -> svgEl("g", we, id))
        ,PATH((we, id)     -> svgEl("path", we, id))
        ,TEXT((we, id)     -> svgEl("text", we, id))
        ,DEFS((we, id)         -> svgEl("defs", we, id))
        ,MARKER((we, id)     -> svgEl("marker", we, id))      
        ;   
        final private Eval val;
        Op(Eval ev) {val=ev;}
        public String build(WebEngine we, String id) {return val.eval(we, id);}
    }
    
    final private WebEngine webEngine;
    final private String id;
    final PrintWriter out;
    static HashMap<WebEngine, Integer> counter = new HashMap<WebEngine, Integer>();

    static String newId(WebEngine we) {
        if (counter.get(we) == null)
            counter.put(we, 0);
        int c = counter.get(we);
        String r = "node_" + c;
        counter.put(we, c + 1);
        return r;
    }

    public String getId() {
        return this.id;
    }

    DomUpdate(String id, WebEngine webEngine, PrintWriter out) {
        this.webEngine = webEngine;
        this.out = out;
        this.id = id;
    }

    DomUpdate(WebEngine webEngine, PrintWriter out) {
        this.webEngine = webEngine;
        this.id = newId(webEngine);
        this.out = out;
    }

    public void addSendMessageHandler(PrintWriter out) {
        JavaApplication app = new JavaApplication(out);
        JSObject window = (JSObject) webEngine.executeScript("window");
        window.setMember("app", app);
    }

    public void innerHTML(String input) {
        final String result =
            "document.getElementById(\"" + this.id + "\").innerHTML=\"" + input.replaceAll("\"", "\\\\\"") + "\";";
        webEngine.executeScript(result);
    }

    public void setFocus() {
        final String result = "document.getElementById(\"" + this.id + "\").focus();";
        webEngine.executeScript(result);
    }

    public DomUpdate select(String id) {
        DomUpdate r = new DomUpdate(id, webEngine, out);
        return r;
    }
    
    public static String htmlEl(String tag, WebEngine we, String id) {
        String newId = newId(we);
        final String result =
        "var el=document.createElement(\""+tag+"\");\n" + "el.id =\"" + newId + "\";\n" + 
        "document.getElementById(\"" + id + "\").appendChild(el);";
        we.executeScript(result);
        return newId;
    }
    
    public static String svgEl(String tag, WebEngine we, String id) {
        String newId = newId(we);
        final String result =
            "var NS=\"http://www.w3.org/2000/svg\";\n" 
                + "var el=document.createElementNS(NS, \""+tag+"\");\n"
                + "el.id =\"" + newId + "\";\n" 
                + "document.getElementById(\"" + id + "\").appendChild(el);";
        we.executeScript(result);
        return newId;
    }
    
    public static String use(WebEngine we, String id, String ref) {
        String newId = newId(we);
        final String result =
            "var NS=\"http://www.w3.org/2000/svg\";\n" 
            +"var link=\"http://www.w3.org/1999/xlink\";\n"
                + "var el=document.createElementNS(NS, \""+"use"+"\");\n"
                + "el.id =\"" + newId + "\";\n" 
                + "el.setAttributeNS(link, \"href\",\"#"+ref+"\");\n"
                + "document.getElementById(\"" + id + "\").appendChild(el);";
        we.executeScript(result);
        return newId;
    }
    
    public static String div(WebEngine we, String id) {
        String newId = newId(we);
        final String result =
        "var row=document.createElement(\"div\");\n" + "row.id =\"" + newId + "\";\n" + 
        "document.getElementById(\"" + id + "\").appendChild(row);";
        we.executeScript(result);
        return newId;
    }
    
    public static String add(WebEngine we, String id, String appendId) {
        final String result =
        "var row=document.getElementById(\""+appendId+"\");\n" + 
        "document.getElementById(\"" + id + "\").appendChild(row);";
        we.executeScript(result);
        return id;
    }
    
    public static String div(WebEngine we) {
        String newId = newId(we);
        final String result =
        "document.createElement(\"div\");\n" + "row.id =\"" + newId + "\";\n" ;
        we.executeScript(result);
        return newId;
    }
    
    public static String removechilds(WebEngine we, String id) {
        final String result = "var myNode = document.getElementById(\""+id+"\");"+
            "while (myNode.firstChild) {\n" +
                "myNode.removeChild(myNode.firstChild);" +
            "}";
        we.executeScript(result);
        return id;
    }
    
    public static String svg(WebEngine we, String id) {
        String newId = newId(we);
        final String result =
            "var NS=\"http://www.w3.org/2000/svg\";\n"
                + "var svg=document.createElementNS(NS, \"svg\");\n" + "svg.id =\"" + newId + "\";\n" 
                + "document.getElementById(\"" + id + "\").appendChild(svg);";
        we.executeScript(result);
        return newId;
    }

    public static String rect(WebEngine we, String id) {
        String newId = newId(we);
        final String result =
            "var NS=\"http://www.w3.org/2000/svg\";\n" 
                + "var rect=document.createElementNS(NS, \"rect\");\n"
                + "rect.id =\"" + newId + "\";\n" 
                + "document.getElementById(\"" + id + "\").appendChild(rect);";
        we.executeScript(result);
        return newId;
    }

    public void attribute(String attr, String val) {
        final String result = "var found = document.getElementById(\"" + this.id + "\");\n" 
                            + "found.setAttribute(\""+ attr + "\",\"" + val + "\");\n";
        webEngine.executeScript(result);
    }
    
    public String attribute(String attr) {
        final String result = "var found = document.getElementById(\"" + this.id + "\");\n" 
                            //+ ((attr.equals("value"))
                            //? "var result = found.value;\n"
                            //: 
                            +  ("var result = found.getAttribute(\""+ attr + "\");\n")
                            + "result";
        String val = (String) webEngine.executeScript(result);
        return val;
    }
    
    public String property(String attr) {
        final String result = "var found = document.getElementById(\"" + this.id + "\");\n" 
                            + "var result = found."+attr+";\n"
                            + "\"\"+result";
        String val = (String) webEngine.executeScript(result);
        return val;
    }
    
    public String getBBox() {
        final String result = 
                            "var result = getBBox(\""+this.id+"\");\n"
                            + "\"\"+result";
        String val = (String) webEngine.executeScript(result);
        return val;
    }
    
    public String getBoundingClientRect() {
        final String result = 
                            "var result = getBoundingClientRect(\""+this.id+"\");\n"
                            + "\"\"+result";
        String val = (String) webEngine.executeScript(result);
        return val;
    }
    
    public String style(String attr) {
        final String result = "getStyle(\""+this.id+"\",\""+attr+"\")";
        String val = (String) webEngine.executeScript(result);
        return val;
    }
    
    public void property(String attr, String val) {
        final String result = "var found = document.getElementById(\"" + this.id + "\");\n" 
                             +"found."+attr+"="+val+";\n";
        webEngine.executeScript(result);
    }

    static public String textarea(WebEngine we, String id) {
        String newId = newId(we);
        final String result = "var input=document.createElement(\"textarea\");\n" + "input.id =\"" + newId + "\";\n"
            + "input.rows=\"1\";\n" + "input.addEventListener(\"input\",function(){app.sendMessage(this);});\n"
            + "document.getElementById(\"" + id
            + "\").appendChild(input);";
        we.executeScript(result);
        return newId;
    }
    
    static public String input(WebEngine we, String id) {
        String newId = newId(we);
        final String result = "var input=document.createElement(\"input\");\n" + "input.id =\"" + newId + "\";\n"
            + "input.addEventListener(\"change\",function(){app.sendChange(\""+newId+"\");});\n"
            + "document.getElementById(\"" + id
            + "\").appendChild(input);";
        we.executeScript(result);
        return newId;
    }
    
    static public String button(WebEngine we, String id) {
        String newId = newId(we);
        final String result = "var input=document.createElement(\"button\");\n" 
            + "input.id =\"" + newId + "\";\n"
            + "input.addEventListener(\"click\",function(){app.sendClick(\""+newId+"\");});\n" 
            + "document.getElementById(\"" + id
            + "\").appendChild(input);";
        we.executeScript(result);
        return newId;
    }
    
    static public String setInterval(WebEngine we, String id, String interval) {
        return setInterval(we, id, Integer.parseInt(interval));
    }
    
    static public String setInterval(WebEngine we, String id, int interval) {
        final String result = 
          id + " = setInterval(function(){app.sendTick(\""+id+"\");},"+interval+");";
        we.executeScript(result);
        return id;
    }
    
    static public String clearInterval(WebEngine we, String id) {
        final String result = "clearInterval("+id+");";
        we.executeScript(result);
        return id;
    }
    }

