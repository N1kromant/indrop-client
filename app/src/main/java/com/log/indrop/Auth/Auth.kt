package com.log.indrop.Auth

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.log.indrop.ui.theme2.InkTheme
import com.log.indrop.ui.theme2.bgColor
import com.log.indrop.ui.theme2.fgColor
import com.log.indrop.ui.theme2.pink
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class Auth: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //AniContent()
            AuthScreen()
        }
    }
}

const val LOGIN = false
const val REGISTRATION = true

const val LOGIN_PAGE = 0
const val REGISTRATION_PAGE = 1

const val LOGIN_BUTTON_TEXT = "Вход"
const val REGISTRATION_BUTTON_TEXT = "Регистрация"

const val TOP_BAR_HEIGHT = 125



@OptIn(DelicateCoroutinesApi::class)
@Preview
@Composable
fun AuthScreen() {
    val theme = MaterialTheme.colorScheme
    val isClicked = remember { mutableStateOf<Boolean>(false) }
    val currentPage = remember { mutableStateOf<String>("Form") }
    val formHeight = remember { mutableStateOf<Dp>(getDeviceHeight()) }
    val visible = true
    val authLogin = remember { mutableStateOf<String>("") }
    val authPassword = remember { mutableStateOf<String>("") }
    val regName = remember { mutableStateOf("") }
    val regLogin = remember { mutableStateOf("") }
    val regPassword = remember { mutableStateOf("") }
    val regRepeatPassword = remember { mutableStateOf("") }
    val switchState = remember { mutableStateOf(false) }


//     val formSize = remember { mutableStateOf() }

    InkTheme {
        Column() {
            if (!isClicked.value) {
                TopBar(TOP_BAR_HEIGHT)
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally() + expandHorizontally(expandFrom = Alignment.End) +
                            fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                            + shrinkHorizontally() + fadeOut(),
                ) {
                    Form(authLogin,
                        authPassword,
                        regName,
                        regLogin,
                        regPassword,
                        regRepeatPassword,
                        switchState,
                        isClicked,
                        Modifier.height(formHeight.value))
                    { isNewAcc ->
                        //TODO:Анимация входа
                        isClicked.value = isNewAcc
                        if(isNewAcc)
                        {
                        }
                        else{
                        }

                    }
                }
            } else {
                TopBar(TOP_BAR_HEIGHT)
                AnimatedVisibility(
                    visible = visible,
                    enter = slideInHorizontally() + expandHorizontally(expandFrom = Alignment.End) +
                            fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                            + shrinkHorizontally() + fadeOut(),
                ) {
                    Box (
                        modifier = Modifier
                            .background(theme.background)
                            .fillMaxSize()
                            .clip(
                                shape = RoundedCornerShape(20.dp, 20.dp)
                            )
                    ) {
                        Column {
                            Loading(modifier = Modifier)
                            SubmitButton("Назад", 32.sp) {
                                isClicked.value = !isClicked.value
                            }
                        }
                    }
                }
            }
        }
    }
}
@OptIn(DelicateCoroutinesApi::class)
@Composable
fun AuthScreen(isLoggedIn: (String, String) -> Unit, isRegisterIn: (String, String, String) -> Unit) {
    val theme = MaterialTheme.colorScheme
    val isClicked = remember { mutableStateOf<Boolean>(false) }
    val currentPage = remember { mutableStateOf<String>("Form") }
    val formHeight = remember { mutableStateOf<Dp>(getDeviceHeight()) }

    val authLogin = remember { mutableStateOf<String>("") }
    val authPassword = remember { mutableStateOf<String>("") }

    val regName = remember { mutableStateOf("") }
    val regLogin = remember { mutableStateOf("") }
    val regPassword = remember { mutableStateOf("") }
    val regRepeatPassword = remember { mutableStateOf("") }

    val switchState = remember { mutableStateOf(false) }


    val visible = true

//     val formSize = remember { mutableStateOf() }

    Column() {
        if (!isClicked.value) {
            TopBar(TOP_BAR_HEIGHT)
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally() + expandHorizontally(expandFrom = Alignment.End) +
                        fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                        + shrinkHorizontally() + fadeOut(),
            ) {
                Form(authLogin,
                    authPassword,
                    regName,
                    regLogin,
                    regPassword,
                    regRepeatPassword,
                    switchState,
                    isClicked,
                    Modifier.height(formHeight.value))
                { isNewAcc ->
                    //TODO:Анимация входа
                    isClicked.value = isNewAcc
                    if(switchState.value) {
                        isRegisterIn(regLogin.value,
                            regPassword.value,
                            regName.value,
                        )
                    }
                    else {
                        isLoggedIn(authLogin.value, authPassword.value)
                    }
                }
            }
        }
        else {
            TopBar(TOP_BAR_HEIGHT)
            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally() + expandHorizontally(expandFrom = Alignment.End) +
                        fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth })
                        + shrinkHorizontally() + fadeOut(),
            ) {
            LoadingForm(isClicked, Modifier)
            }
        }
    }
}

