package com.learn2hire.app.data.repository

import com.learn2hire.app.data.model.Subject
import com.learn2hire.app.data.model.Topic

interface StudyRepository {

    fun getSubjects(): List<Subject>

    fun getSubject(subjectId: String): Subject?

    fun getTopics(subjectId: String): List<Topic>

    fun getTopic(topicId: String): Topic?
}