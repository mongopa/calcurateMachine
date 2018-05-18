package net.minpro.calcuratetraining

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*
import kotlin.concurrent.schedule

class TestActivity : AppCompatActivity(), View.OnClickListener {
    //問題数
    var numberOfQuestion: Int = 0
    //残り問題数
    var numberOfRemaining: Int = 0
    //正解数
    var numberOfCorrect: Int = 0
    //効果音
    lateinit var soundPool: SoundPool
    //サウンドID
    var intSoundId_Correct: Int = 0     //正解音
    var intSoundId_InCorrect: Int = 0   //不正解音
    //タイマー
    lateinit var timer: Timer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // テスト画面が開いたら
//         １．前の問題から渡された問題数を画面に表示させる
        val bundle = intent.extras
        numberOfQuestion = bundle.getInt("numberOfQuestion")
        textViewRemaining.text = numberOfQuestion.toString()
        numberOfRemaining = numberOfQuestion
        numberOfCorrect = 0


        // 「こたえ合わせ」ボタンが押されたら
        buttonAnswerCheck.setOnClickListener {
            //こたえ合わせメソッドを呼ぶ
//             １．回答欄が空白・「-」でない場合にこたえ合わせを実行(answerメソッド)
            if (textViewAnswer.text.toString() != "" && textViewAnswer.text.toString() != "-"){
                answerCheck()
            }

        }

        // 「もどる」ボタンが押されたら
        buttonBack.setOnClickListener {
            finish()
        }

        //電卓ボタンのクリックリスナー＝＞処理はonClickメソッドで
        button0.setOnClickListener(this)
        button1.setOnClickListener(this)
        button2.setOnClickListener(this)
        button3.setOnClickListener(this)
        button4.setOnClickListener(this)
        button5.setOnClickListener(this)
        button6.setOnClickListener(this)
        button7.setOnClickListener(this)
        button8.setOnClickListener(this)
        button9.setOnClickListener(this)
        buttonMinus.setOnClickListener(this)
        buttonClear.setOnClickListener(this)


