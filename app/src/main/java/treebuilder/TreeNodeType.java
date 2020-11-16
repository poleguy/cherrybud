/*
 * Created on Jul 12, 2004
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

/**
 * TreeNodeType
 *
 * @author Ian Kaplan, iank@bearcave.com
 *
 */
public class TreeNodeType extends TypeSafeEnum {
    
    public TreeNodeType( String typeName )
    {
        super( typeName, TreeNodeType.class );
    }

    String getString() { return this.toString(); }

    public static TreeNodeType ATTRIBUTE = new TreeNodeType("ATTRIBUTE");
    public static TreeNodeType COMMENT   = new TreeNodeType("COMMENT");
    public static TreeNodeType END_TAG   = new TreeNodeType("END_TAG");
    public static TreeNodeType TAG       = new TreeNodeType("TAG");
    public static TreeNodeType TEXT      = new TreeNodeType("TEXT");
}
