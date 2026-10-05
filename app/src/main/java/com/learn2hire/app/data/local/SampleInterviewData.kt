package com.learn2hire.app.data.local

import com.learn2hire.app.data.model.InterviewQuestion

object SampleInterviewData {

    val questions = listOf(

        // AI/ML ENGINEER — TECHNICAL

        InterviewQuestion(
            id = "aiml_t1",
            role = "AI/ML Engineer",
            type = "Technical",
            question = "What is the difference between supervised and unsupervised learning?",
            keyPoints = listOf(
                "labeled data",
                "unlabeled data",
                "classification",
                "clustering"
            )
        ),

        InterviewQuestion(
            id = "aiml_t2",
            role = "AI/ML Engineer",
            type = "Technical",
            question = "What is overfitting in machine learning?",
            keyPoints = listOf(
                "training data",
                "unseen data",
                "generalization",
                "regularization"
            )
        ),

        InterviewQuestion(
            id = "aiml_t3",
            role = "AI/ML Engineer",
            type = "Technical",
            question = "Explain how a decision tree works.",
            keyPoints = listOf(
                "features",
                "split",
                "nodes",
                "prediction"
            )
        ),

        InterviewQuestion(
            id = "aiml_t4",
            role = "AI/ML Engineer",
            type = "Technical",
            question = "What is the purpose of a confusion matrix?",
            keyPoints = listOf(
                "classification",
                "true positive",
                "false positive",
                "performance"
            )
        ),

        InterviewQuestion(
            id = "aiml_t5",
            role = "AI/ML Engineer",
            type = "Technical",
            question = "What is the difference between classification and regression?",
            keyPoints = listOf(
                "categorical",
                "continuous",
                "prediction",
                "supervised learning"
            )
        ),

        // FRONTEND DEVELOPER — TECHNICAL

        InterviewQuestion(
            id = "frontend_t1",
            role = "Frontend Developer",
            type = "Technical",
            question = "What is the difference between HTML, CSS, and JavaScript?",
            keyPoints = listOf(
                "structure",
                "styling",
                "behavior",
                "web page"
            )
        ),

        InterviewQuestion(
            id = "frontend_t2",
            role = "Frontend Developer",
            type = "Technical",
            question = "What is responsive web design?",
            keyPoints = listOf(
                "screen size",
                "layout",
                "media queries",
                "responsive"
            )
        ),

        InterviewQuestion(
            id = "frontend_t3",
            role = "Frontend Developer",
            type = "Technical",
            question = "What is the purpose of React state?",
            keyPoints = listOf(
                "data",
                "component",
                "update",
                "render"
            )
        ),

        InterviewQuestion(
            id = "frontend_t4",
            role = "Frontend Developer",
            type = "Technical",
            question = "What is the difference between props and state in React?",
            keyPoints = listOf(
                "props",
                "state",
                "parent",
                "component"
            )
        ),

        InterviewQuestion(
            id = "frontend_t5",
            role = "Frontend Developer",
            type = "Technical",
            question = "What is the DOM?",
            keyPoints = listOf(
                "document",
                "object",
                "tree",
                "web page"
            )
        ),

        // SOFTWARE DEVELOPER — TECHNICAL

        InterviewQuestion(
            id = "software_t1",
            role = "Software Developer",
            type = "Technical",
            question = "What is object-oriented programming?",
            keyPoints = listOf(
                "objects",
                "classes",
                "encapsulation",
                "inheritance"
            )
        ),

        InterviewQuestion(
            id = "software_t2",
            role = "Software Developer",
            type = "Technical",
            question = "What is the difference between a stack and a queue?",
            keyPoints = listOf(
                "LIFO",
                "FIFO",
                "stack",
                "queue"
            )
        ),

        InterviewQuestion(
            id = "software_t3",
            role = "Software Developer",
            type = "Technical",
            question = "What is an API?",
            keyPoints = listOf(
                "communication",
                "applications",
                "request",
                "response"
            )
        ),

        InterviewQuestion(
            id = "software_t4",
            role = "Software Developer",
            type = "Technical",
            question = "What is version control and why is it useful?",
            keyPoints = listOf(
                "changes",
                "history",
                "collaboration",
                "Git"
            )
        ),

        InterviewQuestion(
            id = "software_t5",
            role = "Software Developer",
            type = "Technical",
            question = "What is the difference between a process and a thread?",
            keyPoints = listOf(
                "process",
                "thread",
                "memory",
                "execution"
            )
        ),

        // DATA ANALYST — TECHNICAL

        InterviewQuestion(
            id = "data_t1",
            role = "Data Analyst",
            type = "Technical",
            question = "What is data cleaning and why is it important?",
            keyPoints = listOf(
                "missing values",
                "duplicates",
                "errors",
                "quality"
            )
        ),

        InterviewQuestion(
            id = "data_t2",
            role = "Data Analyst",
            type = "Technical",
            question = "What is the difference between mean, median, and mode?",
            keyPoints = listOf(
                "average",
                "middle",
                "frequency",
                "central tendency"
            )
        ),

        InterviewQuestion(
            id = "data_t3",
            role = "Data Analyst",
            type = "Technical",
            question = "Why is data visualization useful?",
            keyPoints = listOf(
                "patterns",
                "trends",
                "communication",
                "insights"
            )
        ),

        InterviewQuestion(
            id = "data_t4",
            role = "Data Analyst",
            type = "Technical",
            question = "What is SQL used for?",
            keyPoints = listOf(
                "database",
                "query",
                "data",
                "tables"
            )
        ),

        InterviewQuestion(
            id = "data_t5",
            role = "Data Analyst",
            type = "Technical",
            question = "What is correlation?",
            keyPoints = listOf(
                "relationship",
                "variables",
                "positive",
                "negative"
            )
        ),

        // COMMON HR QUESTIONS

        InterviewQuestion(
            id = "hr1",
            role = "All",
            type = "HR",
            question = "Tell me about yourself.",
            keyPoints = listOf(
                "education",
                "skills",
                "projects",
                "goals"
            )
        ),

        InterviewQuestion(
            id = "hr2",
            role = "All",
            type = "HR",
            question = "Why do you want to work with our company?",
            keyPoints = listOf(
                "company",
                "role",
                "skills",
                "growth"
            )
        ),

        InterviewQuestion(
            id = "hr3",
            role = "All",
            type = "HR",
            question = "What are your strengths?",
            keyPoints = listOf(
                "strength",
                "example",
                "skills",
                "impact"
            )
        ),

        InterviewQuestion(
            id = "hr4",
            role = "All",
            type = "HR",
            question = "Where do you see yourself in five years?",
            keyPoints = listOf(
                "career",
                "growth",
                "skills",
                "goals"
            )
        ),

        InterviewQuestion(
            id = "hr5",
            role = "All",
            type = "HR",
            question = "Why should we hire you?",
            keyPoints = listOf(
                "skills",
                "experience",
                "value",
                "role"
            )
        ),

        // COMMON BEHAVIORAL QUESTIONS

        InterviewQuestion(
            id = "behavior1",
            role = "All",
            type = "Behavioral",
            question = "Tell me about a time you worked successfully in a team.",
            keyPoints = listOf(
                "situation",
                "task",
                "action",
                "result"
            )
        ),

        InterviewQuestion(
            id = "behavior2",
            role = "All",
            type = "Behavioral",
            question = "Describe a difficult problem you solved.",
            keyPoints = listOf(
                "problem",
                "approach",
                "action",
                "result"
            )
        ),

        InterviewQuestion(
            id = "behavior3",
            role = "All",
            type = "Behavioral",
            question = "Tell me about a time you faced a tight deadline.",
            keyPoints = listOf(
                "deadline",
                "priority",
                "action",
                "result"
            )
        ),

        InterviewQuestion(
            id = "behavior4",
            role = "All",
            type = "Behavioral",
            question = "Describe a time you made a mistake and how you handled it.",
            keyPoints = listOf(
                "mistake",
                "responsibility",
                "learning",
                "improvement"
            )
        ),

        InterviewQuestion(
            id = "behavior5",
            role = "All",
            type = "Behavioral",
            question = "Tell me about a disagreement you had with a teammate.",
            keyPoints = listOf(
                "conflict",
                "communication",
                "solution",
                "result"
            )
        )
    )

    fun getQuestions(
        role: String,
        type: String
    ): List<InterviewQuestion> {

        return questions.filter { question ->

            question.type == type &&
                    (
                            question.role == role ||
                                    question.role == "All"
                            )
        }
    }
}