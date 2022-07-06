package li.dic.tourphone.domain

data class Message (
    var name: String = "Слушатель",
    var text: String = "",
    var type: MessageType = MessageType.INCOMING
)