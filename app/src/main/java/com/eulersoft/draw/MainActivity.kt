package com.eulersoft.draw

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.Gallery
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {
    private var mImageButtonCurrentPaint: ImageButton?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawing_view.setSizeForBrush(10.toFloat())
        mImageButtonCurrentPaint = ll_paint_colors[1] as ImageButton

        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
        )

        ib_Brush.setOnClickListener { showBrushSizeChooseDialog() }

        ib_gallery.setOnClickListener {
            if(isReadStorageAllowed()){
            val pickPhotoIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhotoIntent, GALLERY)
        }else{
                requestStoragePermission()
            }

        }

        ib_undo.setOnClickListener {
            drawing_view.onClickUndo()
        }
        ib_Save.setOnClickListener {
            if(isReadStorageAllowed()){
            BitmapAsyncTask(getBitmapFromView(fl_drawing_view_container)).execute()
            }
        }
        val pic = ArrayList<Int>()
        pic.add(R.drawable.deer)
        pic.add(R.drawable.barnie)
        pic.add(R.drawable.minions)
        pic.add(R.drawable.mushroom)
        pic.add(R.drawable.owl)
        pic.add(R.drawable.penguin)
        pic.add(R.drawable.princes)
        pic.add(R.drawable.princes2)
        pic.add(R.drawable.random)
        pic.add(R.drawable.square_pants)
        pic.add(R.drawable.tweety)
        pic.add(R.drawable.winnie)
        iv_background.setImageResource(pic.get(0))
        var i : Int=0
        ib_right_arrow.setOnClickListener {

            if(i<pic.size-1){
                i++
                drawing_view.reset()
                iv_background.setImageResource(pic.get(i))
            }else{
                Toast.makeText(this, "this is the last image", Toast.LENGTH_SHORT).show()
            }
        }

        ib_left_arrow.setOnClickListener {
            if(i>0){
                i=i-1
                iv_background.setImageResource(pic.get(i))
                drawing_view.reset()

            }else{
                Toast.makeText(this, "this is the first image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK){
            if(requestCode== GALLERY){
                try{
                    if(data!!.data != null){
                        iv_background.visibility = View.VISIBLE
                        iv_background.setImageURI(data.data)
                    }else{
                        Toast.makeText(this, "error in parsing image", Toast.LENGTH_SHORT).show()
                    }
                }catch (e :Exception){
                    e.printStackTrace()
                }
            }
        }
    }
    private fun showBrushSizeChooseDialog(){
        val brushdialog = Dialog(this)
        brushdialog.setContentView(R.layout.dialog_brush_size)
        brushdialog.setTitle("Brush Size : ")
        val smallBtn = brushdialog.ib_small_brush
        smallBtn.setOnClickListener {
            drawing_view.setSizeForBrush(10.toFloat())
            brushdialog.dismiss()
        }

        val mediumBtn = brushdialog.ib_medium_brush
        mediumBtn.setOnClickListener {
            drawing_view.setSizeForBrush(20.toFloat())
            brushdialog.dismiss()
        }
        val largeBtn = brushdialog.ib_large_brush
        largeBtn.setOnClickListener {
            drawing_view.setSizeForBrush(30.toFloat())
            brushdialog.dismiss()
        }
        brushdialog.show()
    }


    fun paintClicked(view:View){
    if(view!== mImageButtonCurrentPaint){
        val imageButton = view as ImageButton

        var colorTag = imageButton.tag.toString()
        drawing_view.setColor(colorTag)
        imageButton.setImageDrawable(
            ContextCompat.getDrawable(this,R.drawable.pallet_pressed)
        )

        mImageButtonCurrentPaint!!.setImageDrawable(ContextCompat.getDrawable(
            this,R.drawable.pallet_normal))

        mImageButtonCurrentPaint = view

    }
    }
    private fun requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE).toString())){
            Toast.makeText(this, "need a permission", Toast.LENGTH_SHORT).show()
        }
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== STORAGE_PERMISSION_CODE){
            if(grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "now you can read storage", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "oop permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun isReadStorageAllowed():Boolean{
        val result = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)

        return result== PackageManager.PERMISSION_GRANTED
    }
    private fun getBitmapFromView(view:View):Bitmap{
        val returnedBitmap = Bitmap.createBitmap(view.width,
            view.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if(bgDrawable !=null){

            bgDrawable.draw(canvas)
        }else{
            canvas.drawColor(Color.WHITE)
        }
         view.draw(canvas)

        return returnedBitmap

    }

    private inner class BitmapAsyncTask(val mBitmap:Bitmap):AsyncTask<Any,Void,String>(){

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!result!!.isEmpty()){
                Toast.makeText(this@MainActivity, "file saved succesfully : $result"
                    , Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@MainActivity, "something went wrong while saving the file",
                    Toast.LENGTH_SHORT).show()
            }
        }
        override fun doInBackground(vararg params: Any?): String {
            var result =""
            if(mBitmap != null){

               try{
                   val bytes = ByteArrayOutputStream()
                   mBitmap.compress(Bitmap.CompressFormat.PNG,90,bytes)
                   val f = File(externalCacheDir!!.absoluteFile.toString()+ File.separator
                           + "DrawApp_" + System.currentTimeMillis()/1000+".png")
                   val fos = FileOutputStream(f)
                   fos.write(bytes.toByteArray())
                   fos.close()
                   result = f.absolutePath
               }catch(e:Exception){
                   result=""
                   e.printStackTrace()
               }
            }
            return result
        }


    }

    companion object{
        private const val STORAGE_PERMISSION_CODE = 1
        private const val GALLERY = 2
    }

}