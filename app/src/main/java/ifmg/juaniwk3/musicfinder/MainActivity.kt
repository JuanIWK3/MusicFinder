package ifmg.juaniwk3.musicfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.gson.JsonParser
import ifmg.juaniwk3.musicfinder.ui.theme.MusicFinderTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : ComponentActivity() {
    private val musicListState = mutableStateOf(emptyList<Music>())
    private val parser = Parser()

    private fun searchMusic(author: String) {
        if (author.isEmpty()) {
            musicListState.value = emptyList()
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            val req =
                URL("https://itunes.apple.com/search?term=$author").openConnection() as HttpsURLConnection
            val buffer = BufferedReader(InputStreamReader(req.inputStream))
            val response = buffer.readText()

            val newMusicList = parser.parse(response).slice(0..5)

            launch(Dispatchers.Main) {
                // Set the new list to musicListState
                musicListState.value = newMusicList
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicFinderTheme {
                // A surface container using the 'background' color from the theme
                Surface {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        SearchBar(onClick = { searchMusic(it) })
                        MusicList(musicListState)
                    }
                }
            }
        }
    }
}

data class Music(val trackName: String, val releaseDate: String, val collectionName: String)

@Composable
fun MusicList(musicListState: MutableState<List<Music>>) {
    val scrollState = rememberLazyListState()

    LazyColumn(
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(musicListState.value) { music ->
            MusicItem(music)
        }
    }
}

@Composable
fun MusicItem(music: Music) {
    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondary,

            ),
        border = BorderStroke(1.dp, Color(75,0,130)),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text(music.trackName).toString().substring(0, 10)
            Text(music.releaseDate.substring(0, 10))
            Text(music.collectionName)
        }
    }
}

@Composable
fun SearchBar(onClick: (author: String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Author") }
        )
        OutlinedButton(
            onClick = { onClick(text) },
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp)
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Text("Search")
        }
    }
}