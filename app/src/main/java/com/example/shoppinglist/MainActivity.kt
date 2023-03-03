package com.example.shoppinglist

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.shoppinglist.ui.theme.ShoppingListTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<ShoppingListViewModel>()

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        viewModel.setItems(sharedPreferences.getStringSet(
            ShoppingListViewModel.ITEMS, setOf<String>()
        )?.toList() ?: listOf())

        setContent {
            ShoppingListTheme {
                Scaffold(
                    topBar = {
                         RemoveAll(
                             onClick = { viewModel.removeAll(sharedPreferences) },
                             horizontalAlignment = Alignment.CenterHorizontally,
                             modifier = Modifier.fillMaxWidth()
                         )
                    },
                    bottomBar = {
                        Adder({ item -> viewModel.addItem(item, sharedPreferences) },
                            modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp)
                                .height(IntrinsicSize.Max))
                    }
                ) {
                    Row {
                        Spacer(modifier = Modifier.width(8.dp))

                        List(
                            viewModel,
                            modifier = Modifier
                                .padding(it)
                                .fillMaxWidth(),
                            onLongPress = { index -> viewModel.deleteAt(index, sharedPreferences) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RemoveAll(onClick: () -> Unit, horizontalAlignment: Alignment.Horizontal, modifier: Modifier) {
    Column(horizontalAlignment = horizontalAlignment, modifier = modifier) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red,
                contentColor = Color.White)
        ) {
            Text("Remove All")
        }
    }
}

@Composable
fun List(viewModel: ShoppingListViewModel, modifier: Modifier, onLongPress: (index: Int) -> Unit) {
    val items: List<String> by viewModel.items.observeAsState(listOf())

    LazyColumn(modifier = modifier) {
        itemsIndexed(items) {index, item ->
            Column(Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongPress(index) },
                )
            }) {
                Text(item, style = MaterialTheme.typography.h4)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun Adder(onClick: (String) -> Unit, modifier: Modifier) {
    Row(modifier = modifier) {
        var newItem by remember {
            mutableStateOf("")
        }

        TextField(
            value = newItem,
            onValueChange = {
                newItem = it
            },
            label = { Text("New Item") }
        )

        Spacer(Modifier.width(8.dp))
        Spacer(Modifier.weight(1f))
        
        Button(
            onClick = {
                onClick(newItem)
                newItem = ""
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Add")
        }
    }
}