package com.liflymark.normalschedule.ui.import_show_score

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.gyf.immersionbar.ImmersionBar
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.databinding.ActivityImportScoreBinding
import com.liflymark.normalschedule.databinding.ActivityShowScoreBinding
import com.liflymark.normalschedule.databinding.ItemProjectScoreBinding
import com.liflymark.normalschedule.databinding.ItemScoreBinding
import com.liflymark.normalschedule.logic.utils.Convert
import org.angmarch.views.NiceSpinner
import java.util.*


class ShowScoreActivity : AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this)[ShowScoreViewModel::class.java] }
    private lateinit var binding: ActivityShowScoreBinding
    private lateinit var bindingItem: ItemProjectScoreBinding
    private val scoreList = mutableListOf<View>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowScoreBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.scoreToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);//添加默认的返回图标
        supportActionBar?.setHomeButtonEnabled(true); //设置返回键可用
        // score_activity_layout.addStatusBarTopPadding()

        ImmersionBar.with(this)
            .statusBarDarkFont(true)
            .barColor("#2196f3")
            .init()

        val allGradeListString = intent.getStringExtra("grade_list_string")?:""
        if (allGradeListString!=""){
            viewModel.gradeString = allGradeListString
        }
        val allGradeList = Convert.jsonToAllGrade(viewModel.gradeString)
        val allGradeListSize = allGradeList.size
        binding.allGrade.removeAllViews()
        val niceSpinner = findViewById<View>(R.id.team_spinner) as NiceSpinner
        val dataset = mutableListOf<String>()

        for (i in 0 until allGradeListSize){
            val singleProjectGrade = allGradeList[i]
            for (a in singleProjectGrade.thisProjectGradeList.size downTo 1){
                Log.d("ShowScoreActivity", a.toString())
                Log.d("ShowScore", dataset.toString())
                dataset.add("第${a}学期")
            }
            for (t in singleProjectGrade.thisProjectGradeList) {
//                val headerText = singleScoreLayout.findViewById<TextView>(R.id.course_name)
//                headerText.text = "第${singleProjectGrade.thisProjectGradeList.size-i}学期"
//                single_project_score.addView(headerText)
                bindingItem = ItemProjectScoreBinding.inflate(layoutInflater)
                val cardViewLayout = LayoutInflater.from(this).inflate(
                    R.layout.item_project_score,
                    binding.allGrade,
                    false
                )
                val cardView = cardViewLayout.findViewById<LinearLayout>(R.id.single_project_score)
                for (a in t) {
                    val singleScoreLayout = LayoutInflater.from(this).inflate(
                        R.layout.item_score,
                        bindingItem.singleProjectScore,
                        false
                    )
                    val courseNameTextView =
                        singleScoreLayout.findViewById(R.id.course_name) as TextView
                    val courseTypeTextView =
                        singleScoreLayout.findViewById(R.id.course_type) as TextView
                    val scoreTextView =
                        singleScoreLayout.findViewById(R.id.course_score) as TextView

                    courseNameTextView.text = a.courseName
                    courseTypeTextView.text = a.attributeName
                    scoreTextView.text = a.score.toString()
                    cardView.addView(singleScoreLayout)
                }
                scoreList.add(cardViewLayout)
            }

        }
        niceSpinner.attachDataSource(dataset)
        binding.allGrade.addView(scoreList[0])

        niceSpinner.setOnSpinnerItemSelectedListener { parent, view, position, id ->
            val item = parent.getItemAtPosition(position)
            when(item.toString()){
                "第1学期" -> refreshScore(1)
                "第2学期" -> refreshScore(2)
                "第3学期" -> refreshScore(3)
                "第4学期" -> refreshScore(4)
                "第5学期" -> refreshScore(5)
                "第6学期" -> refreshScore(6)
                "第7学期" -> refreshScore(7)
                "第8学期" -> refreshScore(8)
                "第9学期" -> refreshScore(9)
                "第10学期" -> refreshScore(10)
                else -> refreshScore(1)
            }
        }
    }

    private fun refreshScore(pos:Int){
        binding.allGrade.removeAllViews()
        val size = scoreList.size
        binding.allGrade.addView(scoreList[size-pos])
    }

}