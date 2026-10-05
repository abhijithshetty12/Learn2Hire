package com.learn2hire.app.data.repository

import com.learn2hire.app.data.local.StudyContent
import com.learn2hire.app.data.model.Subject
import com.learn2hire.app.data.model.Topic

class StudyRepositoryImpl : StudyRepository {

    override fun getSubjects(): List<Subject> {
        return StudyContent.subjects
    }

    override fun getSubject(subjectId: String): Subject? {
        return StudyContent.getSubject(subjectId)
    }

    override fun getTopics(subjectId: String): List<Topic> {
        return StudyContent.getTopicsForSubject(subjectId)
    }

    override fun getTopic(topicId: String): Topic? {
        return StudyContent.getTopic(topicId)
    }
}