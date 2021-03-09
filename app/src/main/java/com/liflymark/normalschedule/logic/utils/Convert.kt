package com.liflymark.normalschedule.logic.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.liflymark.normalschedule.logic.bean.CourseBean
import com.liflymark.normalschedule.logic.bean.OneByOneCourseBean
import com.liflymark.normalschedule.logic.model.AllCourse
import com.liflymark.normalschedule.logic.model.Grade

internal object Convert {
    lateinit var oneCourse: OneByOneCourseBean

    fun courseResponseToBean(courseResponse: AllCourse) : CourseBean {
        val colorList = arrayListOf<String>()
        colorList.apply {
            add("#12c2e9")
            add("#376B78")
            add("#f64f59")
            add("#CBA689")
            add("#ffffbb33")
            add("#8202F2")
            add("#F77CC2")
            add("#4b5cc4")
            add("#426666")
            add("#40de5a")
            add("#f0c239")
            add("#725e82")
            add("#c32136")
            add("#b35c44")
        }
        val num = string2Unicode(courseResponse.courseName).toInt()
        val color = colorList[num % colorList.count()]
        return CourseBean(
                courseResponse.campusName,
                courseResponse.classDay,
                courseResponse.classSessions,
                courseResponse.classWeek,
                courseResponse.continuingSession,
                courseResponse.courseName,
                courseResponse.teacher,
                courseResponse.teachingBuildName,
                color
        )
    }
    fun courseBeanToOneByOne(courseBeanList: List<CourseBean>): List<List<OneByOneCourseBean>> {
        val allOneCourseList = mutableListOf<List<OneByOneCourseBean>>()
        for (i in 0..19){
            val oneCourseList = mutableListOf<OneByOneCourseBean>()
            for (courseBean in courseBeanList) {
                val name = courseBean.courseName + "\n" + courseBean.teachingBuildName + "\n" + courseBean.teacher

                when(courseBean.classWeek[i].toString()){
                    "1" -> oneCourseList.add(OneByOneCourseBean(
                            name,
                            courseBean.classSessions - 1,
                            courseBean.classSessions + courseBean.continuingSession - 1,
                            courseBean.classDay - 1,
                            courseBean.color))
//                    "0" -> oneCourseList.add(OneByOneCourseBean("0",courseBean.classDay, 0, 0))
//                    else -> oneCourseList.add(OneByOneCourseBean("0",courseBean.classDay, 0, 0))
                }
            }
            allOneCourseList.add(oneCourseList)
        }
        return allOneCourseList
    }

    fun allCourseToJson(allCourseList: List<AllCourse>) : String{
        return Gson().toJson(allCourseList)
    }

    fun allGradeToJson(allGradeList: List<Grade>): String {
        return Gson().toJson(allGradeList)
    }

    fun jsonToAllCourse(str: String) : List<AllCourse>{
        lateinit var a: List<AllCourse>
        val listType = object : TypeToken<List<AllCourse>>() {}.type
        a =  Gson().fromJson(str, listType)
        return a
    }

    fun jsonToAllGrade(str:String): List<Grade>{
        lateinit var a: List<Grade>
        val listType = object : TypeToken<List<Grade>>() {}.type
        a =  Gson().fromJson(str, listType)
        return a
    }

    fun string2Unicode(string: String): String {
        val unicode = StringBuffer()
        for (i in 0 until string.length) {
            // 取出每一个字符
            val c = string[i]
            // 转换为unicode
            // unicode.append("\\u" + Integer.toHexString(c.toInt()))
            unicode.append(Integer.toHexString(c.toInt())[0])
            if (i > 2) {
                break
            }
        }
        return unicode.toString()
    }

//    fun getAllOneCourse(courseBeanList: List<CourseBean>): List<List<OneByOneCourseBean>> {
//        val allOneCourseList = mutableListOf<List<OneByOneCourseBean>>()
//        val needAllOneByOneCourseList = mutableListOf<List<List<OneByOneCourseBean>>>()
//        for (i in courseBeanList) {
//            allOneCourseList.add(courseBeanToOneByOne(i))
//        }
//
//        for (t in 0..19){
//            val aOneCourseList = mutableListOf<OneByOneCourseBean>()
//            for(a in allOneCourseList) {
//                if (a[t].courseName!="0")
//                    aOneCourseList.add(a[t])
//            }
//            needAllOneByOneCourseList.add(aOneCourseList.toList())
//        }
//
//
//
//        return allOneCourseList
//    }


}