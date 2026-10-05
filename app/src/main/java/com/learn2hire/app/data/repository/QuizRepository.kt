package com.learn2hire.app.data.repository

import com.learn2hire.app.data.model.QuizQuestion

interface QuizRepository {

    fun getQuestionsForTopic(topicId: String): List<QuizQuestion>
}