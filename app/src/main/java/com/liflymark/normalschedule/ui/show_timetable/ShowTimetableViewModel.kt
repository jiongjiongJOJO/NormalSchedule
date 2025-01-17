package com.liflymark.normalschedule.ui.show_timetable

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.lifecycle.*
import com.liflymark.normalschedule.NormalScheduleApplication
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.Bulletin2
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.model.AllCourse
import com.liflymark.normalschedule.logic.utils.Convert
import com.liflymark.normalschedule.logic.utils.GetDataUtil
import com.liflymark.normalschedule.logic.utils.LayoutCoordinatesAndDescription
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class ShowTimetableViewModel: ViewModel() {
    private var courseDatabaseLiveData = MutableLiveData(0)
    private var backgroundId = MutableLiveData(0)
    private var needDeleteCourseNameLiveData = MutableLiveData<String>()
    private var _sentenceLiveDate = MutableLiveData<Boolean>(false)

    var shouldSaved = true

    val layoutList = mutableStateListOf<LayoutCoordinatesAndDescription>()
    val showUserGuide = mutableStateOf<Boolean>(false)

    var showToast = 0

    val newUserFLow = Repository.getNewUserOrNot()
    val userVersion = Repository.getUserVersion()
    val courseDatabaseLiveDataVal = Transformations.switchMap(courseDatabaseLiveData) {
        runBlocking {
            val colorItems = Repository.getColorListAsync()
            Repository.loadAllCourse2(colorList = Repository.colorListSettingToStringList(colorItems))
        }
    }


    val backgroundUriStringLiveData = Transformations.switchMap(backgroundId){
        Repository.loadBackground()
    }

    val deleteCourseBeanByNameLiveData = Transformations.switchMap(needDeleteCourseNameLiveData){
        needDeleteCourseNameLiveData.value?.let { it1 -> Repository.deleteCourseByName(it1) }
    }
    val sentenceLiveData = Transformations.switchMap(_sentenceLiveDate){
        Repository.getSentences(it).asLiveData()
    }

    val settingsLiveData = Repository.getScheduleSettings().asLiveData()

    val showStartBulletin2 = mutableStateOf<Bulletin2?>(null)

    init {
        viewModelScope.launch {
            val pm: PackageManager = NormalScheduleApplication.context.packageManager
            val packageName = NormalScheduleApplication.context.packageName
            val pi = pm.getPackageInfo(packageName, 0)
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pi.longVersionCode
            } else {
                pi.versionCode
            }.toInt()
            showStartBulletin2.value = Repository.getNewStartBulletin2(versionCode)
        }
    }


    fun loadAllCourse() {
        courseDatabaseLiveData.value = courseDatabaseLiveData.value?.plus(1)
        Log.d("ShowTimetable", "loadAllCourse执行")
    }

    fun deleteCourse(courseItem:String){
        needDeleteCourseNameLiveData.value = courseItem
    }

    fun setBackground(){
        Log.d("ShowViewModel", "setback")
        backgroundId.value = backgroundId.value?.plus(1)
    }


    suspend fun insertOriginalCourse(allCourseList: List<AllCourse>) {
        Log.d("CourseViewModel", "获取到课程")
        Repository.insertCourse2(allCourseList)
    }

    suspend fun deleteAllCourse(){
        Repository.deleteAllCourseBean2()
    }


    fun mergeClass(className: String,
                   classDay: Int,
                   classSessions: Int,
                   continuingSession: Int,
                   buildingName: String){
        viewModelScope.launch {
            var classWeekResult = Integer.parseInt("000000000000000000000000", 2)
            var teacher = ""
            val classList = Repository.loadCourseUnTeacher(className, classDay, classSessions, continuingSession, buildingName)
            for (singleBean in classList){
                val singleBeanWeek = Integer.parseInt(singleBean.classWeek,2)
                classWeekResult = singleBeanWeek or classWeekResult
                teacher += "${singleBean.teacher} "
            }

            var classWeekResultStr = Integer.toBinaryString(classWeekResult)
            val length = classWeekResultStr.length
            if (classWeekResultStr.length < 24){
                repeat(24-length){
                    classWeekResultStr = "0$classWeekResultStr"
                }
            }
            val newBean = CourseBean(
                campusName = "河北大学",
                classDay = classDay,
                classSessions = classSessions,
                classWeek = classWeekResultStr,
                continuingSession = continuingSession,
                courseName = className,
                teacher = teacher,
                teachingBuildName = buildingName,
                color = Convert.stringToColor(className)
            )
            if (Integer.parseInt("000000000000000000000000", 2)!=classWeekResult) {
                Repository.insertCourse(newBean)
                Repository.deleteCourseByList(classList)
//                loadAllCourse()
            }
        }
    }

    fun fetchSentence(force:Boolean = false) {
        _sentenceLiveDate.value = force
    }


    fun getNowWeek(): Int{
        return  GetDataUtil.whichWeekNow()
    }

    fun startSchool(): Boolean{
        return GetDataUtil.startSchool()
    }

    fun startSchoolDay(): Int{
        return GetDataUtil.startSchoolDay()
    }

    fun startHoliday(): Boolean{
        return GetDataUtil.whichWeekNow() >= 19
    }

    fun putPosition(
        index:Int,
        layout:LayoutCoordinates,
        description:String
    ){
        val hadIndex = layoutList.map { it.index }
        if (index in hadIndex){ return }
        layoutList.add(
            LayoutCoordinatesAndDescription(
                index = index,
                layout = layout,
                description = description
            )
        )
    }

    // size=4 为需要提示的个数，防止页面没有渲染完毕就显示
    fun checkShowUserGuide(){
        Log.d("ShowViewmodel", "layout"+layoutList.size.toString())
        if (layoutList.size==4){
            viewModelScope.launch {
                if (Repository.getUserVersionS() < 4) {
                    showUserGuide.value = true
                }
            }
        }
    }

}