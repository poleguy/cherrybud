package com.poleguy.cherrybud


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.onimur.handlepathoz.HandlePathOz
import br.com.onimur.handlepathoz.HandlePathOzListener
import br.com.onimur.handlepathoz.model.PathOz
import com.poleguy.cherrybud.niuedu.ListTree
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.tree_view.*
import org.xmlpull.v1.XmlPullParserException
import treebuilder.*
import java.io.*
import java.util.*

public interface OpenFileClicked {
    /**
     * This method will be invoked when an item is clicked if the item
     * itself did not already handle the event.
     *
     * tbd:
     * @param item the menu item that was clicked
     * @return {@code true} if the event was handled, {@code false}
     *         otherwise
     */
    fun openNode()
}

// https://stackoverflow.com/questions/31297246/activity-appcompatactivity-fragmentactivity-and-actionbaractivity-when-to-us
class MainActivity : AppCompatActivity(),  HandlePathOzListener.SingleUri, PopupMenu.OnMenuItemClickListener, OpenFileClicked {

    private lateinit var handlePathOz: HandlePathOz
    var path : String = "none"

    //保存数据的集合
    private val tree = ListTree()

    //从ListTreeAdapter派生的Adapter
    internal var adapter: ExampleListTreeAdapter? = null

    //# https://developer.android.com/training/permissions/requesting.html
    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher. You can use either a val, as shown in this snippet,
    // or a lateinit var in your onAttach() or onCreate() method.
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.

            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // https://github.com/android/views-widgets-samples/blob/master/ViewPager2/app/src/main/java/androidx/viewpager2/integration/testapp/BaseCardActivity.kt
        //var viewPager: ViewPager2 = findViewById(R.id.pager)

        handlePathOz = HandlePathOz(this, this)
        // Example of a call to a native method
        //sample_text.text = stringFromJNI()

        //sample_text.text = "blah"

