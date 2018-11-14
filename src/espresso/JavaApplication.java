/** 
 * Copyright (c) 2018, BertLisser, Centrum Wiskunde & Informatica (CWI) 
 * All rights reserved. 
 *  
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 *  
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 *  
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */ 
package espresso;

import java.io.PrintWriter;

import netscape.javascript.JSObject;

    public class JavaApplication {
        final PrintWriter out;
            
            public JavaApplication(PrintWriter out) {
                this.out = out;
            }
         
            
            public void sendClick(String id) {
                out.println(id+":click");
            }
            
            public void sendTick(String id) {
                out.println(id+":tick");
            }
            
            public void sendChange(String id) {
                out.println(id+":change");
            }
            
            public void sendMessage(JSObject v) {
               String s = (String) v.getMember("value");
               String id = (String) v.getMember("id");
               //if(s.length()==4) {
               if (s.trim().length()!=s.length()) {
                   v.setMember("disabled", true);    
                   out.println(id+":input:"+s.trim());
               }
            }
            
            public void sendInput(JSObject v) {
                String s = (String) v.getMember("value");
                String id = (String) v.getMember("id"); 
                if (s!=null)
                    out.println(id+":change:"+s);
                else
                    out.println(id+":input");  
             }
        }

