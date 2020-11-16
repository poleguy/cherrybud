package com.poleguy.cherrybud


import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.poleguy.cherrybud.niuedu.ListTree
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.xmlpull.v1.XmlPullParserException
import treebuilder.TreeBuilder
import java.io.FileReader
import java.io.IOException
import java.io.InputStream


class MainActivity : AppCompatActivity(),  HandlePathOzListener.SingleUri, PopupMenu.OnMenuItemClickListener {

    private lateinit var handlePathOz: HandlePathOz
    var path : String = "none"

    //保存数据的集合
    private val tree = ListTree()

    //从ListTreeAdapter派生的Adapter
    internal var adapter: ExampleListTreeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(toolbar)

        handlePathOz = HandlePathOz(this, this)
        // Example of a call to a native method
        //sample_text.text = stringFromJNI()

        // get reference to button
        val button = findViewById<Button>(R.id.button)

        // https://stackoverflow.com/questions/49697630/open-file-choose-in-android-app-using-kotlin
        button.setOnClickListener {

            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)



            sample_text.text = "blahblah"

        }

        sample_text.text = "blah"

        // ExampleListTreeAdapter

        //创建后台数据：一棵树
        //创建组们，是root node，所有parent为null
        val groupNode1 = tree.addNode(null, "特别关心", R.layout.contacts_group_item)
        val groupNode2 = tree.addNode(null, "我的好友", R.layout.contacts_group_item)
        val groupNode3 = tree.addNode(null, "朋友", R.layout.contacts_group_item)
        val groupNode4 = tree.addNode(null, "家人", R.layout.contacts_group_item)
        val groupNode5 = tree.addNode(null, "同学", R.layout.contacts_group_item)

        //第二层
        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        var contact = ExampleListTreeAdapter.ContactInfo(bitmap, "王二", "[在线]我是王二")
        val contactNode1 = tree.addNode(groupNode2, contact, R.layout.contacts_contact_item)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "王三", "[在线]我是王三")
        val contactNode2 = tree.addNode(groupNode5, contact, R.layout.contacts_contact_item)
        //再添加一个
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "王四", "[离线]我没有状态")
        tree.addNode(groupNode2, contact, R.layout.contacts_contact_item)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "王五", "[离线]我没有状态")
        tree.addNode(groupNode5, contact, R.layout.contacts_contact_item)

        //第三层
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "东邪", "[离线]出来还价")
        var n: ListTree.TreeNode = tree.addNode(contactNode1, contact, R.layout.contacts_contact_item)
        n.isShowExpandIcon = false
        //再添加一个
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "李圆圆", "[离线]昨天出门没出去")
        n = tree.addNode(contactNode1, contact, R.layout.contacts_contact_item)
        n.isShowExpandIcon = false

        adapter = ExampleListTreeAdapter(tree, this)
        listView.layoutManager = LinearLayoutManager(this)
        listView.setAdapter(adapter)

    }

    //On Completion (Sucess or Error)
    //If there is a cancellation or error.
    override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
        //Hide Progress

        //Now you can work with real path:
        Toast.makeText(this, "The real path is: ${pathOz.path} \n The type is: ${pathOz.type}", Toast.LENGTH_SHORT).show()
        path = pathOz.path
        xmlParse(path)
        //Handle any Exception (Optional)
        tr?.let {
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
    // https://www.raywenderlich.com/2705552-introduction-to-android-activities-with-kotlin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // https://stackoverflow.com/questions/55182578/how-to-read-plain-text-file-in-kotlin
        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile = data?.data //The uri with the location of the file
            //sample_text.text = selectedFile.toString()
            if (selectedFile != null)    {
                val infile: InputStream? = contentResolver.openInputStream(selectedFile)
                if (infile != null) {
                    sample_text.text = infile.bufferedReader().use { it.readLine()
                        it.readLine()
                        it.readLine()}
                    //xmlParse(infile)
                    data?.data?.also { it ->
                        handlePathOz.getRealPath(it)
                    }
                    //xmlParse(selectedFile)
                }
            }

            //sample_text.text = File(path).readText()
        } else {
            sample_text.text = "none"
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
           // System.loadLibrary("native-lib")
        }
    }

    // https://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore
    fun getRealPathFromURI(context: Context, contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj =
                arrayOf(MediaStore.Images.Media.DATA)
            cursor = contentUri?.let { context.getContentResolver().query(it, proj, null, null, null) }
            val column_index: Int = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA) ?: 0
            cursor?.moveToFirst()
            cursor?.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
    }

    fun xmlParse(path: String) {
        // https://developer.android.com/reference/org/xmlpull/v1/XmlPullParser
            try {
                println("test")
                //val fileName = getRealPathFromURI(null, selectedFile)
                val fileName = path
                println(path)
                val reader = FileReader(fileName)
                val builder = TreeBuilder()
                val tree = builder.parseXML(reader)
                println(tree)
//                val userList = ArrayList<java.util.HashMap<String?, String?>>()
//                var user: HashMap<String?, String?>? = HashMap()
                val lv: ListView = findViewById(R.id.listView)
//                //https://stackoverflow.com/questions/50196357/android-kotlin-beginner-using-file-with-uri-returned-from-action-get-conte/50196709
//                val inputStream = contentResolver.openInputStream(selectedFile)
//                //val inputStream = assets.open(filename)
//                val parserFactory: XmlPullParserFactory = XmlPullParserFactory.newInstance()
//                val parser: XmlPullParser = parserFactory.newPullParser()
//                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
//                parser.setInput(inputStream, "utf-8")
//                var tag: String?
//                var text = ""
//                var event = parser.eventType
//                while (event != XmlPullParser.END_DOCUMENT) {
//                    tag = parser.name
//                    println(event)
//                    println(tag)
//                    print(text)
//                    println(userList)
//                    println("--")
//                    when (event) {
//
//                            // # https://stackoverflow.com/questions/7726239/how-i-get-attribute-using-by-xmlpull-parser
//                        // parser.getAttributeValue(null, "url")
//                        XmlPullParser.START_TAG -> if (tag == "node") {
//                            user = HashMap()
//                            var name = parser.getAttributeValue(null, "name")
//                            user!!["designation"] = name
//                            println("node found $name")
//                        }
//                        XmlPullParser.TEXT -> {
//                            text = parser.text
//                            println("node $text")
//                        }
//
//                        XmlPullParser.END_TAG -> when (tag) {
//                            "rich_text" -> user!!["name"] = text
//                            "node" -> if (user != null) {
//                                userList.add(user)
//                                //user!!["designation"] = parser.getAttributeValue(2)
//
//                            }
//                        }
//                    }
//                    event = parser.next()
//                }
//                val adapter: ListAdapter = SimpleAdapter(this@MainActivity, userList, R.layout.row,
//                    arrayOf("name", "designation", "location"), intArrayOf(R.id.tvName,
//                        R.id.tvDesignation, R.id.tvLocation))
//                lv.adapter = adapter
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            }
        }