        //val list = listOf("a","b","c")
        //populateTreeData(list)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showIntent()
        }


    }

    fun populateTree() {
        // ExampleListTreeAdapter

        //创建后台数据：一棵树
        //创建组们，是root node，所有parent为null
        val groupNode1 = tree.addNode(null, "特别关心", R.layout.contacts_group_item)
        val groupNode2 = tree.addNode(null, "two 我的好友", R.layout.contacts_group_item)
        val groupNode3 = tree.addNode(null, "朋友", R.layout.contacts_group_item)
        val groupNode4 = tree.addNode(null, "家人", R.layout.contacts_group_item)
        val groupNode5 = tree.addNode(null, "five 同学", R.layout.contacts_group_item)

        //第二层
        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        var contact = ExampleListTreeAdapter.ContactInfo(bitmap, "mno 王二", "[在线]我是王二")
        val contactNode1 = tree.addNode(groupNode2, contact, R.layout.contacts_contact_item)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "jkl 王三", "[在线]我是王三")
        val contactNode2 = tree.addNode(groupNode5, contact, R.layout.contacts_contact_item)
        //再添加一个
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "ghi 王四", "[离线]我没有状态")
        tree.addNode(groupNode2, contact, R.layout.contacts_contact_item)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "def 王五", "[离线]我没有状态")
        tree.addNode(groupNode5, contact, R.layout.contacts_contact_item)

        //第三层
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "abc 东邪", "[离线]出来还价")
        var n: ListTree.TreeNode = tree.addNode(contactNode1, contact, R.layout.contacts_contact_item)
        n.isShowExpandIcon = false
        //再添加一个
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.contacts_normal)
        contact = ExampleListTreeAdapter.ContactInfo(bitmap, "李圆圆", "[离线]昨天出门没出去")
        n = tree.addNode(contactNode1, contact, R.layout.contacts_contact_item)
        n.isShowExpandIcon = false

        adapter = ExampleListTreeAdapter(tree, this, this)
        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = adapter

    }

    private fun displayTree() {
        // displays tree in list view

        adapter = ExampleListTreeAdapter(tree, this, this)
        listView.layoutManager = LinearLayoutManager(this)
        listView.adapter = adapter

    }

    private fun saveMRU(path:String) {
        // not opened with an intent?
        // restore old path
        // https://developer.android.com/guide/topics/ui/settings/use-saved-values
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */)
        //val lastFile = sharedPreferences.getString("last_file", "")
        //Toast.makeText(this, "URI for last file: ${lastFile}", Toast.LENGTH_LONG).show()


        // https://developer.android.com/training/data-storage/shared-preferences?authuser=3
        with (sharedPreferences.edit()) {
            putString("last_file", path)
            apply()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun openPath(path:String) {
        // check permissions and open path
        // save in most recently used
        saveMRU(path)

        //# https://stackoverflow.com/questions/23527767/open-failed-eacces-permission-denied
        //stackoverflow.com/questions/23527767/open-failed-eacces-permission-denied
        // Storage Permissions
        val REQUEST_EXTERNAL_STORAGE = 1
        val PERMISSIONS_STORAGE = arrayOf<String>(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        /**
         * Checks if the app has permission to write to device storage
         *
         * If the app does not has permission then the user will be prompted to grant permissions
         *
         * @param activity
         */
        fun verifyStoragePermissions(activity: Activity?) {
            // Check if we have write permission
            val permission: Int = ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                if (activity != null) {
                    ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                    )
                }
            }
        }

        verifyStoragePermissions(this)


        //Handle any Exception (Optional)
        //tr?.let {
        //    Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
        //}

        //# https://developer.android.com/training/permissions/requesting.html
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale( android.Manifest.permission.READ_EXTERNAL_STORAGE ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                //showInContextUI(...)
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
        when {
            ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                println("write access ok")
            }
            shouldShowRequestPermissionRationale( android.Manifest.permission.WRITE_EXTERNAL_STORAGE ) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected. In this UI,
                // include a "cancel" or "no thanks" button that allows the user to
                // continue using your app without granting the permission.
                //showInContextUI(...)
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
        fun main() {
            //https://stackoverflow.com/questions/7908193/how-to-access-downloads-folder-in-android
            val dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            println(dir)
            handleIntent()


            //val inputStream: InputStream = File(dir?.path + File.separator + "simple.ctd").inputStream()
            //val inputString = inputStream.bufferedReader().use { it.readText() }
            //println(inputString)
        }
        main()
        xmlParse(path)

    }
    //On Completion (Sucess or Error)
    //If there is a cancellation or error.

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestHandlePathOz(pathOz: PathOz, tr: Throwable?) {
        //Hide Progress

        //Now you can work with real path:
        Toast.makeText(
            this,
            "The real path is: ${pathOz.path} \n The type is: ${pathOz.type}",
            Toast.LENGTH_SHORT
        ).show()
        path = pathOz.path

        openPath(path)

    }

    // https://www.raywenderlich.com/2705552-introduction-to-android-activities-with-kotlin
    // called after clicking on open file
    private fun onActivityResult(requestCode: Int, result: ActivityResult) {
        //super.onActivityResult(requestCode, resultCode, data)

        // https://stackoverflow.com/questions/55182578/how-to-read-plain-text-file-in-kotlin
        if (requestCode == 111 && result.resultCode == RESULT_OK) {
            val intent = result.data
            val selectedFile = intent?.data //The uri with the location of the file
            //sample_text.text = selectedFile.toString()
            if (selectedFile != null)    {
                val infile: InputStream? = contentResolver.openInputStream(selectedFile)
                if (infile != null) {
                    //sample_text.text = infile.bufferedReader().use { it.readLine()
                    //    it.readLine()
                    //    it.readLine()
                    //}


                    //xmlParse(infile)
                    intent?.data?.also { it ->
                        handlePathOz.getRealPath(it)
                    }
                    //xmlParse(selectedFile)
                }
            }

            //sample_text.text = File(path).readText()
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
            val xmlTree = builder.parseXML(reader)
            println(xmlTree)

            var curr: TreeNode // node we're parsing currently
            curr = xmlTree.child
            //list.add(curr.toString())

            // in order tree traversal without recursion
            var stack: Stack<TreeNode> = Stack<TreeNode>()
            var parentStack : Stack<ListTree.TreeNode> = Stack<ListTree.TreeNode>()

            // https://www.geeksforgeeks.org/inorder-tree-traversal-without-recursion/
            var level = 0
            var done = false
            while (!done) {
                // traverse the tree
                while (!done) {
                    if (("node" in curr.toString())) {
                        var name: String = "none"
                        var tn: TagNode = curr as TagNode
                        var al: AttributeList = tn.attrList as AttributeList
                        if (al != null) {
                            var it: Iterator<Attribute> = al.iterator as Iterator<Attribute>
                            for (attr in it) {
                                if (attr.name == "name") {
                                    name = attr.value
                                    break
                                }
                            }
                        }
                        // add it to the view
                        var node : ListTree.TreeNode? = null
                        val data = NodeData(name, level, curr)
                        node = if (parentStack.isEmpty()) {
                            tree.addNode(null, data, R.layout.contacts_group_item)
                        } else {
                            tree.addNode(parentStack.lastElement(), data, R.layout.contacts_group_item)
                        }


                        println(name)
                        if(curr.child != null) {
                            // place pointer to a tree node on the stack before traversing the node's subtree
                            stack.push(curr)
                            parentStack.push(node)
                            // if there are children go ahead and do them next
                            curr = curr.child
                            level += 1
                            println(level)
                            continue
                        }
                    }
                    if (curr.sibling == null ) {
                        if (stack.isNotEmpty()) {
                            // reached end of siblings
                            // go back up a level
                            level -= 1
                            println(level)
                            curr = stack.pop()
                            parentStack.pop()
                            if (curr.sibling != null) {
                                curr = curr.sibling
                            } else {
                                break
                            }
                        } else {
                            done = true
                            break // stack is empty, so we're done
                        }
                    } else {
                        curr = curr.sibling // update current node
                    }
                }
            }

            displayTree()

            // put the contents of this tree into the tree viewer
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }
    }

    @Parcelize
    data class Item(
        var currentNode: @RawValue NodeDataStr,
        var title: String
    ) : Parcelable

    override fun openNode() {
        // https://developer.android.com/training/basics/firstapp/starting-activity
        sample_text.text = "ABCD"
        //slider_content.text = "Sample Content Updated"
        //ScreenSlidePagerActivity.supportFragmentManager
        //var fragmentRegister.textViewLanguage.setText("hello mister how do you do");
        //var Fragment Object = ()getSupportFragmentManager()
        // https://www.techotopia.com/index.php/Using_Fragments_in_Android_Studio_-_A_Kotlin_Example
        val intent = Intent(this, ScreenSlidePagerActivity::class.java)
        //startActivity(intent)


        //# https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application?rq=1

        val currentNode = adapter!!.currentNode
        val currentNodeData : NodeData = adapter!!.currentNode?.data as NodeData
        val str = currentNodeData.getContent()
        val nds : NodeDataStr = NodeDataStr(str)
        //val intent = Intent(baseContext, SignoutActivity::class.java)

        // https://stackoverflow.com/questions/2139134/how-to-send-an-object-from-one-android-activity-to-another-using-intents
        // https://medium.com/the-lazy-coders-journal/easy-parcelable-in-kotlin-the-lazy-coders-way-9683122f4c00

        var item = Item(nds,"title")
        intent.putExtra("EXTRA_DATA", item)
        startActivity(intent)

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_add_item -> {
                openNode()
                return true
            }
            R.id.action_clear_children -> {
                //清空所有的儿子们
                //Empty all the sons
                val node = adapter!!.currentNode
                val range = tree.clearDescendant(node)
                adapter!!.notifyItemRangeRemoved(range!!.first, range.second)
                return true
            }
            else -> return false
        }
    }

    //http://bearcave.com/software/java/xml/treebuilder.html



    //# https://developer.android.com/training/basics/intents/filters
    @RequiresApi(Build.VERSION_CODES.M)
    fun showIntent() {

        // Figure out what to do based on the intent type
        val type = intent?.type
        val uri = intent?.data
        Toast.makeText(this, "${type} ${uri}", Toast.LENGTH_LONG).show()
        print("showIntent")
        println(uri.toString())
        if (uri != null) {

            //# https://github.com/xgouchet/Ted/blob/979f8538bbd856fb9f8b6f5f29ff594addc9574d/Ted/src/fr/xgouchet/texteditor/TedActivity.java


            handlePathOz.getRealPath(uri)
        } else {
            // not opened with an intent?
            // restore old path
            // https://developer.android.com/guide/topics/ui/settings/use-saved-values
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this /* Activity context */)
            val lastFile = sharedPreferences.getString("last_file", "")
            Toast.makeText(this, "URI for last file: ${lastFile}", Toast.LENGTH_LONG).show()
            if (lastFile != null) {
                openPath(lastFile)
            }
        }

        //xmlParse(uri.toString())

    }
    private fun handleIntent() {
        val uri = intent.data
        if (uri == null) {
            tellUserThatCouldntOpenFile()
            return
        }
        var text: String? = null
        try {
            val inputStream = contentResolver.openInputStream(uri)
            text = getStringFromInputStream(inputStream)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (text == null) {
            tellUserThatCouldntOpenFile()
            return
        }
        //val textView: TextView = findViewById(R.id.tv_content)
        //textView.setText(text)
        println(text)
    }

    private fun tellUserThatCouldntOpenFile() {
        Toast.makeText(this, "counld not open", Toast.LENGTH_SHORT).show()
    }

    private fun getStringFromInputStream(stream: InputStream?): String {
            var n = 0
            val buffer = CharArray(1024 * 4)
            val reader = InputStreamReader(stream, "UTF8")
            val writer = StringWriter()
            while (-1 != reader.read(buffer).also({ n = it })) writer.write(buffer, 0, n)
            return writer.toString()
        }


    //# https://developer.android.com/guide/topics/ui/settings.html?authuser=3

    private fun showSettings() {
        // show settings screen
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.include, MySettingsFragment())
            .commit()

    }
    // create menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    //# https://www.javatpoint.com/kotlin-android-options-menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(applicationContext, "click on setting", Toast.LENGTH_LONG).show()
                true
            }
            R.id.action_open ->{
                Toast.makeText(applicationContext, "click on open", Toast.LENGTH_LONG).show()
                openFile()
                return true
            }
            R.id.action_exit ->{
                Toast.makeText(applicationContext, "click on exit", Toast.LENGTH_LONG).show()
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    fun openFile() {
        // open a file to view
        //val intent = Intent()
        //    .setType("*/*")
        //    .setAction(Intent.ACTION_GET_CONTENT)

        //startForResult(Intent.createChooser(intent, "Select a file"), 111)

        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode === Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                onActivityResult(111, result)
            }
        }

        val intent: Intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)
        //val intent = Intent(this, SomeActivity::class.java)
        resultLauncher.launch(intent)

    }


}


//stackoverflow.com/questions/3465429/register-to-be-default-app-for-custom-file-type
