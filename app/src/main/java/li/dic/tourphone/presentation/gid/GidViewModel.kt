package li.dic.tourphone.presentation.gid

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import li.dic.tourphone.domain.Message

class GidViewModel : ViewModel() {

    val messages = MutableLiveData<MutableList<Message>>()
}