require: slotfilling/slotFilling.sc
  module = sys.zb-common
  
require: patterns.sc
  module = sys.zb-common
  
require: changeAccountDetails.sc

theme: /

    state: Start
        q!: $regex</start>
        a: Здравствуйте! Я Инара, ваш помощник.
        random:
            a: Что вы хотите узнать?
            a: По какому вопросу вы обращаетесь?
            a: Задайте Ваш вопрос
            a: Скажите свой вопрос
        # заглушки
        event: noMatch || onlyThisState = false, toState = "/NoMatch" 
        intent: /CallTheOperator || onlyThisState = false, toState = "/NoMatch" 
        intent: /ChangeAccountDetails || onlyThisState = false, toState = "/AccountDetails" 
        intent: /ChangeTenants || onlyThisState = false, toState = "/Tenants" 

    state: Hello
        intent!: /привет
        a: Привет привет

    state: Bye
        intent!: /пока
        a: Пока пока

    state: NoMatch
        event!: noMatch
        # a: Я не понял. Вы сказали: {{$request.query}}
        a: Не поняла Вас. Перевожу на оператора

    state: CallTheOperator
        a: Хорошо. Перевожу на оператора

    state: AccountDetails
        a: Хорошо. Давайте обсудим способы смены реквизитов
        
    state: Tenants
        a: Хорошо. Давайте сменим количество проживающих

    state: Match
        event!: match
        a: {{$context.intent.answer}}
        
    state: repeat || noContext = true
        q!:  * ( повтор* / что / еще раз* / ещё раз*) *
        go!: {{$session.contextPath}}