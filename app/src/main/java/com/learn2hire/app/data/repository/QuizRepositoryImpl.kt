package com.learn2hire.app.data.repository

import com.learn2hire.app.data.local.SampleQuizData
import com.learn2hire.app.data.model.QuizQuestion

class QuizRepositoryImpl : QuizRepository {

    override fun getQuestionsForTopic(
        topicId: String
    ): List<QuizQuestion> {
        return SampleQuizData.getQuestionsForTopic(topicId)
    }
}