@Composable
fun Loading(modifier: Modifier) {
    Column (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(modifier = modifier.size(40.dp))
    }
}

@Composable
fun TopBar(height: Int) {
    Box(
        modifier = Modifier
            .height(height.dp)
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(listOf(fgColor, bgColor))
            )
    )
}
@Composable
fun TopBar(height: Int, modifier: Modifier) {
    Box(
        modifier = modifier
            .height(height.dp)
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(listOf(fgColor, bgColor))
            )
    )
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Form(authLogin: MutableState<String>,
         authPassword: MutableState<String>,
         regName: MutableState<String>,
         regLogin: MutableState<String>,
         regPassword: MutableState<String>,
         regRepeatPassword: MutableState<String>,
         switchState: MutableState<Boolean>,
         isClicked: MutableState<Boolean>,
         modifier: Modifier,
         onClick: (isNewAcc: Boolean) -> Unit) {
    val theme = MaterialTheme.colorScheme


    val submitButtonText = remember { mutableStateOf("Войти") }

    val formPages = rememberPagerState ( pageCount = { 2 } )
    val isRegistration = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    var isSwitchClicked by remember { mutableStateOf(false) }

    Box (
        modifier = modifier
            .background(theme.background)
            .fillMaxSize()
            .clip(
                shape = RoundedCornerShape(20.dp, 20.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .background(theme.primaryContainer)
                .fillMaxSize()
        ) {
            Column {
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                )
                Row {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                LaunchedEffect(formPages.currentPage) {
                                    switchState.value = formPages.currentPage == 1  // Если страница 1 — переключатель в true, иначе в false
                                    changeSubmitButtonText(formPages.currentPage, submitButtonText)  // Изменяем текст кнопки отправки
                                }
                                HorizontalPager(
                                    state = formPages,
                                    Modifier.height(getDeviceHeight()/2)
                                ) { page ->

                                    when (page) {
                                        0 -> {
                                            AuthForm(
                                                authLogin,
                                                authPassword
                                            )

                                        }
                                        1 -> {
                                            RegistrationForm(
                                                regName,
                                                regLogin,
                                                regPassword,
                                                regRepeatPassword
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier
                                    .height(20.dp)
                                    .fillMaxWidth())
                                SubmitButton(submitButtonText.value, 28.sp) {

                                    onClick(isRegistration.value)
                                    isClicked.value = !isClicked.value
                                }
                                IsNewUserSwitch(switchState) {

                                    coroutineScope.launch {
                                        if (formPages.currentPage == 0) {

                                            changeSubmitButtonText(submitButtonText)
                                            formPages.animateScrollToPage(
                                                1,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            )
                                        } else {
                                            changeSubmitButtonText(submitButtonText)
                                            formPages.animateScrollToPage(
                                                0,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            )
                                        }
                                    }

                                    isSwitchClicked = true
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingForm(
         isClicked: MutableState<Boolean>,
         modifier: Modifier) {
    val theme = MaterialTheme.colorScheme

    Box (
        modifier = modifier
            .background(theme.background)
            .fillMaxSize()
            .clip(
                shape = RoundedCornerShape(20.dp, 20.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .background(theme.primaryContainer)
                .fillMaxSize()
        ) {
            Column {
                Spacer(
                    modifier = Modifier
                        .height(20.dp)
                        .fillMaxWidth()
                )
                Row {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Column {
                                Box(
                                    Modifier.height(getDeviceHeight()/2),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Loading(
                                        modifier = Modifier.scale(2f) // Увеличиваем элемент Loading в 3 раза
                                    )
                                }
                                Spacer(modifier = Modifier
                                    .height(20.dp)
                                    .fillMaxWidth())
                                SubmitButton("Назад", 28.sp) {
                                    isClicked.value = !isClicked.value
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun changeSubmitButtonText(pageCode: Int, text: MutableState<String>) {
    if (pageCode == REGISTRATION_PAGE) text.value = REGISTRATION_BUTTON_TEXT else text.value = LOGIN_BUTTON_TEXT
}
fun changeSubmitButtonText(text: MutableState<String>) {
    if (text.value == LOGIN_BUTTON_TEXT) text.value = REGISTRATION_BUTTON_TEXT else text.value = LOGIN_BUTTON_TEXT
}

@Composable
fun AuthForm(login: MutableState<String>, password: MutableState<String>) {
    Row {
        Spacer(
            modifier = Modifier
                .width(20.dp)
                .fillMaxHeight()
        )
        Column (
            Modifier
                .weight(0.5f)
        ) {
            Text(
                text = "С возвращением!",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()

            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )
            CustomTextField("Логин", login)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            CustomTextField("Пароль", password)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )
    }
    }
}

@Composable
fun RegistrationForm(name: MutableState<String>, login: MutableState<String>, password: MutableState<String>, passwordRepeat: MutableState<String>) {
    Row {
        Column(
            Modifier
                .weight(0.5f)
        ) {
            Text(
                text = "Добро пожаловать!",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()

            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )
            CustomTextField("Отображемое имя", name)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            CustomTextField("Логин", login)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            CustomTextField("Пароль", password)
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            CustomTextField("Повторите пароль", passwordRepeat)
        }
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(20.dp)
//        )
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(20.dp),
        )
    }
}

@Composable
fun SubmitButton(submitButtonText: String, fontSize: TextUnit, onClick: () -> Unit) {
    Column (
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedButton(
            onClick = { onClick() },
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, Color.White),
            modifier = Modifier
                .fillMaxWidth(0.96f),
        ) {
            Text(submitButtonText,
                fontSize = fontSize,
                fontWeight = FontWeight.Normal
            )
        }

    }
}
@Composable
fun SubmitButton(submitButtonText: MutableState<String>, fontSize: TextUnit, onClick: () -> Unit) {
    Column (
        Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedButton(
            onClick = { onClick() },
            shape = RoundedCornerShape(50),
            border = BorderStroke(1.dp, Color.White),
            modifier = Modifier
                .fillMaxWidth(0.96f),
        ) {
            Text(submitButtonText.value,
                fontSize = fontSize,
                fontWeight = FontWeight.Light
            )
        }

    }
}

@Preview
@Composable
fun SubmitButtonPreview() {
    InkTheme {
        SubmitButton("Принять", 18.sp) {}
    }

}

@Composable
fun IsNewUserSwitch(checkedState: MutableState<Boolean>, onClick: (isNewAcc: Boolean) -> Unit) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Новый пользователь?",
            color = colors.onPrimary
        )
        Switch(
            checked = checkedState.value,
            onCheckedChange = {
                onClick(it)
                checkedState.value = it
            },
            modifier = Modifier,
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = pink, // Цвет переключателя при выключенном состоянии
                uncheckedTrackColor = colors.onPrimary, // Цвет фона переключателя при выключенном состоянии
                uncheckedBorderColor = colors.tertiary,
                checkedThumbColor = Color.White,
                checkedTrackColor = pink,
                checkedBorderColor = pink,
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(text: String, content: () -> Unit?) {
    val textState = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = textState.value,
        onValueChange = { newText -> textState.value = newText },
        modifier = Modifier
            .fillMaxWidth(),
        label = { Text(text) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                // Выполнить действие при нажатии на кнопку "Готово" на клавиатуре
                keyboardController?.hide() // Скрыть клавиатуру
                content()
            }
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent
        ),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(text: String) {
    val textState = remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = textState.value,
        onValueChange = { newText -> textState.value = newText },
        modifier = Modifier
            .fillMaxWidth(),
        label = { Text(text) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                // Выполнить действие при нажатии на кнопку "Готово" на клавиатуре
                keyboardController?.hide() // Скрыть клавиатуру
            }
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent
        ),
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(text: String, textState: MutableState<String>) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = textState.value,
        onValueChange = { newText -> textState.value = newText },
        modifier = Modifier
            .fillMaxWidth(),
        label = { Text(text) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                // Выполнить действие при нажатии на кнопку "Готово" на клавиатуре
                keyboardController?.hide() // Скрыть клавиатуру
            }
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent
        ),
    )
}

fun getDeviceHeight(): Dp {
    val screenHeightPx = Resources.getSystem().displayMetrics.heightPixels
    val density = Resources.getSystem().displayMetrics.density
    return (screenHeightPx / density).dp
}