        //  ２．１問めの問題を出す（questionメソッドの呼び出し）
        question()

    }

    override fun onResume() {
        super.onResume()

        //soundPoolの準備
        soundPool =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            SoundPool.Builder().setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build())
                    .setMaxStreams(1)
                    .build()
        } else {
            SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        }

        //効果音ファイルをメモリにロード
        intSoundId_Correct = soundPool.load(this, R.raw.sound_correct, 1)
        intSoundId_InCorrect = soundPool.load(this, R.raw.sound_incorrect, 1)

        //タイマーの準備
        timer = Timer()

    }

    override fun onPause() {
        super.onPause()

        //効果音を後片付け
        soundPool.release()

        //タイマーのキャンセル
        timer.cancel()
    }


    //問題を出す処理をするメソッド
    //問題が出されたら（questionメソッド）
    private fun question() {
//         １．「もどる」ボタンを使えなくする
        buttonBack.isEnabled = false

//         ２．「こたえ合わせ」ボタンと電卓ボタンを使えるようにする
        buttonAnswerCheck.isEnabled = true
        button0.isEnabled = true
        button1.isEnabled = true
        button2.isEnabled = true
        button3.isEnabled = true
        button4.isEnabled = true
        button5.isEnabled = true
        button6.isEnabled = true
        button7.isEnabled = true
        button8.isEnabled = true
        button9.isEnabled = true
        buttonMinus.isEnabled = true
        buttonClear.isEnabled = true


//        ３．問題の２つの数字を１~100からランダムに設定して表示
        val random = Random()
        val intQuestionLeft = random.nextInt(100) + 1
        val intQuestionRight = random.nextInt(100) + 1
        textViewLeft.text = intQuestionLeft.toString()
        textViewRight.text = intQuestionRight.toString()

//        ４．計算方法を「+」「-」からランダムに設定して表示
        when(random.nextInt(2) + 1){
            1 -> textViewOperator.text = "+"
            2 -> textViewOperator.text = "-"
        }

//        ５．前の問題で入力した自分のこたえを消す
        textViewAnswer.text = ""

//        ６．〇・×画像を見えないようにする
        imageView3.visibility = View.INVISIBLE


    }

    //こたえ合わせ処理をするメソッド
    //こたえあわせ処理（answerCheckメソッド）
    private fun answerCheck() {

//         １．「もどる」「こたえ合わせ」「電卓」ボタンを使えなくする
        buttonBack.isEnabled = false
        buttonAnswerCheck.isEnabled = false
        button0.isEnabled = false
        button1.isEnabled = false
        button2.isEnabled = false
        button3.isEnabled = false
        button4.isEnabled = false
        button5.isEnabled = false
        button6.isEnabled = false
        button7.isEnabled = false
        button8.isEnabled = false
        button9.isEnabled = false
        buttonMinus.isEnabled = false
        buttonClear.isEnabled = false


//        ２．のこり問題数を１つ減らして表示させる
        numberOfRemaining -= 1
        textViewRemaining.text = numberOfRemaining.toString()

//        ３．〇・×画像を見えるようにする
        imageView3.visibility = View.VISIBLE

//         ４．自分の入力したこたえと本当にこたえを比較する
        //自分の答え
        val intMyAnswer: Int = textViewAnswer.text.toString().toInt()

        //本当の答え
        val intRealAnswer: Int =
                if (textViewOperator.text.toString() == "+"){
                    textViewLeft.text.toString().toInt() + textViewRight.text.toString().toInt()
                } else {
                    textViewLeft.text.toString().toInt() - textViewRight.text.toString().toInt()
                }

        //比較
        if (intMyAnswer == intRealAnswer){
            // ５．合っている場合 ⇒ 正解数を１つ増やして表示・〇画像・ピンポン音
            numberOfCorrect += 1
            textViewCorrect.text = numberOfCorrect.toString()
            imageView3.setImageResource(R.drawable.pic_correct)
            soundPool.play(intSoundId_Correct, 1.0f, 1.0f, 0, 0, 1.0f)

        } else {
            // ６．間違っている場合 ⇒ ×画像・ブー音
            imageView3.setImageResource(R.drawable.pic_incorrect)
            soundPool.play(intSoundId_InCorrect, 1.0f, 1.0f, 0, 0, 1.0f)

        }

//         7．正答率を計算して表示（正解数÷出題済み問題数）
        val intPoint:Int = ((numberOfCorrect.toDouble() / (numberOfQuestion - numberOfRemaining).toDouble()) * 100).toInt()
        textViewPoint.text = intPoint.toString()


        if (numberOfRemaining == 0){
            //8．残り問題数がなくなった場合（テストが終わった場合）
//        　　　　　⇒ もどるボタン〇、こたえあわせボタン×、「テスト終了」表示
            buttonBack.isEnabled = true
            buttonAnswerCheck.isEnabled = false
            textViewMessage.text = "テスト終了"

        } else {
            //9．残り問題数がある場合 ⇒ １秒後に次の問題を出す(questionメソッド)
            timer.schedule(1000, {runOnUiThread { question() }})

        }

    }

    //ボタンが押されたときにやることを書く場所
    // 電卓ボタンが押されたら
    override fun onClick(p0: View?) {
//         １．電卓ボタンを押すたびに１文字ずつ表示
//        　　　　（「-」は先頭だけ/０は先頭にならない/１文字目が０の場合０は押せない）

        val button: Button = p0 as Button

        when(p0?.id){
        //クリアボタン＝＞消す
            R.id.buttonClear
            -> textViewAnswer.text = ""

        //マイナスボタン（「-」は先頭だけ）
            R.id.buttonMinus
            ->  if (textViewAnswer.text.toString() == "")
                textViewAnswer.text = "-"

        //0（１文字目が０か-の場合０は押せない）
            R.id.button0
            -> if (textViewAnswer.text.toString() != "0" || textViewAnswer.text.toString() != "-")
                textViewAnswer.append(button.text)

        //1～9の数字
            else
            -> if (textViewAnswer.text.toString() == "0")
                textViewAnswer.text = button.text
            else textViewAnswer.append(button.text)

        }


    }


}