//    fun buildTree(parser: XmlPullParser) : TreeNode {
//        var root: TreeNode = null
//        var child: TreeNode = null
//        var curSib: TreeNode = null
//        var done: Boolean = false
//        do {
//            var event: Int  = parser.nextToken();
//            when (event) {
//                XmlPullParser.START_TAG -> {
//                    root = buildTagNode()
//                    if (parser.getDepth() == 1) {
//                        documentTag = root
//                    }
//                    var t: TreeNode = buildTree();
//                    while ( !t instanceof EndTag) {
//                        if (child == null) {
//                            child = t
//                            curSib = child
//                        } else {
//                            curSib.setSibling(t)
//                            curSib = t
//                        }
//                        t = buildTree()
//                    }
//                    root.setChild( child )
//                    done = true
//                }
//
//
//                XmlPullParser.TEXT -> {
//                    var content: String = parser.getText()
//                    root = TextNode( text )
//                    done = true
//                }
//                XmlPullParser.END_TAG -> {
//                    var name: String = parser.getName()
//                    root = EndTag(name)
//                    var depth: int = parser.getDepth()
//                    if ( depth == 1 )
//                }
//                XmlPullParser.END_DOCUMENT ->
//                    root = new
//
//
//            }
//        }
//    }
override fun onMenuItemClick(item: MenuItem?): Boolean {
    when (item?.itemId) {
        R.id.action_add_item -> {
            //向当前行增加一个儿子
            val node = adapter!!.currentNode
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
            val contact = ExampleListTreeAdapter.ContactInfo(
                bitmap, "New contact", "[离线]我没有状态")
            val childNode = tree.addNode(node, contact, R.layout.contacts_contact_item)
            adapter!!.notifyTreeItemInserted(node, childNode)
            return true
        }
        R.id.action_clear_children -> {
            //清空所有的儿子们
            val node = adapter!!.currentNode
            val range = tree.clearDescendant(node)
            adapter!!.notifyItemRangeRemoved(range!!.first, range.second)
            return true
        }
        else -> return false
    }
}

    //http://bearcave.com/software/java/xml/treebuilder.html
}
