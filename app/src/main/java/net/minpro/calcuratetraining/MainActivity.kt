package net.minpro.calcuratetraining

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val arrayAdaptor = ArrayAdapter.createFromResource(this, R.array.number_of_question, android.R.layout.simple_spinner_item)

        //スピナーとつなぐ
        spinner.adapter = arrayAdaptor

        button.setOnClickListener {
            //スピナーをゲットする
            val numberOfQuestion:Int = spinner.selectedItem.toString().toInt()

            val intent = Intent(this@MainActivity, TestActivity::class.java)
            intent.putExtra("numberOfQuestion", numberOfQuestion)
            startActivity(intent)


        }
    }
}
