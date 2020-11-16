/** \file
 * 
 * Sep 14, 2004
 *
 * Copyright Ian Kaplan 2004, Bear Products International
 *
 * You may use this code for any purpose, without restriction,
 * including in proprietary code for which you charge a fee.
 * In using this code you acknowledge that you understand its
 * function completely and accept all risk in its use.
 *
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
package treebuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

/**
 * Main
 * Sep 14, 2004
 * 
<p>
     Read an XML file, build an in-memory tree that mirrors the XML structure
     and then serialize the in-memory tree back to XML.
</p>
 * @author Ian Kaplan, www.bearcave.com, iank@bearcave.com
 */
public class Main {

    private void usage()
    {
        String name = getClass().getName();
        System.out.println("usage: " + name + "<XML fileName>");
        System.out.println("       Where the <XML fileName> is the file name of an XML document");
    }


    private void printXMLPullError( XmlPullParserException e, String xmlFileName )
    {
        int lineNum = e.getLineNumber();
        int columnNumber = e.getColumnNumber();
        String exceptionMsg = e.getMessage();
        String msg = xmlFileName + "(" + lineNum + ", " + columnNumber + "): " + exceptionMsg;
        System.out.println(msg);
    } // printXMLPullError


    private void buildTree( String[] args )
    {
        if (args.length == 1) {
            String xmlFileName = args[0];
            FileReader reader = null;
            try {
                reader = new FileReader( xmlFileName );
                TreeBuilder builder = new TreeBuilder();
                TreeNode root = builder.parseXML( reader );
                if (root != null) {
                    TreeToXML t = new TreeToXML( root );
                    String s = t.toString();
                    if (s != null) {
                        System.out.println("XML:");
                        System.out.println( s );
                    }
                    else {
                        System.out.println("serialized TreeNode tree is null");
                    }
                }
                else {
                    System.out.println("root is null");
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error opening file " + xmlFileName + " = " + e);
            } catch (IOException e1) {
                System.out.println(xmlFileName + ": IOException = " + e1 );
            } catch (XmlPullParserException e2) {
                printXMLPullError( e2, xmlFileName );
            }
        }
        else {
            usage();
        }

    } // buildTree


    public static void main(String[] args) 
    {
        Main t = new Main();
        t.buildTree( args );
    }
}
