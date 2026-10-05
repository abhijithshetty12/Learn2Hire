package com.learn2hire.app.data.local

import com.learn2hire.app.data.model.QuizQuestion

object SampleQuizData {

    val questions = listOf(
        QuizQuestion(
            id = "ml1_q1",
            topicId = "ml1",
            question = "What is the main purpose of Machine Learning?",
            options = listOf(
                "To manually program every possible decision",
                "To allow systems to learn patterns from data and make predictions or decisions",
                "To replace databases with algorithms",
                "To design only user interfaces"
            ),
            correctAnswerIndex = 1,
            explanation = "Machine Learning enables systems to learn patterns from data and use those patterns to make predictions or decisions."
        ),

        QuizQuestion(
            id = "ml1_q2",
            topicId = "ml1",
            question = "Which type of Machine Learning learns from labeled training data?",
            options = listOf(
                "Supervised Learning",
                "Unsupervised Learning",
                "Reinforcement Learning",
                "Random Learning"
            ),
            correctAnswerIndex = 0,
            explanation = "Supervised Learning uses labeled training data, where examples include the expected output."
        ),

        QuizQuestion(
            id = "ml1_q3",
            topicId = "ml1",
            question = "Which type of Machine Learning is commonly used to discover patterns in unlabeled data?",
            options = listOf(
                "Supervised Learning",
                "Unsupervised Learning",
                "Reinforcement Learning",
                "Compiled Learning"
            ),
            correctAnswerIndex = 1,
            explanation = "Unsupervised Learning works with unlabeled data to discover patterns or structure."
        ),

        QuizQuestion(
            id = "ml1_q4",
            topicId = "ml1",
            question = "Which learning type involves learning through rewards and penalties?",
            options = listOf(
                "Supervised Learning",
                "Unsupervised Learning",
                "Reinforcement Learning",
                "Static Learning"
            ),
            correctAnswerIndex = 2,
            explanation = "Reinforcement Learning learns behavior through interaction, rewards, and penalties."
        ),

        QuizQuestion(
            id = "ml1_q5",
            topicId = "ml1",
            question = "What is training data used for in Machine Learning?",
            options = listOf(
                "To provide examples from which a model can learn patterns",
                "Only to store application passwords",
                "To draw the user interface",
                "To compile Android code"
            ),
            correctAnswerIndex = 0,
            explanation = "Training data provides the examples and information from which a machine learning model learns."
        ),

        QuizQuestion(
            id = "ml2_q1",
            topicId = "ml2",
            question = "What does a classification model predict?",
            options = listOf(
                "A continuous numerical value",
                "A discrete class label",
                "A database table",
                "A programming language"
            ),
            correctAnswerIndex = 1,
            explanation = "Classification predicts discrete categories or class labels."
        ),

        QuizQuestion(
            id = "ml2_q2",
            topicId = "ml2",
            question = "Which of the following is a classification problem?",
            options = listOf(
                "Predicting tomorrow's temperature",
                "Predicting the price of a house",
                "Determining whether an email is spam or not spam",
                "Estimating monthly sales revenue"
            ),
            correctAnswerIndex = 2,
            explanation = "Spam versus not-spam is a discrete class-label prediction, so it is a classification problem."
        ),

        QuizQuestion(
            id = "ml2_q3",
            topicId = "ml2",
            question = "In classification, what is a class label?",
            options = listOf(
                "The category assigned to an input",
                "The number of training epochs",
                "A continuous measurement",
                "The name of a database"
            ),
            correctAnswerIndex = 0,
            explanation = "A class label represents the discrete category that the model predicts."
        ),

        QuizQuestion(
            id = "ml2_q4",
            topicId = "ml2",
            question = "Which example best represents binary classification?",
            options = listOf(
                "Predicting house price",
                "Predicting temperature",
                "Predicting pass or fail",
                "Predicting exact sales revenue"
            ),
            correctAnswerIndex = 2,
            explanation = "Pass or fail contains two discrete classes and is therefore a binary classification example."
        ),

        QuizQuestion(
            id = "ml2_q5",
            topicId = "ml2",
            question = "What does a trained classification model do with new input data?",
            options = listOf(
                "Predicts its class",
                "Deletes the input",
                "Converts it into a database",
                "Always returns a continuous value"
            ),
            correctAnswerIndex = 0,
            explanation = "After training, a classification model uses learned patterns to predict a class for new data."
        ),

        QuizQuestion(
            id = "ml3_q1",
            topicId = "ml3",
            question = "What type of output does regression predict?",
            options = listOf(
                "A continuous numerical value",
                "Only a true or false label",
                "A database document",
                "A fixed class name"
            ),
            correctAnswerIndex = 0,
            explanation = "Regression is used to predict continuous numerical values."
        ),

        QuizQuestion(
            id = "ml3_q2",
            topicId = "ml3",
            question = "Which of the following is a regression problem?",
            options = listOf(
                "Spam or not spam",
                "Pass or fail",
                "Predicting a house price",
                "Positive or negative sentiment"
            ),
            correctAnswerIndex = 2,
            explanation = "House price is a continuous numerical value, making it a regression problem."
        ),

        QuizQuestion(
            id = "ml3_q3",
            topicId = "ml3",
            question = "In a regression problem, what are features?",
            options = listOf(
                "Input variables used to make a prediction",
                "Only the final predicted value",
                "Database passwords",
                "Class labels only"
            ),
            correctAnswerIndex = 0,
            explanation = "Features are the input variables that a regression model uses to predict a numerical value."
        ),

        QuizQuestion(
            id = "ml3_q4",
            topicId = "ml3",
            question = "Which output is most suitable for a regression model?",
            options = listOf(
                "Disease present or absent",
                "Tomorrow's temperature",
                "Spam or not spam",
                "Positive or negative"
            ),
            correctAnswerIndex = 1,
            explanation = "Temperature is a continuous numerical quantity and is suitable for regression."
        ),

        QuizQuestion(
            id = "ml3_q5",
            topicId = "ml3",
            question = "What is the main difference between regression and classification?",
            options = listOf(
                "Regression predicts continuous values while classification predicts discrete labels",
                "Regression never uses data",
                "Classification predicts only numbers",
                "They are exactly the same task"
            ),
            correctAnswerIndex = 0,
            explanation = "Regression predicts continuous numerical values, whereas classification predicts discrete class labels."
        ),

        QuizQuestion(
            id = "nb1",
            topicId = "ml4",
            question = "Naïve Bayes is primarily based on which theorem?",
            options = listOf(
                "Pythagorean theorem",
                "Bayes' theorem",
                "Central Limit theorem",
                "No Free Lunch theorem"
            ),
            correctAnswerIndex = 1,
            explanation = "Naïve Bayes uses Bayes' theorem to calculate the probability of a class given observed features."
        ),

        QuizQuestion(
            id = "nb2",
            topicId = "ml4",
            question = "What is the 'naïve' assumption in Naïve Bayes?",
            options = listOf(
                "All classes have equal probability",
                "Training examples are independent",
                "Features are conditionally independent given the class",
                "The dataset contains no missing values"
            ),
            correctAnswerIndex = 2,
            explanation = "Naïve Bayes assumes that features are conditionally independent when the class is known."
        ),

        QuizQuestion(
            id = "nb3",
            topicId = "ml4",
            question = "Which is a common application of Naïve Bayes?",
            options = listOf(
                "Spam detection",
                "Image rendering",
                "Database indexing",
                "Operating system scheduling"
            ),
            correctAnswerIndex = 0,
            explanation = "Naïve Bayes is commonly used for text classification tasks such as spam detection."
        ),

        QuizQuestion(
            id = "nb4",
            topicId = "ml4",
            question = "What does prior probability represent?",
            options = listOf(
                "Probability after observing evidence",
                "Probability of a class before observing current evidence",
                "Probability that every feature is independent",
                "Probability of model failure"
            ),
            correctAnswerIndex = 1,
            explanation = "The prior represents what we know about the probability of a class before considering the current evidence."
        ),

        QuizQuestion(
            id = "nb5",
            topicId = "ml4",
            question = "Naïve Bayes is mainly used for which type of task?",
            options = listOf(
                "Classification",
                "Sorting",
                "Rendering",
                "Compression"
            ),
            correctAnswerIndex = 0,
            explanation = "Naïve Bayes is primarily a classification algorithm."
        ),

        QuizQuestion(
            id = "wad1_q1",
            topicId = "wad1",
            question = "What is the primary role of a client in a client-server system?",
            options = listOf(
                "Provide the user interface and send requests",
                "Store every database permanently",
                "Replace the network",
                "Compile the server operating system"
            ),
            correctAnswerIndex = 0,
            explanation = "The client provides the user-facing interface and sends requests to a server."
        ),

        QuizQuestion(
            id = "wad1_q2",
            topicId = "wad1",
            question = "What is a primary responsibility of the server?",
            options = listOf(
                "Process requests and return responses",
                "Only display buttons to the user",
                "Only store images on the client",
                "Replace HTTP"
            ),
            correctAnswerIndex = 0,
            explanation = "The server processes incoming requests, performs required logic, and returns responses."
        ),

        QuizQuestion(
            id = "wad1_q3",
            topicId = "wad1",
            question = "Which protocol is commonly used for communication between web clients and servers?",
            options = listOf(
                "HTTP",
                "Kotlin",
                "SQL",
                "HTML"
            ),
            correctAnswerIndex = 0,
            explanation = "HTTP is commonly used for request-response communication between web clients and servers."
        ),

        QuizQuestion(
            id = "wad1_q4",
            topicId = "wad1",
            question = "Where is business logic commonly processed in a client-server web application?",
            options = listOf(
                "Server",
                "Keyboard",
                "Display panel",
                "CSS file only"
            ),
            correctAnswerIndex = 0,
            explanation = "The server commonly processes application business logic and interacts with databases."
        ),

        QuizQuestion(
            id = "wad1_q5",
            topicId = "wad1",
            question = "What normally follows a request sent by a client to a server?",
            options = listOf(
                "A response from the server",
                "A new programming language",
                "Deletion of the client",
                "A hardware upgrade"
            ),
            correctAnswerIndex = 0,
            explanation = "In the request-response model, the server processes the client's request and sends back a response."
        ),

        QuizQuestion(
            id = "wad2_q1",
            topicId = "wad2",
            question = "What does Node.js allow developers to do?",
            options = listOf(
                "Run JavaScript outside the browser",
                "Use JavaScript only for CSS",
                "Replace every database with HTML",
                "Run only Android XML"
            ),
            correctAnswerIndex = 0,
            explanation = "Node.js provides a runtime for executing JavaScript outside the web browser."
        ),

        QuizQuestion(
            id = "wad2_q2",
            topicId = "wad2",
            question = "What is Express primarily used for with Node.js?",
            options = listOf(
                "Simplifying web server development and HTTP handling",
                "Training machine learning models",
                "Creating database hardware",
                "Designing image pixels"
            ),
            correctAnswerIndex = 0,
            explanation = "Express is a Node.js framework that simplifies routing, middleware, and HTTP request handling."
        ),

        QuizQuestion(
            id = "wad2_q3",
            topicId = "wad2",
            question = "What is a route in an Express application?",
            options = listOf(
                "A definition that handles requests for a particular path or endpoint",
                "A database password",
                "A CSS animation",
                "A machine learning label"
            ),
            correctAnswerIndex = 0,
            explanation = "Routes connect HTTP requests and paths to application handling logic."
        ),

        QuizQuestion(
            id = "wad2_q4",
            topicId = "wad2",
            question = "What is middleware in an Express application?",
            options = listOf(
                "Logic that runs during request processing before the final response",
                "A replacement for JavaScript",
                "A database table only",
                "A browser image format"
            ),
            correctAnswerIndex = 0,
            explanation = "Middleware participates in the request-processing pipeline and can perform reusable operations before a response is produced."
        ),

        QuizQuestion(
            id = "wad2_q5",
            topicId = "wad2",
            question = "Which concept is commonly implemented using Node.js and Express?",
            options = listOf(
                "REST API",
                "Kleene Star",
                "Regression output",
                "Finite automaton state"
            ),
            correctAnswerIndex = 0,
            explanation = "Node.js and Express are commonly used to create HTTP services and REST APIs."
        ),

        QuizQuestion(
            id = "wad3_q1",
            topicId = "wad3",
            question = "How does SQL commonly organize data?",
            options = listOf(
                "Structured tables",
                "Only image files",
                "Finite automata",
                "Unlabeled clusters"
            ),
            correctAnswerIndex = 0,
            explanation = "SQL databases commonly organize structured data into tables."
        ),

        QuizQuestion(
            id = "wad3_q2",
            topicId = "wad3",
            question = "Which characteristic is commonly associated with NoSQL databases?",
            options = listOf(
                "Flexible data models",
                "Only fixed relational tables",
                "No ability to store data",
                "Mandatory HTML schemas"
            ),
            correctAnswerIndex = 0,
            explanation = "NoSQL systems commonly support flexible models such as documents and key-value structures."
        ),

        QuizQuestion(
            id = "wad3_q3",
            topicId = "wad3",
            question = "Which structure is commonly associated with document-oriented NoSQL databases?",
            options = listOf(
                "Documents",
                "CPU registers",
                "DFA states",
                "CSS selectors only"
            ),
            correctAnswerIndex = 0,
            explanation = "Document-oriented NoSQL databases store information as documents."
        ),

        QuizQuestion(
            id = "wad3_q4",
            topicId = "wad3",
            question = "What does a database schema describe?",
            options = listOf(
                "The structure and organization of stored data",
                "Only the screen color",
                "A network cable",
                "A machine learning reward"
            ),
            correctAnswerIndex = 0,
            explanation = "A database schema defines how stored data is structured and organized."
        ),

        QuizQuestion(
            id = "wad3_q5",
            topicId = "wad3",
            question = "Which database type is strongly associated with structured tables and relationships?",
            options = listOf(
                "SQL",
                "NoSQL only",
                "HTTP",
                "Express"
            ),
            correctAnswerIndex = 0,
            explanation = "SQL databases use structured tables and provide strong support for relationships between data."
        ),

        QuizQuestion(
            id = "atcd1_q1",
            topicId = "atcd1",
            question = "What are the main components used by a finite automaton to process input?",
            options = listOf(
                "States and transitions",
                "HTML and CSS",
                "Rows and columns only",
                "Rewards and penalties"
            ),
            correctAnswerIndex = 0,
            explanation = "A finite automaton processes symbols by moving between states according to transitions."
        ),

        QuizQuestion(
            id = "atcd1_q2",
            topicId = "atcd1",
            question = "What does the alphabet of a finite automaton represent?",
            options = listOf(
                "The set of input symbols",
                "The set of database tables",
                "Only the final state",
                "The number of servers"
            ),
            correctAnswerIndex = 0,
            explanation = "The alphabet is the finite set of symbols that may appear in input strings."
        ),

        QuizQuestion(
            id = "atcd1_q3",
            topicId = "atcd1",
            question = "Where does a finite automaton begin processing an input string?",
            options = listOf(
                "Initial state",
                "Any random state",
                "Final state only",
                "Database state"
            ),
            correctAnswerIndex = 0,
            explanation = "Processing starts from the designated initial state."
        ),

        QuizQuestion(
            id = "atcd1_q4",
            topicId = "atcd1",
            question = "What is the purpose of a final or accepting state?",
            options = listOf(
                "To indicate acceptance when input processing ends in it",
                "To start every transition",
                "To store a web request",
                "To define a database schema"
            ),
            correctAnswerIndex = 0,
            explanation = "A string is accepted when processing finishes in an accepting or final state."
        ),

        QuizQuestion(
            id = "atcd1_q5",
            topicId = "atcd1",
            question = "Finite automata are used to recognize which class of languages?",
            options = listOf(
                "Regular languages",
                "Only programming languages",
                "Context-sensitive languages only",
                "Natural languages only"
            ),
            correctAnswerIndex = 0,
            explanation = "Finite automata recognize regular languages."
        ),

        QuizQuestion(
            id = "atcd2_q1",
            topicId = "atcd2",
            question = "In a DFA, how many transitions are defined for each state and input symbol?",
            options = listOf(
                "Exactly one",
                "Always zero",
                "At least three",
                "Any number including several choices"
            ),
            correctAnswerIndex = 0,
            explanation = "A DFA has exactly one transition for every state-input-symbol combination."
        ),

        QuizQuestion(
            id = "atcd2_q2",
            topicId = "atcd2",
            question = "What can an NFA have for the same state and input symbol?",
            options = listOf(
                "Multiple possible transitions",
                "Exactly one transition only in every case",
                "No states",
                "Only database relationships"
            ),
            correctAnswerIndex = 0,
            explanation = "An NFA can have multiple possible transitions for a state and input symbol."
        ),

        QuizQuestion(
            id = "atcd2_q3",
            topicId = "atcd2",
            question = "Which automaton may use epsilon transitions?",
            options = listOf(
                "NFA",
                "DFA",
                "Both must always use them",
                "Neither can have states"
            ),
            correctAnswerIndex = 0,
            explanation = "An NFA may include epsilon transitions that do not consume an input symbol."
        ),

        QuizQuestion(
            id = "atcd2_q4",
            topicId = "atcd2",
            question = "What does a transition function describe?",
            options = listOf(
                "How an automaton moves between states for input",
                "How a database sorts rows",
                "How CSS colors are selected",
                "How a model predicts prices"
            ),
            correctAnswerIndex = 0,
            explanation = "The transition function specifies the next state or possible states for a given state and input."
        ),

        QuizQuestion(
            id = "atcd2_q5",
            topicId = "atcd2",
            question = "When is an input accepted by a DFA or NFA?",
            options = listOf(
                "When a valid computation finishes in an accepting state",
                "Whenever the input is nonempty",
                "Only when epsilon is present",
                "Whenever there is one state"
            ),
            correctAnswerIndex = 0,
            explanation = "Acceptance occurs when processing the input can finish in an accepting state."
        ),

        QuizQuestion(
            id = "atcd3_q1",
            topicId = "atcd3",
            question = "What does a regular expression describe?",
            options = listOf(
                "A regular language",
                "A database schema",
                "A continuous prediction",
                "An HTTP server"
            ),
            correctAnswerIndex = 0,
            explanation = "A regular expression is an algebraic notation used to describe a regular language."
        ),

        QuizQuestion(
            id = "atcd3_q2",
            topicId = "atcd3",
            question = "What does union represent in a regular expression?",
            options = listOf(
                "A choice between alternatives",
                "Mandatory repetition forever",
                "Database joining only",
                "A numerical prediction"
            ),
            correctAnswerIndex = 0,
            explanation = "Union represents alternatives in a regular expression."
        ),

        QuizQuestion(
            id = "atcd3_q3",
            topicId = "atcd3",
            question = "What does concatenation mean in a regular expression?",
            options = listOf(
                "Placing expressions in sequence",
                "Choosing only one alternative",
                "Deleting all symbols",
                "Sorting database rows"
            ),
            correctAnswerIndex = 0,
            explanation = "Concatenation describes symbols or expressions occurring one after another."
        ),

        QuizQuestion(
            id = "atcd3_q4",
            topicId = "atcd3",
            question = "What does the Kleene star allow?",
            options = listOf(
                "Zero or more repetitions",
                "Exactly one repetition only",
                "Exactly two states",
                "Only empty languages"
            ),
            correctAnswerIndex = 0,
            explanation = "The Kleene star represents zero or more repetitions of the expression it is applied to."
        ),

        QuizQuestion(
            id = "atcd3_q5",
            topicId = "atcd3",
            question = "How are regular expressions and finite automata related?",
            options = listOf(
                "They have equivalent expressive power for regular languages",
                "They describe unrelated language classes",
                "Finite automata cannot represent regular languages",
                "Regular expressions are only database queries"
            ),
            correctAnswerIndex = 0,
            explanation = "Regular expressions and finite automata are equivalent ways of describing regular languages."
        )

    )

    fun getQuestionsForTopic(topicId: String): List<QuizQuestion> {
        return questions.filter { it.topicId == topicId }
    }
}