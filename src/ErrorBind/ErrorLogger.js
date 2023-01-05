
function SetAnwserToUser($context){
    var answers = [
        "Что-то пошло не так.",
        "Произошла ошибка. Пожалуйста, повторите запрос позже.",
        "Все сломалось. Попробуйте еще раз."
    ];
    var randomAnswer = answers[$reactions.random(answers.length)];
    $reactions.answer(randomAnswer);
    
}
function SendErrorMessage(place, message){
    // отправить информацию по ошибке в бота
    // https://api.telegram.org/bot<token>/sendMessage?chat_id=<chat_id>&text=Всем привет!
    var token = $secrets.get("InaraErrorBotToken", "Токен не найден");
    var chat_id = $env.get("InaraSeviceAddress", "Чат не найден");
    var url = "https://api.telegram.org/bot"+token+"/sendMessage?chat_id="+chat_id+"&text=Произошла ошибка в боте "+place;
    var response =  $http.query(url, {method: "GET"
    //,timeout: 20000        // таймаут выполнения запроса в мс
            })
    
} 

bind("onScriptError",function($context){
    SetAnwserToUser($context);
    $analytics.setSessionData("Ошибка", "onScriptError: " +  $context.exception.message)
    log($context.exception.message);
    
    SendErrorMessage("onScriptError: ",  $context.exception.message);
//    $mail.sendMessage("dladzina@alseco.kz", "Ошбика бота Телефоны сотрудников", "onScriptError: " +  $context.exception.message);
});

bind("onDialogError", function($context) {

    SetAnwserToUser($context);
    $analytics.setSessionData("Ошибка", "onDialogError: " + $context.exception.message)
    log($context.exception.message);
    SendErrorMessage("onDialogError: ",  $context.exception.message);
//    $mail.sendMessage("dladzina@alseco.kz", "Ошбика бота Телефоны сотрудников", "onDialogError: " +  $context.exception.message);
});

bind("onAnyError", function($context) {
    SetAnwserToUser($context);
    $analytics.setSessionData("Ошибка", "onAnyError: " + $context.exception.message)
    log($context.exception.message);
    SendErrorMessage("onAnyError: ",  $context.exception.message);
//    $mail.sendMessage("dladzina@alseco.kz", "Ошбика бота Телефоны сотрудников", "onAnyError: " +  $context.exception.message);
});
