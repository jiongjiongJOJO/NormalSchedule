package com.liflymark.normalschedule.ui.class_course

import androidx.lifecycle.*
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.bean.getData
import com.liflymark.normalschedule.ui.show_timetable.getNeededClassList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ClassCourseViewModel:ViewModel() {
    private val putMajor = MutableLiveData<List<String>>()
    private val loadBean = MutableLiveData<List<String>>()


    val classCourseListLiveData = Transformations.switchMap(putMajor){
        Repository.loadCourseByMajor2(it[0], it[1]).asLiveData()
    }

    val classBeanLiveData = Transformations.switchMap(loadBean){
        Repository.loadCourseByMajorToAll(it[0], it[1]).asLiveData()
    }

    val departmentListFlow = Repository.getDepartmentList()


//    fun putDepartmentAndMajor(department:String, major: String)=
//        Repository.loadCourseByMajor(department, major).map {
//            if (it.isSuccess){
//                it.getOrElse { getNeededClassList(getData()) }
//            }
//
//    }

    fun putDepartmentAndMajor(department:String, major: String){
        putMajor.value = listOf(department, major.replace(".json",""))
    }

    fun putDepartmentAndMajorBean(department:String, major: String){
        loadBean.value = listOf(department, major.replace(".json",""))
    }

    fun saveAccount(){
        Repository.saveAccount("", "")
    }
    fun getAccount(){
        viewModelScope.launch {

        }
    }
}