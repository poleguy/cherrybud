package treebuilder;

/** \file
 * 
 * Sep 13, 2004
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

/**
 * TreeToString
 * 
   A class to support representation of the TreeNode tree as a String.
   Note that there is a similar class, TreeToXML which will serialize
   the in-memory tree back to XML.
<p>
   For those of you who speak the "gang of four" Design Patterns languauge,
   this class follows a "visitor" pattern.
</p>
 */
public class TreeToString {
    private final char startChar = '<';
    private final char endChar = '>';
    private TreeNode mRoot;
    private StringBuffer mBuf = null;
    private final int INDENT = 2;
    private int mIndentLevel;


    private void indent()
    {
        for (int i = 0; i < mIndentLevel; i++) {
            mBuf.append(' ');
        }
    } // indent

    private void openAngle()
    {
        if (mIndentLevel > 0) {
            mBuf.append('\n');
        }
        indent();
        mBuf.append( startChar );
        mIndentLevel += INDENT;
    } // openAngle


    private void closeAngle( boolean first )
    {
        assert mIndentLevel > 0;

        mIndentLevel -= INDENT;
        if (! first ) {
            mBuf.append('\n');
            indent();
        }
        mBuf.append( endChar );
        mBuf.append(' ');
    } // closeAngle


    private void nodeToString( TreeNode node )
    {
        TreeNodeType ty = node.getType();
        String nodeStr = node.toString();
        if (ty == TreeNodeType.TEXT) {
            nodeStr = nodeStr.trim();
        }
        else if (ty == TreeNodeType.COMMENT) {
            nodeStr = "\n<--" + nodeStr + "-->";
        }
        if (nodeStr.length() > 0) {
            // String tyName = ty.toString();
            // mBuf.append( tyName );
            // mBuf.append(':');
            mBuf.append( nodeStr );
            mBuf.append(' ');
        }
    } // nodeToString



    private void leavesToString( TreeNode root )
    {
        if (root != null) {
            for (TreeNode n = root.getChild(); n != null; n = n.getSibling()) {
                if (! n.isLeaf()) {
                    rootToString( n );
                }
                else {
                    nodeToString( n );
                }
            }
        }
    } // leavesToString


    private void rootToString( TreeNode root )
    {
        boolean first = true;

        if (root != null) {
            openAngle();
            nodeToString(root );
            if (! root.isLeaf()) {
                // foreach child of root ...
                for (TreeNode n = root.getChild(); n != null; n = n.getSibling()) {
                    if (! n.isLeaf()) {
                        first = false;
                    }
                } // for
                leavesToString( root );
            } // if root is not a leaf
            closeAngle( first );
        }
    }  // rootToString

    

    public TreeToString( TreeNode root ) 
    { 
        mRoot = root; 
    }


    public String toString()
    {
        mBuf = new StringBuffer();
        for (TreeNode t = mRoot; t != null; t = t.getSibling()) {
            mIndentLevel = 0;
            rootToString( t );
            mBuf.append('\n');
        }
        return mBuf.toString();
    } // toString

}
